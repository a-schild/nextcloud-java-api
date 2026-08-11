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
package org.aarboard.nextcloud.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.aarboard.nextcloud.api.calendar.Calendar;
import org.aarboard.nextcloud.api.calendar.CalendarEntry;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Integration tests for the CalDAV support (issue #59).
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCalendars {

    private static final String CALENDAR = "api-test-calendar";
    private static final String DISPLAY_NAME = "API test calendar";
    private static final String ENTRY = "api-test-event-1.ics";

    /** The event starts here, so a range around it must match and one after it must not. */
    private static final Instant EVENT_START = Instant.parse("2026-08-12T09:00:00Z");

    private static String serverName = null;
    private static NextcloudConnector _nc = null;

    private static String iCalendar(String summary) {
        return "BEGIN:VCALENDAR\r\n"
                + "VERSION:2.0\r\n"
                + "PRODID:-//nextcloud-java-api//integration test//EN\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:api-test-event-1\r\n"
                + "DTSTAMP:20260811T080000Z\r\n"
                + "DTSTART:20260812T090000Z\r\n"
                + "DTEND:20260812T100000Z\r\n"
                + "SUMMARY:" + summary + "\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
    }

    @BeforeClass
    public static void setUp() {
        TestHelper th = new TestHelper();
        serverName = th.getServerName();
        if (serverName != null) {
            _nc = new NextcloudConnector(serverName, th.getServerPort() == 443, th.getServerPort(),
                    th.getUserName(), th.getPassword());
            _nc.createCalendar(CALENDAR, DISPLAY_NAME, "#FF0000");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (_nc != null) {
            try {
                _nc.deleteCalendar(CALENDAR);
            } catch (NextcloudApiException e) {
                // the test that deletes it may already have run
            }
            _nc.close();
        }
    }

    @Test
    public void t01_listCalendars() {
        if (serverName == null) {
            return;
        }
        List<Calendar> calendars = _nc.listCalendars();
        assertFalse("The user should have at least the calendar we created", calendars.isEmpty());

        Calendar created = calendars.stream()
                .filter(c -> CALENDAR.equals(c.getName()))
                .findFirst().orElse(null);
        assertNotNull("Created calendar not found in the listing", created);
        assertEquals(DISPLAY_NAME, created.getDisplayName());
        assertNotNull("Calendar should expose a ctag", created.getCtag());
    }

    @Test
    public void t02_putAndGetEntry() {
        if (serverName == null) {
            return;
        }
        _nc.putCalendarEntry(CALENDAR, ENTRY, iCalendar("API test event"));

        CalendarEntry entry = _nc.getCalendarEntry(CALENDAR, ENTRY);
        assertNotNull(entry);
        assertEquals(ENTRY, entry.getName());
        assertNotNull("A stored entry must have an etag", entry.getEtag());
        assertTrue(entry.getData().contains("SUMMARY:API test event"));
        assertTrue(entry.getData().contains("UID:api-test-event-1"));
    }

    @Test
    public void t03_getEntries() {
        if (serverName == null) {
            return;
        }
        List<CalendarEntry> entries = _nc.getCalendarEntries(CALENDAR);
        assertEquals(1, entries.size());
        assertEquals(ENTRY, entries.get(0).getName());
        assertTrue(entries.get(0).getData().contains("UID:api-test-event-1"));
    }

    @Test
    public void t04_getEntriesInRange() {
        if (serverName == null) {
            return;
        }
        List<CalendarEntry> hit = _nc.getCalendarEntriesInRange(CALENDAR,
                EVENT_START.minus(1, ChronoUnit.DAYS), EVENT_START.plus(1, ChronoUnit.DAYS));
        assertEquals("The event should be in a range around it", 1, hit.size());
        assertTrue(hit.get(0).getData().contains("UID:api-test-event-1"));

        List<CalendarEntry> miss = _nc.getCalendarEntriesInRange(CALENDAR,
                EVENT_START.plus(30, ChronoUnit.DAYS), EVENT_START.plus(60, ChronoUnit.DAYS));
        assertTrue("No event should be in a range after it", miss.isEmpty());
    }

    @Test
    public void t05_updateWithEtagPrecondition() {
        if (serverName == null) {
            return;
        }
        CalendarEntry entry = _nc.getCalendarEntry(CALENDAR, ENTRY);
        String etag = entry.getEtag();

        // Updating with the current etag succeeds
        _nc.putCalendarEntry(CALENDAR, ENTRY, iCalendar("Updated summary"), etag);
        assertTrue(_nc.getCalendarEntry(CALENDAR, ENTRY).getData().contains("SUMMARY:Updated summary"));

        // Re-using the now stale etag must be refused instead of overwriting
        try {
            _nc.putCalendarEntry(CALENDAR, ENTRY, iCalendar("Should not be stored"), etag);
            fail("Expected the stale If-Match precondition to fail");
        } catch (NextcloudApiException expected) {
            // expected
        }
        assertTrue("The refused update must not have been stored",
                _nc.getCalendarEntry(CALENDAR, ENTRY).getData().contains("SUMMARY:Updated summary"));
    }

    @Test
    public void t06_deleteEntry() {
        if (serverName == null) {
            return;
        }
        _nc.deleteCalendarEntry(CALENDAR, ENTRY);

        assertNull(_nc.getCalendarEntry(CALENDAR, ENTRY));
        assertTrue(_nc.getCalendarEntries(CALENDAR).isEmpty());
    }

    @Test
    public void t07_deleteCalendar() {
        if (serverName == null) {
            return;
        }
        _nc.deleteCalendar(CALENDAR);

        assertTrue("The deleted calendar must be gone from the listing",
                _nc.listCalendars().stream().noneMatch(c -> CALENDAR.equals(c.getName())));
    }

    @Test
    public void t08_rejectsNameWithPathSeparator() {
        if (serverName == null) {
            return;
        }
        try {
            _nc.getCalendarEntries("../../etc");
            fail("Expected a path separator in a calendar name to be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
