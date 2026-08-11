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

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.webdav.AWebdavHandler;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Shared plumbing for the CalDAV and CardDAV connectors. The two protocols are
 * structurally identical - a per-user home collection holding collections which
 * in turn hold text resources - so collection listing, entry retrieval and the
 * write operations live here and the subclasses only supply the namespaces,
 * element and content type names that differ.
 *
 * @author a.schild
 * @since 14.3
 */
public abstract class ADavCollectionHandler extends AWebdavHandler {

    protected static final String NS_DAV = "DAV:";
    protected static final String NS_CALDAV = "urn:ietf:params:xml:ns:caldav";
    protected static final String NS_CARDDAV = "urn:ietf:params:xml:ns:carddav";
    protected static final String NS_CALENDARSERVER = "http://calendarserver.org/ns/";
    protected static final String NS_APPLE_ICAL = "http://apple.com/ns/ical/";

    protected static final QName PROP_DISPLAYNAME = new QName(NS_DAV, "displayname", "d");
    protected static final QName PROP_CTAG = new QName(NS_CALENDARSERVER, "getctag", "cs");

    private static final ContentType XML_CONTENT_TYPE =
            ContentType.create("application/xml", StandardCharsets.UTF_8);

    protected ADavCollectionHandler(ServerConfig serverConfig) {
        super(serverConfig);
    }

    /**
     * @return the URL of the per-user home collection, with a trailing slash,
     *         e.g. {@code https://host/remote.php/dav/calendars/user/}
     */
    protected abstract String getHomeUrl();

    /**
     * @return the resource type marking a collection this handler manages, e.g.
     *         {@code {urn:ietf:params:xml:ns:caldav}calendar}
     */
    protected abstract QName getCollectionResourceType();

    /**
     * @return local name of the element carrying the payload in a REPORT
     *         response, i.e. {@code calendar-data} or {@code address-data}
     */
    protected abstract String getDataElementName();

    /**
     * @return namespace of the payload element and of the multiget report
     */
    protected abstract String getReportNamespace();

    /**
     * @return name of the multiget report element, e.g.
     *         {@code calendar-multiget}
     */
    protected abstract String getMultigetElementName();

    /**
     * @return content type to send when storing an entry
     */
    protected abstract ContentType getEntryContentType();

    /**
     * Builds the URL of a collection below the home collection.
     *
     * @param collectionName name of the collection
     * @return the collection URL, with a trailing slash
     */
    protected String collectionUrl(String collectionName) {
        ConnectorCommon.requireValidPathSegment(collectionName);
        return getHomeUrl() + encodeSegment(collectionName) + "/";
    }

    /**
     * Builds the URL of a single entry inside a collection.
     *
     * @param collectionName name of the collection
     * @param entryName      name of the entry, or a full href of one - in which
     *                       case the last path segment is used
     * @return the entry URL
     */
    protected String entryUrl(String collectionName, String entryName) {
        return collectionUrl(collectionName) + encodeSegment(lastSegment(entryName));
    }

    /**
     * Lists the collections in the user's home collection, keeping only those
     * carrying the resource type this handler manages. This filters out the
     * scheduling inbox/outbox, the trash bin and subscriptions, which live in
     * the same home collection.
     *
     * @param <T>          the concrete collection type
     * @param extraProps   additional properties to request
     * @param factory      creates a new, empty collection instance
     * @param decorator    fills the type specific properties of a collection
     * @return the matching collections
     */
    protected <T extends DavCollection> List<T> listCollections(Set<QName> extraProps,
            Supplier<T> factory, CollectionDecorator<T> decorator) {
        Set<QName> props = new java.util.HashSet<>(extraProps);
        props.add(PROP_DISPLAYNAME);
        props.add(PROP_CTAG);

        String homeUrl = getHomeUrl();
        Sardine sardine = buildAuthSardine();
        try {
            List<T> collections = new ArrayList<>();
            for (DavResource resource : sardine.propfind(homeUrl, 1, props)) {
                if (!resource.getResourceTypes().contains(getCollectionResourceType())) {
                    continue;
                }
                T collection = factory.get();
                collection.setHref(resource.getPath());
                collection.setName(lastSegment(resource.getPath()));
                collection.setDisplayName(resource.getDisplayName() != null
                        ? resource.getDisplayName()
                        : resource.getCustomPropsNS().get(PROP_DISPLAYNAME));
                collection.setCtag(resource.getCustomPropsNS().get(PROP_CTAG));
                decorator.decorate(collection, resource);
                collections.add(collection);
            }
            return collections;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            shutdownSardine(sardine);
        }
    }

    /**
     * Fetches every entry of a collection, payload included.
     * <p>
     * This is done with a PROPFIND to enumerate the entries followed by a single
     * multiget REPORT for their payloads, rather than one GET per entry.
     *
     * @param <T>            the concrete entry type
     * @param collectionName name of the collection
     * @param entryFactory   creates a new, empty entry instance
     * @return all entries of the collection
     */
    protected <T extends DavEntry> List<T> getEntries(String collectionName, Supplier<T> entryFactory) {
        String collectionUrl = collectionUrl(collectionName);
        List<String> hrefs = listEntryHrefs(collectionUrl);
        if (hrefs.isEmpty()) {
            return new ArrayList<>();
        }
        return multiget(collectionUrl, hrefs, entryFactory);
    }

    /**
     * Fetches a single entry.
     *
     * @param <T>            the concrete entry type
     * @param collectionName name of the collection
     * @param entryName      name of the entry
     * @param entryFactory   creates a new, empty entry instance
     * @return the entry, or {@code null} if it does not exist
     */
    protected <T extends DavEntry> T getEntry(String collectionName, String entryName,
            Supplier<T> entryFactory) {
        String url = entryUrl(collectionName, entryName);
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", authorizationHeader());

        try (CloseableHttpClient client = buildSyncClient();
                CloseableHttpResponse response = client.execute(get)) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 404) {
                return null;
            }
            if (status != 200) {
                throw new NextcloudApiException("Fetching " + url + " failed with status " + status);
            }
            T entry = entryFactory.get();
            entry.setHref(URI.create(url).getPath());
            entry.setData(readBody(response.getEntity()));
            Header etag = response.getFirstHeader("ETag");
            if (etag != null) {
                entry.setEtag(unquote(etag.getValue()));
            }
            return entry;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Creates or replaces an entry.
     *
     * @param collectionName name of the collection
     * @param entryName      name of the entry, including the file extension
     * @param data           the raw payload to store
     * @param ifMatchEtag    when set, the write only succeeds while the stored
     *                       entry still carries this etag; when {@code null} the
     *                       entry is written unconditionally
     * @return the etag of the stored entry, or {@code null} if the server did
     *         not return one
     */
    protected String putEntry(String collectionName, String entryName, String data, String ifMatchEtag) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Entry data must not be empty");
        }
        String url = entryUrl(collectionName, entryName);
        HttpPut put = new HttpPut(url);
        put.setHeader("Authorization", authorizationHeader());
        if (ifMatchEtag != null) {
            put.setHeader("If-Match", quote(ifMatchEtag));
        }
        put.setEntity(new StringEntity(data, getEntryContentType()));

        try (CloseableHttpClient client = buildSyncClient();
                CloseableHttpResponse response = client.execute(put)) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 412) {
                throw new NextcloudApiException("Entry " + entryName
                        + " was modified on the server, the If-Match precondition failed");
            }
            if (status != 201 && status != 204 && status != 200) {
                throw new NextcloudApiException("Storing " + url + " failed with status " + status);
            }
            Header etag = response.getFirstHeader("ETag");
            return etag != null ? unquote(etag.getValue()) : null;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Deletes an entry.
     *
     * @param collectionName name of the collection
     * @param entryName      name of the entry
     */
    protected void deleteEntry(String collectionName, String entryName) {
        HttpDelete delete = new HttpDelete(entryUrl(collectionName, entryName));
        delete.setHeader("Authorization", authorizationHeader());
        execute(delete, "Deleting entry " + entryName, 204, 200);
    }

    /**
     * Deletes a whole collection and everything in it.
     *
     * @param collectionName name of the collection
     */
    protected void deleteCollection(String collectionName) {
        HttpDelete delete = new HttpDelete(collectionUrl(collectionName));
        delete.setHeader("Authorization", authorizationHeader());
        execute(delete, "Deleting collection " + collectionName, 204, 200);
    }

    /**
     * Issues a request with a body and an XML content type, e.g. MKCALENDAR or
     * an extended MKCOL.
     *
     * @param method          the HTTP method name
     * @param url             the target URL
     * @param body            the request body
     * @param description     used in the error message
     * @param expectedStatus  the accepted status codes
     */
    protected void executeWithBody(String method, String url, String body, String description,
            int... expectedStatus) {
        DavMethod request = new DavMethod(method, url);
        request.setHeader("Authorization", authorizationHeader());
        request.setEntity(new StringEntity(body, XML_CONTENT_TYPE));
        execute(request, description, expectedStatus);
    }

    private List<String> listEntryHrefs(String collectionUrl) {
        Sardine sardine = buildAuthSardine();
        try {
            List<String> hrefs = new ArrayList<>();
            String collectionPath = URI.create(collectionUrl).getPath();
            for (DavResource resource : sardine.list(collectionUrl, 1)) {
                String path = resource.getPath();
                // The collection itself is part of a depth 1 listing.
                if (path == null || samePath(path, collectionPath)) {
                    continue;
                }
                hrefs.add(path);
            }
            return hrefs;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            shutdownSardine(sardine);
        }
    }

    private <T extends DavEntry> List<T> multiget(String collectionUrl, List<String> hrefs,
            Supplier<T> entryFactory) {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<x:").append(getMultigetElementName())
                .append(" xmlns:d=\"").append(NS_DAV).append('"')
                .append(" xmlns:x=\"").append(getReportNamespace()).append("\">")
                .append("<d:prop><d:getetag/><x:").append(getDataElementName()).append("/></d:prop>");
        for (String href : hrefs) {
            body.append("<d:href>").append(xmlEscape(href)).append("</d:href>");
        }
        body.append("</x:").append(getMultigetElementName()).append('>');

        return report(collectionUrl, body.toString(), entryFactory);
    }

    /**
     * Sends a REPORT request and parses the multistatus response.
     *
     * @param <T>          the concrete entry type
     * @param url          the target collection URL
     * @param body         the REPORT body
     * @param entryFactory creates a new, empty entry instance
     * @return the entries carried by the response
     */
    protected <T extends DavEntry> List<T> report(String url, String body, Supplier<T> entryFactory) {
        DavMethod request = new DavMethod("REPORT", url);
        request.setHeader("Authorization", authorizationHeader());
        request.setHeader("Depth", "1");
        request.setEntity(new StringEntity(body, XML_CONTENT_TYPE));

        try (CloseableHttpClient client = buildSyncClient();
                CloseableHttpResponse response = client.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            if (status != 207) {
                throw new NextcloudApiException("REPORT on " + url + " failed with status " + status);
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            try (InputStream in = entity.getContent()) {
                return MultistatusParser.parse(in, getDataElementName(), entryFactory);
            }
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    private void execute(HttpUriRequest request, String description, int... expectedStatus) {
        try (CloseableHttpClient client = buildSyncClient();
                CloseableHttpResponse response = client.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            for (int expected : expectedStatus) {
                if (status == expected) {
                    return;
                }
            }
            throw new NextcloudApiException(description + " failed with status " + status);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            if (request instanceof HttpRequestBase) {
                ((HttpRequestBase) request).releaseConnection();
            }
        }
    }

    private static String readBody(HttpEntity entity) throws IOException {
        if (entity == null) {
            return null;
        }
        try (InputStream in = entity.getContent()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static boolean samePath(String left, String right) {
        return stripTrailingSlash(left).equals(stripTrailingSlash(right));
    }

    private static String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    /**
     * Returns the last path segment of a value, so that callers may pass either
     * a bare entry name or a full href.
     *
     * @param value an entry name or href
     * @return the last path segment
     */
    protected static String lastSegment(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        String trimmed = stripTrailingSlash(value);
        int lastSlash = trimmed.lastIndexOf('/');
        String segment = lastSlash >= 0 ? trimmed.substring(lastSlash + 1) : trimmed;
        if (segment.isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        return segment;
    }

    /**
     * Percent-encodes a single path segment. The segment has already been
     * checked not to contain a path separator, so only the remaining reserved
     * characters need escaping.
     *
     * @param segment the path segment
     * @return the encoded segment
     */
    protected static String encodeSegment(String segment) {
        StringBuilder encoded = new StringBuilder();
        for (byte b : segment.getBytes(StandardCharsets.UTF_8)) {
            int value = b & 0xFF;
            if ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z')
                    || (value >= '0' && value <= '9')
                    || value == '-' || value == '_' || value == '.' || value == '~') {
                encoded.append((char) value);
            } else {
                encoded.append('%').append(String.format("%02X", value));
            }
        }
        return encoded.toString();
    }

    /**
     * Escapes the characters that may not appear in XML character data.
     *
     * @param value the raw value
     * @return the escaped value
     */
    protected static String xmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String quote(String etag) {
        String value = etag.trim();
        return value.startsWith("\"") || value.startsWith("W/") ? value : "\"" + value + "\"";
    }

    private static String unquote(String etag) {
        String value = etag.trim();
        if (value.startsWith("W/")) {
            value = value.substring(2);
        }
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Fills in the properties specific to a collection type.
     *
     * @param <T> the concrete collection type
     */
    @FunctionalInterface
    protected interface CollectionDecorator<T extends DavCollection> {
        void decorate(T collection, DavResource resource);
    }
}
