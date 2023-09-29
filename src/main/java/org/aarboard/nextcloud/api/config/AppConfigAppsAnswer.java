/*
 * Copyright (C) 2020 Marco Descher
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
package org.aarboard.nextcloud.api.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import org.aarboard.nextcloud.api.utils.XMLAnswer;

@XmlRootElement(name = "ocs")
public class AppConfigAppsAnswer extends XMLAnswer
{
    private Data data;

    public List<String> getAppConfigApps()
    {
        return data.appConfigApps;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static final class Data
    {
        @XmlElementWrapper(name = "data")
        @XmlElement(name = "element")
        private List<String> appConfigApps;
    }
}
