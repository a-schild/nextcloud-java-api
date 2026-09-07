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

/**
 * A single resource inside a CalDAV or CardDAV collection, carrying the raw
 * payload as it is stored on the server.
 * <p>
 * The library deliberately does not parse the iCalendar/vCard payload: it has no
 * dependency on an iCalendar or vCard library, so consumers stay free to use
 * whichever one they prefer (for example ical4j or ez-vcard) or none at all.
 *
 * @author a.schild
 * @since 14.3
 */
public abstract class DavEntry {

    private String href;
    private String etag;
    private String data;

    /**
     * @return the absolute path of the resource on the server, for example
     *         {@code /remote.php/dav/calendars/user/personal/abc123.ics}
     */
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * The name of the resource within its collection, i.e. the last segment of
     * the href. This is the value to pass back to the update and delete methods.
     *
     * @return the resource name, or {@code null} if no href is set
     */
    public String getName() {
        if (href == null) {
            return null;
        }
        String trimmed = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        int lastSlash = trimmed.lastIndexOf('/');
        return lastSlash >= 0 ? trimmed.substring(lastSlash + 1) : trimmed;
    }

    /**
     * @return the entity tag of this resource, usable as a precondition when
     *         updating it to avoid overwriting a concurrent change
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * @return the raw payload (an iCalendar or vCard document), or {@code null}
     *         if only the metadata was requested
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{href=" + href + ", etag=" + etag + '}';
    }
}
