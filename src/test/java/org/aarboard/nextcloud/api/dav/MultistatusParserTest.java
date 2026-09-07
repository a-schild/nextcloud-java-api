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
package org.aarboard.nextcloud.api.dav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.aarboard.nextcloud.api.calendar.CalendarEntry;
import org.aarboard.nextcloud.api.contacts.Contact;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.junit.Test;

/**
 * Unit tests for the CalDAV/CardDAV multistatus reader. These need no server.
 *
 * @author a.schild
 */
public class MultistatusParserTest {

    private static InputStream stream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testParsesCalendarMultistatus() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<d:multistatus xmlns:d=\"DAV:\" xmlns:cal=\"urn:ietf:params:xml:ns:caldav\">"
                + "<d:response>"
                + "<d:href>/remote.php/dav/calendars/user/personal/event1.ics</d:href>"
                + "<d:propstat><d:prop>"
                + "<d:getetag>&quot;abc123&quot;</d:getetag>"
                + "<cal:calendar-data>BEGIN:VCALENDAR\nUID:event1\nEND:VCALENDAR</cal:calendar-data>"
                + "</d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat>"
                + "</d:response>"
                + "<d:response>"
                + "<d:href>/remote.php/dav/calendars/user/personal/event2.ics</d:href>"
                + "<d:propstat><d:prop>"
                + "<d:getetag>W/&quot;def456&quot;</d:getetag>"
                + "<cal:calendar-data>BEGIN:VCALENDAR\nUID:event2\nEND:VCALENDAR</cal:calendar-data>"
                + "</d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat>"
                + "</d:response>"
                + "</d:multistatus>";

        List<CalendarEntry> entries = MultistatusParser.parse(stream(xml), "calendar-data",
                CalendarEntry::new);

        assertEquals(2, entries.size());
        assertEquals("/remote.php/dav/calendars/user/personal/event1.ics", entries.get(0).getHref());
        assertEquals("event1.ics", entries.get(0).getName());
        assertEquals("abc123", entries.get(0).getEtag());
        assertTrue(entries.get(0).getData().contains("UID:event1"));
        // A weak etag keeps its value but loses the W/ marker and the quotes
        assertEquals("def456", entries.get(1).getEtag());
    }

    @Test
    public void testParsesAddressbookMultistatus() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<d:multistatus xmlns:d=\"DAV:\" xmlns:card=\"urn:ietf:params:xml:ns:carddav\">"
                + "<d:response>"
                + "<d:href>/remote.php/dav/addressbooks/users/user/contacts/c1.vcf</d:href>"
                + "<d:propstat><d:prop>"
                + "<d:getetag>&quot;e1&quot;</d:getetag>"
                + "<card:address-data>BEGIN:VCARD\nFN:Jane Doe\nEND:VCARD</card:address-data>"
                + "</d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat>"
                + "</d:response>"
                + "</d:multistatus>";

        List<Contact> contacts = MultistatusParser.parse(stream(xml), "address-data", Contact::new);

        assertEquals(1, contacts.size());
        assertEquals("c1.vcf", contacts.get(0).getName());
        assertTrue(contacts.get(0).getData().contains("FN:Jane Doe"));
    }

    /**
     * A multiget for a href that no longer exists yields a 404 propstat without
     * a payload. Such a response carries no data for the caller, so it must not
     * turn into an entry with a null payload.
     */
    @Test
    public void testSkipsResponsesWithoutPayload() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<d:multistatus xmlns:d=\"DAV:\" xmlns:cal=\"urn:ietf:params:xml:ns:caldav\">"
                + "<d:response>"
                + "<d:href>/remote.php/dav/calendars/user/personal/gone.ics</d:href>"
                + "<d:propstat><d:prop><cal:calendar-data/></d:prop>"
                + "<d:status>HTTP/1.1 404 Not Found</d:status></d:propstat>"
                + "</d:response>"
                + "<d:response>"
                + "<d:href>/remote.php/dav/calendars/user/personal/here.ics</d:href>"
                + "<d:propstat><d:prop>"
                + "<d:getetag>&quot;e&quot;</d:getetag>"
                + "<cal:calendar-data>BEGIN:VCALENDAR\nEND:VCALENDAR</cal:calendar-data>"
                + "</d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat>"
                + "</d:response>"
                + "</d:multistatus>";

        List<CalendarEntry> entries = MultistatusParser.parse(stream(xml), "calendar-data",
                CalendarEntry::new);

        assertEquals(1, entries.size());
        assertEquals("here.ics", entries.get(0).getName());
    }

    @Test
    public void testEmptyMultistatusYieldsNoEntries() {
        String xml = "<?xml version=\"1.0\"?><d:multistatus xmlns:d=\"DAV:\"/>";

        assertTrue(MultistatusParser.parse(stream(xml), "calendar-data", CalendarEntry::new).isEmpty());
    }

    @Test
    public void testParsesHrefs() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<d:multistatus xmlns:d=\"DAV:\">"
                + "<d:response><d:href>/dav/calendars/user/personal/</d:href></d:response>"
                + "<d:response><d:href>/dav/calendars/user/personal/a.ics</d:href></d:response>"
                + "</d:multistatus>";

        List<String> hrefs = MultistatusParser.parseHrefs(stream(xml));

        assertEquals(2, hrefs.size());
        assertEquals("/dav/calendars/user/personal/", hrefs.get(0));
        assertEquals("/dav/calendars/user/personal/a.ics", hrefs.get(1));
    }

    /**
     * The reader must not resolve external entities, so that a malicious or
     * compromised server cannot use a DAV response to read local files.
     */
    @Test
    public void testRejectsExternalEntities() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE d [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
                + "<d:multistatus xmlns:d=\"DAV:\" xmlns:cal=\"urn:ietf:params:xml:ns:caldav\">"
                + "<d:response><d:href>/a.ics</d:href>"
                + "<d:propstat><d:prop><cal:calendar-data>&xxe;</cal:calendar-data></d:prop></d:propstat>"
                + "</d:response></d:multistatus>";

        try {
            List<CalendarEntry> entries = MultistatusParser.parse(stream(xml), "calendar-data",
                    CalendarEntry::new);
            // Some StAX implementations report the disallowed DTD by failing, others
            // by simply not expanding the entity. Never leak the file content.
            for (CalendarEntry entry : entries) {
                assertTrue("External entity was expanded",
                        entry.getData() == null || !entry.getData().contains("root:"));
            }
        } catch (NextcloudApiException expected) {
            // DTD support is disabled, which is the outcome we want
        }
    }

    @Test
    public void testEntryNameOfNullHrefIsNull() {
        CalendarEntry entry = new CalendarEntry();
        assertNull(entry.getName());
    }

    @Test
    public void testMalformedXmlIsWrapped() {
        try {
            MultistatusParser.parse(stream("<d:multistatus>"), "calendar-data", CalendarEntry::new);
            fail("Expected a NextcloudApiException for malformed XML");
        } catch (NextcloudApiException expected) {
            // expected
        }
    }
}
