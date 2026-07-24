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
package org.aarboard.nextcloud.api.filesharing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Pure unit tests for the {@link ShareType} mapping (no server required).
 *
 * @author a.schild
 */
public class ShareTypeTest {

    @Test
    public void testIntValues() {
        assertEquals(7, ShareType.CIRCLE.getIntValue());
        assertEquals(10, ShareType.TALK.getIntValue());
    }

    @Test
    public void testGetShareTypeForIntValue() {
        assertEquals(ShareType.CIRCLE, ShareType.getShareTypeForIntValue(7));
        assertEquals(ShareType.TALK, ShareType.getShareTypeForIntValue(10));
        assertEquals(ShareType.FEDERATED_CLOUD_SHARE, ShareType.getShareTypeForIntValue(6));
    }
}
