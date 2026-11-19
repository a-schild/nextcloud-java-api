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

import org.aarboard.nextcloud.api.dav.DavCollection;

/**
 * A calendar collection of a Nextcloud user.
 *
 * @author a.schild
 * @since 14.3
 */
public class Calendar extends DavCollection {

    private String color;
    private String order;

    /**
     * @return the calendar colour as an HTML colour code, e.g. {@code #FF0000},
     *         or {@code null} if the calendar has none
     */
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the sort order the clients display the calendar in, or
     *         {@code null} if unset
     */
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
