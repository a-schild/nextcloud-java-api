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
package org.aarboard.nextcloud.api.groupfolders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Information about a single group folder as returned by the
 * <a href="https://github.com/nextcloud/groupfolders">Group Folders</a> app.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupFolderInfo {

    @JsonProperty
    private Integer id;
    @JsonProperty
    private String mount_point;
    @JsonProperty
    private Map<String, Integer> groups;
    @JsonProperty
    private Long quota;

    /**
     * @return the id of the group folder
     */
    @JsonIgnore
    public Integer getId() {
        return id;
    }

    /**
     * @return the mount point (name) of the group folder
     */
    @JsonIgnore
    public String getMountPoint() {
        return mount_point;
    }

    /**
     * @return the assigned groups mapped to their permission bit mask
     */
    @JsonIgnore
    public Map<String, Integer> getAssignedGroups() {
        return groups;
    }

    /**
     * @return the quota of the group folder in bytes, or {@code -3} for an
     * unlimited quota (see the Group Folders app documentation)
     */
    @JsonIgnore
    public Long getQuota() {
        return quota;
    }
}
