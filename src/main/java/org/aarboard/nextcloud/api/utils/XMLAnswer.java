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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author a.schild
 */
@XmlRootElement(name="ocs")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLAnswer {
	private Meta meta;

    /**
     * @return the status
     */
    public String getStatus() {
        return meta.status;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return meta.statusCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return meta.message;
    }

    /**
     * @return the totalItems
     */
    public int getTotalItems() {
        return meta.totalItems;
    }

    /**
     * @return the itemsPerPage
     */
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
