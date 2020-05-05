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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

            boolean result = _nc.folderExists(TEST_FOLDER);
            if (result)
            {
                _nc.deleteFolder(TEST_FOLDER);
            }
            _nc.createFolder(TEST_FOLDER);
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
            assertTrue(result.contains(TEST_FOLDER));
        }
    }

    @Test
    public void t23_testFolderExists() {
        System.out.println("folderExists");
        if (_nc != null)
        {
            boolean result = _nc.folderExists(TEST_FOLDER);
            assertTrue(result);

            result = _nc.folderExists("non-existing-folder");
            assertFalse(result);
        }
    }

    @Test
    public void t24_testDeleteFolder() {
        System.out.println("deleteFolder");
        if (_nc != null)
        {
            _nc.deleteFolder(TEST_FOLDER);
        }
    }


    @Test
    public void t28_testList() {
        System.out.println("list");

        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER);

            String rootPath = "";
            List<String> result = _nc.listFolderContent(rootPath);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER);

            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER));
        }
    }

    @Test
    public void t29_testListRecursive() {
        System.out.println("list recursive");
        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER);
            _nc.createFolder(TEST_FOLDER+"/"+TEST_FOLDER+"_sub");

            String rootPath = "";
            List<String> result = _nc.listFolderContent(TEST_FOLDER, -1);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER);

            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER+"_sub"));
        }
    }

    @Test
    public void t30_testListRecursiveFullPath() {
        System.out.println("list recursive");
        if (_nc != null)
        {
            //prepare
            _nc.createFolder(TEST_FOLDER);
            _nc.createFolder(TEST_FOLDER+"/"+TEST_FOLDER+"_sub/");

            String rootPath = "";
            List<String> result = _nc.listFolderContent(TEST_FOLDER, -1, false, true);

            //cleanup
            _nc.deleteFolder(TEST_FOLDER);

            assertNotNull(result);
            assertTrue(result.contains(TEST_FOLDER+"/"+TEST_FOLDER+"_sub/"));
        }
    }
}
