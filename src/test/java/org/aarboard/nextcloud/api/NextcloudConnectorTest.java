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

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

import org.aarboard.nextcloud.api.provisioning.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author a.schild
 */
public class NextcloudConnectorTest {
    
    private String serverName= null;
    private String userName= null;
    private String password= null;
    
    private NextcloudConnector _nc;
    
    public NextcloudConnectorTest() {
    }
    
    @Before
    public void setUp() {
        if (serverName != null)
        {
            _nc= new NextcloudConnector(serverName, true, 0, userName, password);
        }
    }

    /**
     * Test of getUsers method, of class Connector.
     */
    @Test
    public void testGetUsers() throws Exception {
        System.out.println("getUsers");
        if (_nc != null)
        {
            Collection<String> result = _nc.getUsers();
            assertNotNull(result);
        }
    }

    /**
     * Test of getUsers method, of class Connector.
     */
    @Test
    public void testGetUsers_3args() throws Exception {
        System.out.println("getUsers");
        if (_nc != null)
        {
            String search = null;
            int limit = -1;
            int offset = -1;
            Collection<String> result = _nc.getUsers(search, limit, offset);
            assertNotNull(result);
        }
    }
    
    /**
     * Test of getUsers method, of class Connector.
     */
    @Test
    public void testGetGroups_3args() throws Exception {
        System.out.println("getGroups");
        if (_nc != null)
        {
            String search = null;
            int limit = -1;
            int offset = -1;
            Collection<String> result = _nc.getGroups(search, limit, offset);
            assertNotNull(result);
        }
    }

    /**
     * Test of getUsers method, of class Connector.
     */
    @Test
    public void testGetUsers_0args() throws Exception {
        System.out.println("getUsers");
        if (_nc != null)
        {
            Collection<String> result = _nc.getUsers();
            assertNotNull(result);
        }
    }

    /**
     * Test of getUser method, of class Connector.
     */
    @Test
    public void testGetUser() throws Exception {
        System.out.println("getUser");
        if (_nc != null)
        {
            User result = _nc.getUser("admin");
            assertNotNull(result);
        }
    }

    /**
     * Test of createGroup method, of class Connector.
     */
    @Test
    public void testCreateGroup() throws Exception {
        System.out.println("createGroup");
        if (_nc != null)
        {
            String groupId = "nextG1";
            boolean expResult = true;
            boolean result = _nc.createGroup(groupId);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of createGroup method, of class Connector.
     */
    @Test
    public void testDeleteGroup() throws Exception {
        System.out.println("deleteGroup");
        if (_nc != null)
        {
            String groupId = "nextG1";
            boolean expResult = true;
            boolean result = _nc.deleteGroup(groupId);
            assertEquals(expResult, result);
        }
    }
    
    /**
     * Test of getGroups method, of class Connector.
     */
    @Test
    public void testGetGroups_0args() throws Exception {
        System.out.println("getGroups");
        if (_nc != null)
        {
            Collection<String> result = _nc.getGroups();
            assertNotNull(result);
        }
    }
    
    /**
     * Test of getFolders method, of class Folders.
     */
    @Test
    public void testGetFolders() throws Exception {
        System.out.println("getFolders");
        if (_nc != null)
        {
            String rootPath = "";
            List<String> result = _nc.getFolders(rootPath);
            assertNotNull(result);
        }
    }

    /**
     * Test of exists method, of class Folders.
     */
    @Test
    public void testExists() throws Exception {
        System.out.println("exists");
        if (_nc != null)
        {
            String rootPath = "clientsync";
            boolean expResult = true;
            boolean result = _nc.folderExists(rootPath);
            assertEquals(expResult, result);


            rootPath = "clientsync-no-exist";
            expResult = false;
            result = _nc.folderExists(rootPath);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of createFolder method, of class Folders.
     */
    @Test
    public void testCreateFolder() throws Exception {
        System.out.println("createFolder");
        if (_nc != null)
        {
            String rootPath = "new-test-folder";
            _nc.createFolder(rootPath);
            _nc.deleteFolder(rootPath);
        }
    }
    
    
    /**
     * Test of fileExists method, of class Files.
     */
    @Test
    public void testFileExists() throws Exception {
        System.out.println("fileExists");
        if (_nc != null)
        {
            String rootPath = "test.txt";
            boolean expResult = true;
            boolean result = _nc.fileExists(rootPath);
            assertEquals(expResult, result);


            rootPath = "clientsync-no-exist";
            expResult = false;
            result = _nc.fileExists(rootPath);
            assertEquals(expResult, result);
        }
    }
    
    /**
     * Test of uploadFile method, of class Files
     */
    @Test
    public void testUploadFile() throws Exception {
    	System.out.println("uploadFile");
    	if(_nc != null)
    	{
    		String pathTo = null;
    		String fileName = "test.txt";
			FileInputStream fileInputStream = new FileInputStream(pathTo + fileName);
			String remotePath = fileName; 
			_nc.uploadFile(fileInputStream, remotePath);
    	}
    }
    
    /**
     * Test of removeFile method, of class Files
     */
    @Test
    public void testRemoveFile() throws Exception {
    	System.out.println("uploadFile");
    	if(_nc != null)
    	{
    		String fileName = "test.txt";
			String remotePath = fileName; 
			_nc.removeFile(remotePath);
    	}
    }
    
}
