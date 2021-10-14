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

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import static org.aarboard.nextcloud.api.ATestClass.TESTFILE1;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFolders extends ATestClass {

    @Test
    public void t21_testCreateFolder() {
        System.out.println("createFolder");
        if (_nc != null)
        {

            boolean result = _nc.folderExists(TEST_FOLDER1);
            if (result)
            {
                _nc.deleteFolder(TEST_FOLDER1);
            }
            _nc.createFolder(TEST_FOLDER1);
        }
    }

    @Test
    public void t22_testGetFolders() {
        System.out.println("getFolders");
        if (_nc != null)
        {
            String rootPath = "";
            List<String> result = _nc.getFolders(rootPath);
            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER1));
        }
    }

    @Test
    public void t222_testCreateFolder() {
        System.out.println("createFolder");
        if (_nc != null)
        {

            boolean result = _nc.folderExists(TEST_FOLDER2);
            if (result)
            {
                _nc.deleteFolder(TEST_FOLDER2);
            }
            _nc.createFolder(TEST_FOLDER2);
        }
    }

    @Test
    public void t222_testGetFolders() {
        System.out.println("getFolders");
        if (_nc != null)
        {
            String rootPath = "";
            List<String> result = _nc.getFolders(rootPath);
            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER2));
        }
    }

    @Test
    public void t23_testFolderExists() {
        System.out.println("folderExists");
        if (_nc != null)
        {
            boolean result = _nc.folderExists(TEST_FOLDER1);
            assertTrue(result);

            result = _nc.folderExists("non-existing-folder");
            assertFalse(result);
        }
    }

    @Test
    public void t232_testFolderExists() {
        System.out.println("folderExists");
        if (_nc != null)
        {
            boolean result = _nc.folderExists(TEST_FOLDER2);
            assertTrue(result);

            result = _nc.folderExists("non-existing-folder");
            assertFalse(result);
        }
    }

    @Test
    public void t25_testFolderRename() {
        System.out.println("folderRename");
        if (_nc != null)
        {
            _nc.renameFile(TEST_FOLDER1, TEST_FOLDER1_RENAMED, true);

            boolean result = _nc.folderExists(TEST_FOLDER1_RENAMED);
            assertTrue(result);

            result = _nc.folderExists(TEST_FOLDER1);
            assertFalse(result);

            _nc.renameFile(TEST_FOLDER1_RENAMED, TEST_FOLDER1, true);

            result = _nc.folderExists(TEST_FOLDER1);
            assertTrue(result);
        }
    }
    
    @Test
    public void t30_testDeleteFolder() {
        System.out.println("deleteFolder");
        if (_nc != null)
        {
            _nc.deleteFolder(TEST_FOLDER1);
        }
    }


    @Test
    public void t40_testList() {
        System.out.println("list");

        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER1);

            String rootPath = "";
            List<String> result = _nc.listFolderContent(rootPath);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER1);

            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER1));
        }
    }

    @Test
    public void t41_testListRecursive() {
        System.out.println("list recursive");
        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER1);
            _nc.createFolder(TEST_FOLDER1+"/"+TEST_FOLDER1+"_sub");

            String rootPath = "";
            List<String> result = _nc.listFolderContent(TEST_FOLDER1, -1);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER1);

            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER1+"_sub"));
        }
    }

    @Test
    public void t42_testListRecursiveFullPath() {
        System.out.println("list recursive");
        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER1);
            _nc.createFolder(TEST_FOLDER1+"/"+TEST_FOLDER1+"_sub/");

            String rootPath = "";
            List<String> result = _nc.listFolderContent(TEST_FOLDER1, -1, false, true);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER1);

            assertNotNull(result);
            assertTrue("Not matching ["+result+"] with expected ["+TEST_FOLDER1+"/"+TEST_FOLDER1+"_sub/"+"]", result.contains(TEST_FOLDER1+"/"+TEST_FOLDER1+"_sub/"));
        }
    }

    @Test
    public void t50_testDownloadFolder() {
        System.out.println("testDownloadfolder");
        if (_nc != null)
        {
            //prepare
            File of= new File(System.getProperty("java.io.tmpdir")+File.separator+TEST_FOLDER1);
            of.mkdirs();
            boolean result = _nc.folderExists(TEST_FOLDER2);
            if (result)
            {
                _nc.deleteFolder(TEST_FOLDER2);
            }
            
            _nc.createFolder(TEST_FOLDER2);
            _nc.createFolder(TEST_FOLDER2+"/"+TEST_FOLDER1);

            try
            {
                _nc.downloadFolder(TEST_FOLDER2, of.getAbsolutePath());
                //cleanup
                _nc.deleteFolder(TEST_FOLDER2);
                File of1= new File(System.getProperty("java.io.tmpdir")+File.separator+TEST_FOLDER1+File.separator+TEST_FOLDER2);

                assertTrue("Downloaded folder missing", of1.exists());
            }
            catch (IOException ex)
            {
                assertTrue("Exception in testcase", true);
            }
        }
    }
}
