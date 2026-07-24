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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.aarboard.nextcloud.api.utils.JsonAnswer;

/**
 * Answer of the "list group folders" call.
 */
public class GroupFoldersListAnswer extends JsonAnswer {

    @JsonProperty
    @JsonDeserialize(using = GroupListDeserializer.class)
    private Map<Integer, GroupFolderInfo> data;

    @JsonIgnore
    public Collection<GroupFolderInfo> getAllGroupFolders() {
        if (data != null) {
            return data.values();
        }
        return Collections.emptyList();
    }
}
