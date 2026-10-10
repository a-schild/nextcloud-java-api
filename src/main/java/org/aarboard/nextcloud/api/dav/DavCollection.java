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
 * Common metadata of a CalDAV/CardDAV collection (a calendar or an address
 * book).
 *
 * @author a.schild
 * @since 14.3
 */
public abstract class DavCollection {

    private String name;
    private String href;
    private String displayName;
    private String description;
    private String ctag;

    /**
     * The collection name as it appears in the URL, for example
     * {@code personal}. This is the identifier to pass to the entry methods.
     *
     * @return the collection name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the absolute path of the collection on the server
     */
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the human readable name shown in the Nextcloud UI, which may
     *         differ from {@link #getName()}
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The collection tag: it changes whenever anything inside the collection
     * changes, so it can be used to skip a full sync when nothing has changed.
     *
     * @return the ctag of the collection
     */
    public String getCtag() {
        return ctag;
    }

    public void setCtag(String ctag) {
        this.ctag = ctag;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + name + ", displayName=" + displayName + '}';
    }
}
