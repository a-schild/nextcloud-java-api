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
package org.aarboard.nextcloud.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.aarboard.nextcloud.api.provisioning.GroupListAnswer;
import org.aarboard.nextcloud.api.provisioning.UserDetailsListAnswer;
import org.aarboard.nextcloud.api.provisioning.UserListAnswer;
import org.junit.Test;

/**
 * Pure parsing tests (no server required) for the OCS JSON quirk where empty
 * collections are serialized as an empty array {@code []} instead of an object.
 *
 * @author a.schild
 */
public class JsonAnswerParserTest {

    /**
     * An empty group list comes back with {@code "data":[]} instead of an
     * object; it must deserialize to an empty list rather than throwing.
     */
    @Test
    public void testEmptyGroupListDataAsArray() {
        String json = "{\"ocs\":{\"meta\":{\"status\":\"ok\",\"statuscode\":100},\"data\":[]}}";
        GroupListAnswer answer = JsonAnswerParser.getInstance(GroupListAnswer.class)
                .parseResponse(new StringReader(json));
        assertTrue(answer.getAllGroups().isEmpty());
    }

    /**
     * An empty user-details map comes back with {@code "users":[]}; it must
     * deserialize to an empty list rather than throwing.
     */
    @Test
    public void testEmptyUserDetailsUsersAsArray() {
        String json = "{\"ocs\":{\"meta\":{\"status\":\"ok\",\"statuscode\":100},\"data\":{\"users\":[]}}}";
        UserDetailsListAnswer answer = JsonAnswerParser.getInstance(UserDetailsListAnswer.class)
                .parseResponse(new StringReader(json));
        assertTrue(answer.getAllUserDetails().isEmpty());
    }

    /**
     * An empty user list comes back with {@code "data":[]}; it must deserialize
     * to an empty list rather than throwing.
     */
    @Test
    public void testEmptyUserListDataAsArray() {
        String json = "{\"ocs\":{\"meta\":{\"status\":\"ok\",\"statuscode\":100},\"data\":[]}}";
        UserListAnswer answer = JsonAnswerParser.getInstance(UserListAnswer.class)
                .parseResponse(new StringReader(json));
        assertTrue(answer.getAllUsers().isEmpty());
    }

    /**
     * A populated group list must still deserialize correctly (guards against
     * the empty-array handling breaking the normal case).
     */
    @Test
    public void testPopulatedGroupList() {
        String json = "{\"ocs\":{\"meta\":{\"status\":\"ok\",\"statuscode\":100},"
                + "\"data\":{\"groups\":[\"admin\",\"users\"]}}}";
        GroupListAnswer answer = JsonAnswerParser.getInstance(GroupListAnswer.class)
                .parseResponse(new StringReader(json));
        List<String> groups = answer.getAllGroups();
        assertEquals(2, groups.size());
        assertEquals("admin", groups.get(0));
        assertEquals("users", groups.get(1));
    }
}
