/* 
 * Copyright (C) 2017 a.schild
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
package org.aarboard.nextcloud.api.utils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author a.schild
 */
@XmlRootElement(name="ocs")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLAnswer implements NextcloudResponse {
	private Meta meta;

    @Override
    public String getStatus() {
        return meta.status;
    }

    @Override
    public int getStatusCode() {
        return meta.statusCode;
    }

    @Override
    public String getMessage() {
        return meta.message;
    }

    @Override
    public int getTotalItems() {
        return meta.totalItems;
    }

    @Override
    public int getItemsPerPage() {
        return meta.itemsPerPage;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
	private static final class Meta {
        private String status= null;
        @XmlElement(name="statuscode")
        private int statusCode= -1;
        private String message= null;
        @XmlElement(name="totalitems")
        private int totalItems= -1;
        @XmlElement(name="itemsperpage")
        private int itemsPerPage= -1;
    }
}
