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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

/**
 * Minimal reader for the {@code DAV:multistatus} documents returned by the
 * CalDAV/CardDAV {@code REPORT} requests.
 * <p>
 * Sardine models plain WebDAV properties only and drops the
 * {@code calendar-data} / {@code address-data} payload elements, so the REPORT
 * responses are read here instead. Only the three elements the connectors need
 * are extracted (href, getetag and the payload); everything else is skipped.
 * Like {@code XMLAnswerParser}, the reader is hardened against XXE by
 * disabling DTDs and external entities.
 *
 * @author a.schild
 * @since 14.3
 */
public final class MultistatusParser {

    private static final XMLInputFactory XML_INPUT_FACTORY = createHardenedInputFactory();

    private static final String EL_RESPONSE = "response";
    private static final String EL_HREF = "href";
    private static final String EL_ETAG = "getetag";

    private MultistatusParser() {
    }

    private static XMLInputFactory createHardenedInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
    }

    /**
     * Parses a multistatus document into entries.
     *
     * @param <T>          the concrete entry type
     * @param in           the response body
     * @param dataElement  local name of the element holding the payload, i.e.
     *                     {@code calendar-data} or {@code address-data}
     * @param entryFactory creates a new, empty entry instance
     * @return one entry per {@code response} element that carried a payload
     */
    public static <T extends DavEntry> List<T> parse(InputStream in, String dataElement,
            Supplier<T> entryFactory) {
        List<T> entries = new ArrayList<>();
        try {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(in);
            try {
                T current = null;
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        String local = reader.getLocalName();
                        if (EL_RESPONSE.equals(local)) {
                            current = entryFactory.get();
                        } else if (current == null) {
                            continue;
                        } else if (EL_HREF.equals(local)) {
                            // A response holds exactly one href, but the propstat
                            // sections can contain further ones (e.g. in principal
                            // properties); keep the first, which is the resource.
                            if (current.getHref() == null) {
                                current.setHref(reader.getElementText());
                            }
                        } else if (EL_ETAG.equals(local)) {
                            current.setEtag(unquote(reader.getElementText()));
                        } else if (dataElement.equals(local)) {
                            current.setData(reader.getElementText());
                        }
                    } else if (event == XMLStreamConstants.END_ELEMENT
                            && EL_RESPONSE.equals(reader.getLocalName())) {
                        // Responses without a payload are error entries (404/403
                        // propstat) and are not useful to the caller. Such a
                        // propstat still echoes the requested element, empty, so
                        // an empty payload counts as absent here.
                        if (current != null && current.getData() != null
                                && !current.getData().isEmpty()) {
                            entries.add(current);
                        }
                        current = null;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            throw new NextcloudApiException("Could not parse the DAV multistatus response", e);
        }
        return entries;
    }

    /**
     * Reads only the hrefs of a multistatus document, ignoring any payload.
     *
     * @param in the response body
     * @return the href of every {@code response} element, in document order
     */
    public static List<String> parseHrefs(InputStream in) {
        List<String> hrefs = new ArrayList<>();
        try {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(in);
            try {
                boolean inResponse = false;
                boolean hrefSeen = false;
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        String local = reader.getLocalName();
                        if (EL_RESPONSE.equals(local)) {
                            inResponse = true;
                            hrefSeen = false;
                        } else if (inResponse && !hrefSeen && EL_HREF.equals(local)) {
                            hrefs.add(reader.getElementText());
                            hrefSeen = true;
                        }
                    } else if (event == XMLStreamConstants.END_ELEMENT
                            && EL_RESPONSE.equals(reader.getLocalName())) {
                        inResponse = false;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            throw new NextcloudApiException("Could not parse the DAV multistatus response", e);
        }
        return hrefs;
    }

    private static String unquote(String etag) {
        if (etag == null) {
            return null;
        }
        String value = etag.trim();
        if (value.startsWith("W/")) {
            value = value.substring(2);
        }
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
}
