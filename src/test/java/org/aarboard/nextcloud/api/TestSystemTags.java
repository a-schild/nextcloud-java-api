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
package org.aarboard.nextcloud.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.aarboard.nextcloud.api.systemtags.Tag;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Integration tests for the system tags support.
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSystemTags {

    private static final String TEST_FOLDER = "/systemtags-test-folder";
    private static final String TAG_NAME = "api-test-tag";

    private static String serverName = null;
    private static NextcloudConnector _nc = null;

    private static int tagId = -1;
    private static long fileId = -1;

    @BeforeClass
    public static void setUp() {
        TestHelper th = new TestHelper();
        serverName = th.getServerName();
        if (serverName != null) {
            _nc = new NextcloudConnector(serverName, th.getServerPort() == 443, th.getServerPort(),
                    th.getUserName(), th.getPassword());
            _nc.createFolder(TEST_FOLDER);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (_nc != null) {
            try {
                if (tagId != -1) {
                    _nc.deleteSystemTag(tagId);
                }
            } catch (Exception ex) {
                // best effort
            }
            _nc.deleteFolder(TEST_FOLDER);
        }
    }

    @Test
    public void t01_testCreateTag() {
        if (_nc != null) {
            tagId = _nc.createSystemTag(TAG_NAME, true, true);
            assertTrue(tagId > 0);
        }
    }

    @Test
    public void t02_testGetTags() {
        if (_nc != null) {
            assertTrue(_nc.getSystemTags().stream().anyMatch(t -> t.getId() == tagId));
        }
    }

    @Test
    public void t03_testAssignTag() throws Exception {
        if (_nc != null) {
            fileId = Long.parseLong(_nc.getProperties(TEST_FOLDER, true).getFileId());
            _nc.assignSystemTag(fileId, tagId);

            Tag assigned = _nc.getSystemTagsForFile(fileId).stream()
                    .filter(t -> t.getId() == tagId).findFirst().orElse(null);
            assertNotNull(assigned);
            assertEquals(TAG_NAME, assigned.getName());
        }
    }

    @Test
    public void t04_testRemoveTag() {
        if (_nc != null) {
            _nc.removeSystemTag(fileId, tagId);
            assertTrue(_nc.getSystemTagsForFile(fileId).stream().noneMatch(t -> t.getId() == tagId));
        }
    }

    @Test
    public void t05_testDeleteTag() {
        if (_nc != null) {
            int deletedId = tagId;
            _nc.deleteSystemTag(tagId);
            tagId = -1;
            assertTrue(_nc.getSystemTags().stream().noneMatch(t -> t.getId() == deletedId));
        }
    }
}
