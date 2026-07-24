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

import org.junit.Test;

/**
 * Unit tests for {@link ConnectorCommon#requireValidPathSegment(String)}.
 *
 * @author a.schild
 */
public class ConnectorCommonTest {

    @Test
    public void testValidSegmentPassesThrough() {
        assertEquals("john.doe", ConnectorCommon.requireValidPathSegment("john.doe"));
        assertEquals("group with spaces", ConnectorCommon.requireValidPathSegment("group with spaces"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForwardSlashRejected() {
        ConnectorCommon.requireValidPathSegment("admin/../otheruser");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBackslashRejected() {
        ConnectorCommon.requireValidPathSegment("admin\\evil");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRejected() {
        ConnectorCommon.requireValidPathSegment(null);
    }
}
