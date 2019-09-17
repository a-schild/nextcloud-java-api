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
package org.aarboard.nextcloud.api.filesharing;

import java.security.InvalidParameterException;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author a.schild
 */
@XmlType
@XmlEnum(String.class)
public enum ItemType {
	@XmlEnumValue("folder") FOLDER("folder"),
	@XmlEnumValue("file") FILE("file");

    private final String itemTypeStr;

    private ItemType(String itemTypeStr) {
        this.itemTypeStr = itemTypeStr;
    }

    public String getItemTypeStr() {
        return itemTypeStr;
    }
    
    public static ItemType getItemByName(String name)
    {
        for (ItemType t : ItemType.values())
        {
            if (t.getItemTypeStr().equals(name))
            {
                return t;
            }
        }
        throw new InvalidParameterException("Invalid ItemType found <"+name+">");
    }
    
    
}
