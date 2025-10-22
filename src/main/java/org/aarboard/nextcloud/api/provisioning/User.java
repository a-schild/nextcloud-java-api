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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 *
 * @author a.schild
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    private String id;
    private boolean enabled;
    private long lastLogin;
    private String backend;
    private String email;
    private String displayname;
    private String phone;
    private String address;
    private String website;
    private String twitter;
    private Quota quota;
    private List<String> groups;
    private String language;
    private String locale;

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getBackend() {
        return backend;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }

    public String getTwitter() {
        return twitter;
    }

    public Quota getQuota() {
        return quota;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getLanguage() {
        return language;
    }

    public String getLocale() {
        return locale;
    }
}
