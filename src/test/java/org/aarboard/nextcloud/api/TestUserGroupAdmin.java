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
package org.aarboard.nextcloud.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.aarboard.nextcloud.api.provisioning.User;
import org.aarboard.nextcloud.api.provisioning.UserData;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUserGroupAdmin extends ATestClass {
    
    @Test
    public void t01_testCreateUser() {
        System.out.println("createUser");
        if (_nc != null)
        {
            boolean result = _nc.createUser(TESTUSER, "aBcDeFg123456");
            assertTrue(result);
        }
    }

    @Test
    public void t02_testGetUsers() {
        System.out.println("getUsers");
        if (_nc != null)
        {
            Collection<String> result = _nc.getUsers();
            assertNotNull(result);
            assertTrue(result.contains(TESTUSER));
        }
    }

    @Test
    public void t03_testGetUsers_3args() {
        System.out.println("getUsers");
        if (_nc != null)
        {
            String search = TESTUSER;
            int limit = 1;
            int offset = -1;
            Collection<String> result = _nc.getUsers(search, limit, offset);
            assertNotNull(result);
            assertTrue(result.contains(TESTUSER));
        }
    }

    @Test
    public void t04_testEditUser() {
        System.out.println("editUser");
        if (_nc != null)
        {
            boolean result = _nc.editUser(TESTUSER, UserData.TWITTER, "test");
            assertTrue(result);
        }
    }

    @Test
    public void t05_testDisableUser() {
        System.out.println("disableUser");
        if (_nc != null)
        {
            boolean result = _nc.disableUser(TESTUSER);
            assertTrue(result);
        }
    }

    @Test
    public void t06_testGetUser() {
        System.out.println("getUser");
        if (_nc != null)
        {
            User result = _nc.getUser(TESTUSER);
            assertNotNull(result);
            assertEquals(TESTUSER, result.getId());
            assertEquals("test", result.getTwitter());
            assertFalse(result.isEnabled());
        }
    }

    @Test
    public void t07_testEnableUser() {
        System.out.println("enableUser");
        if (_nc != null)
        {
            boolean result = _nc.enableUser(TESTUSER);
            assertTrue(result);
        }
    }

    @Test
    public void t08_testCreateGroup() {
        System.out.println("createGroup");
        if (_nc != null)
        {
            String groupId = TESTGROUP;
            boolean result = _nc.createGroup(groupId);
            assertTrue(result);
        }
    }

    @Test
    public void t09_testGetGroups_0args() {
        System.out.println("getGroups");
        if (_nc != null)
        {
            Collection<String> result = _nc.getGroups();
            assertNotNull(result);
            assertTrue(result.contains(TESTGROUP));
        }
    }

    @Test
    public void t10_testGetGroups_3args() {
        System.out.println("getGroups");
        if (_nc != null)
        {
            String search = TESTGROUP;
            int limit = 1;
            int offset = -1;
            Collection<String> result = _nc.getGroups(search, limit, offset);
            assertNotNull(result);
            assertTrue(result.contains(TESTGROUP));
        }
    }

    @Test
    public void t11_testAddUserToGroup() {
        System.out.println("addUserToGroup");
        if (_nc != null)
        {
            boolean result = _nc.addUserToGroup(TESTUSER, TESTGROUP);
            assertTrue(result);
        }
    }

    @Test
    public void t12_testGetGroupsOfUser() {
        System.out.println("getGroupsOfUser");
        if (_nc != null)
        {
            List<String> result = _nc.getGroupsOfUser(TESTUSER);
            assertNotNull(result);
            assertTrue(result.contains(TESTGROUP));
        }
    }

    @Test
    public void t13_testGetMembersOfGroup() {
        System.out.println("getMembersOfGroup");
        if (_nc != null)
        {
            List<String> result = _nc.getMembersOfGroup(TESTGROUP);
            assertNotNull(result);
            assertTrue(result.contains(TESTUSER));
        }
    }

    @Test
    public void t14_testPromoteToSubadmin() {
        System.out.println("promoteToSubadmin");
        if (_nc != null)
        {
            boolean result = _nc.promoteToSubadmin(TESTUSER, TESTGROUP);
            assertTrue(result);
        }
    }

    @Test
    public void t15_testGetSubadminGroupsOfUser() {
        System.out.println("getSubadminGroupsOfUser");
        if (_nc != null)
        {
            List<String> result = _nc.getSubadminGroupsOfUser(TESTUSER);
            assertNotNull(result);
            assertTrue(result.contains(TESTGROUP));
        }
    }

    @Test
    public void t16_testGetSubadminsOfGroup() {
        System.out.println("getSubadminsOfGroup");
        if (_nc != null)
        {
            List<String> result = _nc.getSubadminsOfGroup(TESTGROUP);
            assertNotNull(result);
            assertTrue(result.contains(TESTUSER));
        }
    }

    @Test
    public void t17_testDemoteSubadmin() {
        System.out.println("demoteSubadmin");
        if (_nc != null)
        {
            boolean result = _nc.demoteSubadmin(TESTUSER, TESTGROUP);
            assertTrue(result);
        }
    }

    @Test
    public void t18_testRemoveUserFromGroup() {
        System.out.println("removeUserFromGroup");
        if (_nc != null)
        {
            boolean result = _nc.removeUserFromGroup(TESTUSER, TESTGROUP);
            assertTrue(result);
        }
    }

    @Test
    public void t19_testDeleteUser() {
        System.out.println("deleteUser");
        if (_nc != null)
        {
            boolean result = _nc.deleteUser(TESTUSER);
            assertTrue(result);
        }
    }

    @Test
    public void t20_testDeleteGroup() {
        System.out.println("deleteGroup");
        if (_nc != null)
        {
            String groupId = TESTGROUP;
            boolean result = _nc.deleteGroup(groupId);
            assertTrue(result);
        }
    }

}
