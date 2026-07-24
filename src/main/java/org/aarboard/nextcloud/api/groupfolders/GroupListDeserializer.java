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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserializes the {@code data} element of the group folders list. Depending on
 * the server / app version this is either a JSON object keyed by the folder id
 * ({@code {"1":{...},"2":{...}}}) or a JSON array ({@code [{...},{...}]}), and
 * an empty result may be an empty array. Iterating the container node handles
 * all of these uniformly.
 */
public class GroupListDeserializer extends JsonDeserializer<Map<Integer, GroupFolderInfo>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Map<Integer, GroupFolderInfo> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        Map<Integer, GroupFolderInfo> result = new HashMap<>();
        JsonNode node = p.readValueAsTree();
        if (node != null && node.isContainerNode()) {
            for (JsonNode entry : node) {
                GroupFolderInfo info = MAPPER.treeToValue(entry, GroupFolderInfo.class);
                result.put(info.getId(), info);
            }
        }
        return result;
    }
}
