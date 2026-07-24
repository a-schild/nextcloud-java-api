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

import java.util.Optional;

import org.aarboard.nextcloud.api.groupfolders.GroupFolderInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Integration tests for the Group Folders app support. Requires the
 * {@code groupfolders} app to be installed on the test server (the test
 * container installs it automatically).
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGroupFolders {

    private static final String TEST_GROUP_FOLDER = "api-test-group-folder";
    private static final String TEST_GROUP_FOLDER_RENAMED = "api-test-group-folder-renamed";
    private static final String ADMIN_GROUP = "admin";

    private static String serverName = null;
    private static NextcloudConnector _nc = null;

    private static int groupFolderId = -1;

    @BeforeClass
    public static void setUp() {
        TestHelper th = new TestHelper();
        serverName = th.getServerName();
        if (serverName != null) {
            _nc = new NextcloudConnector(serverName, th.getServerPort() == 443, th.getServerPort(),
                    th.getUserName(), th.getPassword());
        }
    }

    @AfterClass
    public static void tearDown() {
        if (_nc != null && groupFolderId != -1) {
            try {
                _nc.deleteGroupFolder(groupFolderId);
            } catch (Exception ex) {
                // best effort cleanup
            }
        }
    }

    @Test
    public void t01_testCreateGroupFolder() {
        if (_nc != null) {
            groupFolderId = _nc.createGroupFolder(TEST_GROUP_FOLDER);
            assertTrue(groupFolderId > 0);
        }
    }

    @Test
    public void t02_testGetGroupFolders() {
        if (_nc != null) {
            Optional<GroupFolderInfo> folder = findTestFolder();
            assertTrue(folder.isPresent());
            assertEquals(TEST_GROUP_FOLDER, folder.get().getMountPoint());
        }
    }

    @Test
    public void t03_testRenameGroupFolder() {
        if (_nc != null) {
            _nc.renameGroupFolder(groupFolderId, TEST_GROUP_FOLDER_RENAMED);
            GroupFolderInfo folder = getTestFolderById();
            assertNotNull(folder);
            assertEquals(TEST_GROUP_FOLDER_RENAMED, folder.getMountPoint());
        }
    }

    @Test
    public void t04_testGrantAccess() {
        if (_nc != null) {
            _nc.grantAccessToGroupFolder(groupFolderId, ADMIN_GROUP);
            GroupFolderInfo folder = getTestFolderById();
            assertNotNull(folder);
            assertTrue(folder.getAssignedGroups().containsKey(ADMIN_GROUP));
        }
    }

    @Test
    public void t05_testSetPermissions() {
        if (_nc != null) {
            // read + update
            _nc.setGroupFolderPermissions(groupFolderId, ADMIN_GROUP, 3);
            GroupFolderInfo folder = getTestFolderById();
            assertNotNull(folder);
            assertEquals(Integer.valueOf(3), folder.getAssignedGroups().get(ADMIN_GROUP));
        }
    }

    @Test
    public void t06_testSetQuota() {
        if (_nc != null) {
            long quota = 5L * 1024 * 1024 * 1024; // 5 GB
            _nc.setGroupFolderQuota(groupFolderId, quota);
            GroupFolderInfo folder = getTestFolderById();
            assertNotNull(folder);
            assertEquals(Long.valueOf(quota), folder.getQuota());
        }
    }

    @Test
    public void t07_testRevokeAccess() {
        if (_nc != null) {
            _nc.revokeAccessToGroupFolder(groupFolderId, ADMIN_GROUP);
            GroupFolderInfo folder = getTestFolderById();
            assertNotNull(folder);
            assertTrue(folder.getAssignedGroups() == null
                    || !folder.getAssignedGroups().containsKey(ADMIN_GROUP));
        }
    }

    @Test
    public void t08_testDeleteGroupFolder() {
        if (_nc != null) {
            _nc.deleteGroupFolder(groupFolderId);
            int deletedId = groupFolderId;
            groupFolderId = -1;
            assertTrue(_nc.getGroupFolders().stream().noneMatch(f -> f.getId() == deletedId));
        }
    }

    private Optional<GroupFolderInfo> findTestFolder() {
        return _nc.getGroupFolders().stream()
                .filter(f -> f.getId() != null && f.getId() == groupFolderId)
                .findFirst();
    }

    private GroupFolderInfo getTestFolderById() {
        return findTestFolder().orElse(null);
    }
}
