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
 * https://docs.nextcloud.com/server/11/developer_manual/core/ocs-share-api.html
 * 
 * @author a.schild
 */
@XmlType
@XmlEnum(Integer.class)
public enum ShareType {

    @XmlEnumValue("0") USER(0),
    @XmlEnumValue("1") GROUP(1),
    @XmlEnumValue("3") PUBLIC_LINK(3),
    @XmlEnumValue("4") EMAIL(4),
    @XmlEnumValue("6") FEDERATED_CLOUD_SHARE(6);

    private final int intValue;
    
    private ShareType(int iV) {
        intValue= iV;
    }

    public int getIntValue() {
        return intValue;
    }

    public static ShareType getShareTypeForIntValue(int i)
    {
        for (ShareType s : ShareType.values())
        {
            if (s.getIntValue() == i)
            {
                return s;
            }
        }
        throw new InvalidParameterException("Invalid ShareType found " + i);
    }
    
}
