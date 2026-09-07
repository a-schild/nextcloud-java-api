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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import org.aarboard.nextcloud.api.contacts.AddressBook;
import org.aarboard.nextcloud.api.contacts.Contact;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Integration tests for the CardDAV support (issue #59).
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAddressBooks {

    private static final String ADDRESSBOOK = "api-test-addressbook";
    private static final String DISPLAY_NAME = "API test address book";
    private static final String DESCRIPTION = "Created by the integration tests";
    private static final String CONTACT = "api-test-contact-1.vcf";

    private static String serverName = null;
    private static NextcloudConnector _nc = null;

    private static String vCard(String fullName) {
        return "BEGIN:VCARD\r\n"
                + "VERSION:3.0\r\n"
                + "UID:api-test-contact-1\r\n"
                + "FN:" + fullName + "\r\n"
                + "N:Doe;Jane;;;\r\n"
                + "EMAIL:jane.doe@example.org\r\n"
                + "END:VCARD\r\n";
    }

    @BeforeClass
    public static void setUp() {
        TestHelper th = new TestHelper();
        serverName = th.getServerName();
        if (serverName != null) {
            _nc = new NextcloudConnector(serverName, th.getServerPort() == 443, th.getServerPort(),
                    th.getUserName(), th.getPassword());
            _nc.createAddressBook(ADDRESSBOOK, DISPLAY_NAME, DESCRIPTION);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (_nc != null) {
            try {
                _nc.deleteAddressBook(ADDRESSBOOK);
            } catch (NextcloudApiException e) {
                // the test that deletes it may already have run
            }
            _nc.close();
        }
    }

    @Test
    public void t01_listAddressBooks() {
        if (serverName == null) {
            return;
        }
        List<AddressBook> books = _nc.listAddressBooks();

        AddressBook created = books.stream()
                .filter(b -> ADDRESSBOOK.equals(b.getName()))
                .findFirst().orElse(null);
        assertNotNull("Created address book not found in the listing", created);
        assertEquals(DISPLAY_NAME, created.getDisplayName());
        assertEquals(DESCRIPTION, created.getDescription());
    }

    @Test
    public void t02_putAndGetContact() {
        if (serverName == null) {
            return;
        }
        _nc.putContact(ADDRESSBOOK, CONTACT, vCard("Jane Doe"));

        Contact contact = _nc.getContact(ADDRESSBOOK, CONTACT);
        assertNotNull(contact);
        assertEquals(CONTACT, contact.getName());
        assertNotNull("A stored contact must have an etag", contact.getEtag());
        assertTrue(contact.getData().contains("FN:Jane Doe"));
    }

    @Test
    public void t03_getContacts() {
        if (serverName == null) {
            return;
        }
        List<Contact> contacts = _nc.getContacts(ADDRESSBOOK);
        assertEquals(1, contacts.size());
        assertEquals(CONTACT, contacts.get(0).getName());
        assertTrue(contacts.get(0).getData().contains("EMAIL:jane.doe@example.org"));
    }

    @Test
    public void t04_updateContact() {
        if (serverName == null) {
            return;
        }
        _nc.putContact(ADDRESSBOOK, CONTACT, vCard("Jane Renamed"));

        assertTrue(_nc.getContact(ADDRESSBOOK, CONTACT).getData().contains("FN:Jane Renamed"));
        assertEquals("Updating must not create a second contact", 1, _nc.getContacts(ADDRESSBOOK).size());
    }

    @Test
    public void t05_deleteContact() {
        if (serverName == null) {
            return;
        }
        _nc.deleteContact(ADDRESSBOOK, CONTACT);

        assertNull(_nc.getContact(ADDRESSBOOK, CONTACT));
        assertTrue(_nc.getContacts(ADDRESSBOOK).isEmpty());
    }

    @Test
    public void t06_deleteAddressBook() {
        if (serverName == null) {
            return;
        }
        _nc.deleteAddressBook(ADDRESSBOOK);

        assertTrue("The deleted address book must be gone from the listing",
                _nc.listAddressBooks().stream().noneMatch(b -> ADDRESSBOOK.equals(b.getName())));
    }

    @Test
    public void t07_rejectsNameWithPathSeparator() {
        if (serverName == null) {
            return;
        }
        try {
            _nc.getContacts("../other");
            fail("Expected a path separator in an address book name to be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
