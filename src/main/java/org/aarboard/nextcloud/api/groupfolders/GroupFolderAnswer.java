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
import org.aarboard.nextcloud.api.utils.JsonAnswer;

/**
 * Answer of the "create group folder" call, carrying the id of the new folder.
 */
public class GroupFolderAnswer extends JsonAnswer {

    @JsonProperty
    private Data data;

    @JsonIgnore
    public Integer getId() {
        if (data != null) {
            return data.id;
        }
        return null;
    }

    public static class Data {
        @JsonProperty
        public Integer id;
    }
}
