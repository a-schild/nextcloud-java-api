/*
 * Copyright (C) 2026 a.schild
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aarboard.nextcloud.api.calendar;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.dav.ADavCollectionHandler;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.apache.http.entity.ContentType;

/**
 * Access to the calendars of the authenticated user over
 * <a href="https://datatracker.ietf.org/doc/html/rfc4791">CalDAV</a>, below
 * {@code remote.php/dav/calendars/}: listing calendars, reading and writing
 * their entries, and creating or deleting calendars.
 * <p>
 * Entries are exchanged as raw iCalendar documents, see {@link CalendarEntry}.
 *
 * @author a.schild
 * @since 14.3
 */
public class Calendars extends ADavCollectionHandler {

    private static final String CALENDAR_ROOT = "remote.php/dav/calendars/";

    private static final QName RESOURCE_TYPE_CALENDAR = new QName(NS_CALDAV, "calendar");
    private static final QName PROP_CALENDAR_DESCRIPTION = new QName(NS_CALDAV, "calendar-description", "c");
    private static final QName PROP_CALENDAR_COLOR = new QName(NS_APPLE_ICAL, "calendar-color", "ic");
    private static final QName PROP_CALENDAR_ORDER = new QName(NS_APPLE_ICAL, "calendar-order", "ic");

    /** UTC in the basic format iCalendar and the CalDAV time-range filter use. */
    private static final DateTimeFormatter UTC_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    public Calendars(ServerConfig serverConfig) {
        super(serverConfig);
    }

    @Override
    protected String getHomeUrl() {
        return buildDavUrl(CALENDAR_ROOT + encodeSegment(getCurrentUserId())) + "/";
    }

    @Override
    protected QName getCollectionResourceType() {
        return RESOURCE_TYPE_CALENDAR;
    }

    @Override
    protected String getDataElementName() {
        return "calendar-data";
    }

    @Override
    protected String getReportNamespace() {
        return NS_CALDAV;
    }

    @Override
    protected String getMultigetElementName() {
        return "calendar-multiget";
    }

    @Override
    protected ContentType getEntryContentType() {
        return ContentType.create("text/calendar", java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Lists the calendars of the authenticated user. The scheduling inbox and
     * outbox, the trash bin and subscribed calendars are not included.
     *
     * @return the user's calendars
     */
    public List<Calendar> listCalendars() {
        Set<QName> props = new HashSet<>();
        props.add(PROP_CALENDAR_DESCRIPTION);
        props.add(PROP_CALENDAR_COLOR);
        props.add(PROP_CALENDAR_ORDER);

        return listCollections(props, Calendar::new, (calendar, resource) -> {
            calendar.setDescription(resource.getCustomPropsNS().get(PROP_CALENDAR_DESCRIPTION));
            calendar.setColor(resource.getCustomPropsNS().get(PROP_CALENDAR_COLOR));
            calendar.setOrder(resource.getCustomPropsNS().get(PROP_CALENDAR_ORDER));
        });
    }

    /**
     * Fetches every entry of a calendar, iCalendar payload included.
     *
     * @param calendarName name of the calendar, see {@link Calendar#getName()}
     * @return all entries of the calendar
     */
    public List<CalendarEntry> getCalendarEntries(String calendarName) {
        return getEntries(calendarName, CalendarEntry::new);
    }

    /**
     * Fetches the events of a calendar overlapping a time range. Recurring
     * events are matched on their expanded occurrences, but are returned as the
     * stored iCalendar document, not expanded into one entry per occurrence.
     *
     * @param calendarName name of the calendar
     * @param from         start of the range, inclusive
     * @param to           end of the range, exclusive
     * @return the matching entries
     */
    public List<CalendarEntry> getCalendarEntriesInRange(String calendarName, Instant from, Instant to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both range bounds must be set");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("The end of the range must not be before its start");
        }
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<c:calendar-query xmlns:d=\"" + NS_DAV + "\" xmlns:c=\"" + NS_CALDAV + "\">"
                + "<d:prop><d:getetag/><c:calendar-data/></d:prop>"
                + "<c:filter><c:comp-filter name=\"VCALENDAR\"><c:comp-filter name=\"VEVENT\">"
                + "<c:time-range start=\"" + UTC_TIMESTAMP.format(from) + "\""
                + " end=\"" + UTC_TIMESTAMP.format(to) + "\"/>"
                + "</c:comp-filter></c:comp-filter></c:filter>"
                + "</c:calendar-query>";

        return report(collectionUrl(calendarName), body, CalendarEntry::new);
    }

    /**
     * Fetches a single calendar entry.
     *
     * @param calendarName name of the calendar
     * @param entryName    name of the entry, or its href
     * @return the entry, or {@code null} if it does not exist
     */
    public CalendarEntry getCalendarEntry(String calendarName, String entryName) {
        return getEntry(calendarName, entryName, CalendarEntry::new);
    }

    /**
     * Creates a calendar entry, or replaces it if one of that name exists.
     *
     * @param calendarName name of the calendar
     * @param entryName    name of the entry, conventionally the UID of the
     *                     event followed by {@code .ics}
     * @param iCalendar    the iCalendar document to store
     * @return the etag of the stored entry, or {@code null} if the server did
     *         not return one
     */
    public String putCalendarEntry(String calendarName, String entryName, String iCalendar) {
        return putEntry(calendarName, entryName, iCalendar, null);
    }

    /**
     * Replaces a calendar entry only while it still carries the given etag, so
     * that a change made in the meantime is not silently overwritten.
     *
     * @param calendarName name of the calendar
     * @param entryName    name of the entry
     * @param iCalendar    the iCalendar document to store
     * @param etag         the etag the stored entry must still have
     * @return the etag of the stored entry
     * @throws org.aarboard.nextcloud.api.exception.NextcloudApiException if the
     *         entry was modified in the meantime
     */
    public String putCalendarEntry(String calendarName, String entryName, String iCalendar, String etag) {
        return putEntry(calendarName, entryName, iCalendar, etag);
    }

    /**
     * Deletes a calendar entry.
     *
     * @param calendarName name of the calendar
     * @param entryName    name of the entry, or its href
     */
    public void deleteCalendarEntry(String calendarName, String entryName) {
        deleteEntry(calendarName, entryName);
    }

    /**
     * Creates a calendar.
     *
     * @param calendarName name of the calendar as it appears in the URL
     * @param displayName  name shown in the UI, may be {@code null}
     * @param color        HTML colour code, e.g. {@code #FF0000}, may be
     *                     {@code null}
     */
    public void createCalendar(String calendarName, String displayName, String color) {
        ConnectorCommon.requireValidPathSegment(calendarName);

        StringBuilder props = new StringBuilder();
        if (displayName != null) {
            props.append("<d:displayname>").append(xmlEscape(displayName)).append("</d:displayname>");
        }
        if (color != null) {
            props.append("<ic:calendar-color>").append(xmlEscape(color)).append("</ic:calendar-color>");
        }

        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<c:mkcalendar xmlns:d=\"" + NS_DAV + "\" xmlns:c=\"" + NS_CALDAV + "\""
                + " xmlns:ic=\"" + NS_APPLE_ICAL + "\">"
                + (props.length() > 0 ? "<d:set><d:prop>" + props + "</d:prop></d:set>" : "")
                + "</c:mkcalendar>";

        executeWithBody("MKCALENDAR", collectionUrl(calendarName), body,
                "Creating calendar " + calendarName, 201);
    }

    /**
     * Deletes a calendar and all of its entries.
     *
     * @param calendarName name of the calendar
     */
    public void deleteCalendar(String calendarName) {
        deleteCollection(calendarName);
    }
}
