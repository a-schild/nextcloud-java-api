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
package org.aarboard.nextcloud.api.provisioning;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.aarboard.nextcloud.api.utils.XMLAnswer;

/**
 *
 * @author a.schild
 */
@XmlRootElement(name = "ocs")
public class UsersXMLAnswer extends XMLAnswer
{
    private Data data;

    public List<String> getUsers()
    {
        return data.users;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static final class Data
    {
        @XmlElementWrapper(name = "users")
        @XmlElement(name = "element")
        private List<String> users;
    }
}
