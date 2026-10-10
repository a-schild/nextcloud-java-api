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
package org.aarboard.nextcloud.api.contacts;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.dav.ADavCollectionHandler;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.apache.http.entity.ContentType;

/**
 * Access to the address books of the authenticated user over
 * <a href="https://datatracker.ietf.org/doc/html/rfc6352">CardDAV</a>, below
 * {@code remote.php/dav/addressbooks/users/}: listing address books, reading and
 * writing their contacts, and creating or deleting address books.
 * <p>
 * Contacts are exchanged as raw vCard documents, see {@link Contact}.
 *
 * @author a.schild
 * @since 14.3
 */
public class AddressBooks extends ADavCollectionHandler {

    private static final String ADDRESSBOOK_ROOT = "remote.php/dav/addressbooks/users/";

    private static final QName RESOURCE_TYPE_ADDRESSBOOK = new QName(NS_CARDDAV, "addressbook");
    private static final QName PROP_ADDRESSBOOK_DESCRIPTION =
            new QName(NS_CARDDAV, "addressbook-description", "card");

    public AddressBooks(ServerConfig serverConfig) {
        super(serverConfig);
    }

    @Override
    protected String getHomeUrl() {
        return buildDavUrl(ADDRESSBOOK_ROOT + encodeSegment(getCurrentUserId())) + "/";
    }

    @Override
    protected QName getCollectionResourceType() {
        return RESOURCE_TYPE_ADDRESSBOOK;
    }

    @Override
    protected String getDataElementName() {
        return "address-data";
    }

    @Override
    protected String getReportNamespace() {
        return NS_CARDDAV;
    }

    @Override
    protected String getMultigetElementName() {
        return "addressbook-multiget";
    }

    @Override
    protected ContentType getEntryContentType() {
        return ContentType.create("text/vcard", StandardCharsets.UTF_8);
    }

    /**
     * Lists the address books of the authenticated user.
     *
     * @return the user's address books
     */
    public List<AddressBook> listAddressBooks() {
        Set<QName> props = new HashSet<>();
        props.add(PROP_ADDRESSBOOK_DESCRIPTION);

        return listCollections(props, AddressBook::new, (book, resource) ->
                book.setDescription(resource.getCustomPropsNS().get(PROP_ADDRESSBOOK_DESCRIPTION)));
    }

    /**
     * Fetches every contact of an address book, vCard payload included.
     *
     * @param addressBookName name of the address book, see
     *                        {@link AddressBook#getName()}
     * @return all contacts of the address book
     */
    public List<Contact> getContacts(String addressBookName) {
        return getEntries(addressBookName, Contact::new);
    }

    /**
     * Fetches a single contact.
     *
     * @param addressBookName name of the address book
     * @param contactName     name of the contact resource, or its href
     * @return the contact, or {@code null} if it does not exist
     */
    public Contact getContact(String addressBookName, String contactName) {
        return getEntry(addressBookName, contactName, Contact::new);
    }

    /**
     * Creates a contact, or replaces it if one of that name exists.
     *
     * @param addressBookName name of the address book
     * @param contactName     name of the contact resource, conventionally the
     *                        UID of the vCard followed by {@code .vcf}
     * @param vCard           the vCard document to store
     * @return the etag of the stored contact, or {@code null} if the server did
     *         not return one
     */
    public String putContact(String addressBookName, String contactName, String vCard) {
        return putEntry(addressBookName, contactName, vCard, null);
    }

    /**
     * Replaces a contact only while it still carries the given etag, so that a
     * change made in the meantime is not silently overwritten.
     *
     * @param addressBookName name of the address book
     * @param contactName     name of the contact resource
     * @param vCard           the vCard document to store
     * @param etag            the etag the stored contact must still have
     * @return the etag of the stored contact
     * @throws org.aarboard.nextcloud.api.exception.NextcloudApiException if the
     *         contact was modified in the meantime
     */
    public String putContact(String addressBookName, String contactName, String vCard, String etag) {
        return putEntry(addressBookName, contactName, vCard, etag);
    }

    /**
     * Deletes a contact.
     *
     * @param addressBookName name of the address book
     * @param contactName     name of the contact resource, or its href
     */
    public void deleteContact(String addressBookName, String contactName) {
        deleteEntry(addressBookName, contactName);
    }

    /**
     * Creates an address book.
     *
     * @param addressBookName name of the address book as it appears in the URL
     * @param displayName     name shown in the UI, may be {@code null}
     * @param description     description of the address book, may be
     *                        {@code null}
     */
    public void createAddressBook(String addressBookName, String displayName, String description) {
        ConnectorCommon.requireValidPathSegment(addressBookName);

        StringBuilder props = new StringBuilder();
        props.append("<d:resourcetype><d:collection/><card:addressbook/></d:resourcetype>");
        if (displayName != null) {
            props.append("<d:displayname>").append(xmlEscape(displayName)).append("</d:displayname>");
        }
        if (description != null) {
            props.append("<card:addressbook-description>").append(xmlEscape(description))
                    .append("</card:addressbook-description>");
        }

        // Extended MKCOL (RFC 5689): a plain MKCOL would create an ordinary
        // collection, the resourcetype in the body is what makes it an address book.
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<d:mkcol xmlns:d=\"" + NS_DAV + "\" xmlns:card=\"" + NS_CARDDAV + "\">"
                + "<d:set><d:prop>" + props + "</d:prop></d:set>"
                + "</d:mkcol>";

        executeWithBody("MKCOL", collectionUrl(addressBookName), body,
                "Creating address book " + addressBookName, 201);
    }

    /**
     * Deletes an address book and all of its contacts.
     *
     * @param addressBookName name of the address book
     */
    public void deleteAddressBook(String addressBookName) {
        deleteCollection(addressBookName);
    }
}
