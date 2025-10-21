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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.aarboard.nextcloud.api.webdav.ResourceProperties;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFiles extends ATestClass {
    
    private final static String TEST1_FILE_CONTENT= "Content of Test 1 file";
    private final static String TEST2_FILE_CONTENT= "";
    private final static String TEST3_FILE_CONTENT= "Content of Test 3 file";
    private final static String TEST4_FILE_CONTENT= "";
    
    @Test
    public void t25_testUploadFile() {
        System.out.println("uploadFile ("+TESTFILE1+")");
        if (_nc != null)
        {
            InputStream inputStream = new ByteArrayInputStream(TEST1_FILE_CONTENT.getBytes());
            _nc.uploadFile(inputStream, TESTFILE1, false);
        }
    }

    @Test
    public void t25_2_testUploadFile() {
        System.out.println("uploadFile 0Bytes ("+TESTFILE2+")");
        if (_nc != null)
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST2_FILE_CONTENT.getBytes());
            _nc.uploadFile(inputStream, TESTFILE2, false);
        }
    }

    @Test
    public void t25_3_testUploadFile() {
        System.out.println("uploadFile umlauts ("+TESTFILE3+")");
        if (_nc != null)
        {
            InputStream inputStream = new ByteArrayInputStream(TEST3_FILE_CONTENT.getBytes());
            _nc.uploadFile(inputStream, TESTFILE3, false);
        }
    }

    @Test
    public void t25_4_testUploadFile() {
        System.out.println("uploadFile 0Bytes ("+TESTFILE4+")");
        if (_nc != null)
        {
            InputStream inputStream = new ByteArrayInputStream(TEST4_FILE_CONTENT.getBytes());
            _nc.uploadFile(inputStream, TESTFILE4, true);
        }
    }

    @Test
    public void t25_6_testUploadFile() {
        System.out.println("uploadFile 26 Bytes ("+TESTFILE6+")");
        if (_nc != null)
        {
            File srcFile= new File("src/test/resources/"+TESTFILE6);
            _nc.uploadFile(srcFile, TESTFILE6);
        }
    }

    @Test
    public void t25_7_testUploadFile() {
        System.out.println("uploadFile 26 Bytes ("+TESTFILE7+")");
        if (_nc != null)
        {
            File srcFile= new File("src/test/resources/"+TESTFILE7);
            _nc.uploadFile(srcFile, TESTFILE7);
        }
    }
    
    @Test
    public void t26_testFileExists() {
        System.out.println("fileExists ("+TESTFILE1+")");
        if (_nc != null)
        {
            boolean result = _nc.fileExists(TESTFILE1);
            assertTrue(result);

            result = _nc.fileExists("non-existing-file");
            assertFalse(result);
        }
    }

    @Test
    public void t26_2_testFileExists2() {
        System.out.println("fileExists2 ("+TESTFILE3+")");
        if (_nc != null)
        {
            boolean result = _nc.fileExists(TESTFILE3);
            assertTrue(result);
        }
    }

    @Test
    public void t27_2_1_testFolderProperties() {
        System.out.println("folderProperties all (/)");
        if (_nc != null)
        {
            try
            {
                ResourceProperties props= _nc.getProperties("/", true);
                Assert.assertNotNull("Properties returned", props);
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }
    
    @Test
    public void t26_2_testFileExists4() {
        System.out.println("fileExists4 ("+TESTFILE4+")");
        if (_nc != null)
        {
            boolean result = _nc.fileExists(TESTFILE4);
            assertTrue(result);
        }
    }

    @Test
    public void t26_2_testFileExists7() {
        System.out.println("fileExists7 ("+TESTFILE7+")");
        if (_nc != null)
        {
            boolean result = _nc.fileExists(TESTFILE7);
            assertTrue(result);
        }
    }

    @Test
    public void t27_1_testFileProperties() {
        System.out.println("fileProperties basic ("+TESTFILE1+")");
        if (_nc != null)
        {
            try
            {
                _nc.getProperties(TESTFILE1, false);
                //assertTrue(result);
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }

    @Test
    public void t27_2_testFileProperties() {
        System.out.println("fileProperties basic ("+TESTFILE2+")");
        if (_nc != null)
        {
            try
            {
                _nc.getProperties(TESTFILE2, false);
                //assertTrue(result);
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }

    @Test
    public void t27_2_1_testFileProperties() {
        System.out.println("fileProperties all ("+TESTFILE2+")");
        if (_nc != null)
        {
            try
            {
                _nc.getProperties(TESTFILE2, true);
                //assertTrue(result);
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }

    @Test
    public void t27_2_2_testFileProperties() {
        System.out.println("fileProperties partial/all ("+TESTFILE2+")");
        if (_nc != null)
        {
            try
            {
                ResourceProperties propsPartial= _nc.getProperties(TESTFILE2, false);
                ResourceProperties propsFull= _nc.getProperties(TESTFILE2, true);
                assertTrue("ETAG is not identic", propsPartial.getEtag().equals(propsFull.getEtag()));
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }

    @Test
    public void t27_2_6_testFileProperties() {
        System.out.println("fileProperties all ("+TESTFILE6+")");
        if (_nc != null)
        {
            try
            {
                ResourceProperties props= _nc.getProperties(TESTFILE6, true);
                assertNotNull("Retval is null", props);
            }
            catch (IOException ex)
            {
                assertFalse(ex.getMessage(), false);
            }
        }
    }

    
    @Test
    public void t27_99_testFileProperties() {
        System.out.println("fileProperties not existing");
        if (_nc != null)
        {
            try
            {
                _nc.getProperties(TESTFILE2+"-not-existing", false);
                fail("Resource should throw 404 error");
            }
            catch (Exception ex)
            {
              assertEquals(
                  "com.github.sardine.impl.SardineException: status code: 404, reason phrase: Unexpected response (404 Not Found)",
                  ex.getMessage());
            }
        }
    }



    @Test
    public void t28_3_testDowloadFile() throws IOException {
        System.out.println("downloadFile umlauts as InputStream ("+TESTFILE3+")");
        if (_nc != null)
        {
            InputStream is= _nc.downloadFile(TESTFILE3);
            int nCount= 0;
            while (is.read() != -1)
            {
                nCount++;
            }
            is.close();
            assertEquals("Downloaded content size does not match", nCount,
              TEST3_FILE_CONTENT.length());
        }
    }

    @Test
    public void t29_3_testDowloadFile() throws IOException {
        System.out.println("downloadFile umlauts as File ("+TESTFILE1+")");
        if (_nc != null)
        {
            File of= new File(System.getProperty("java.io.tmpdir")+File.separator+TESTFILE1);
            if (_nc.downloadFile(TESTFILE1, of.getParent()))
            {
                assertEquals("Downloaded content size does not match", of.length(),
                  TEST1_FILE_CONTENT.length());
            }
            else
            {
                fail("Downloaded file failed");
            }
            if (of.exists())
            {
                of.delete();
            }
        }
    }

    @Test
    public void t29_3_testDowloadFile4() throws IOException {
        System.out.println("downloadFile  as File ("+TESTFILE4+")");
        if (_nc != null)
        {
            File of= new File(System.getProperty("java.io.tmpdir")+File.separator+TESTFILE4);
            if (_nc.downloadFile(TESTFILE4, of.getParent()))
            {
                assertEquals("Downloaded content size does not match", 0, of.length());
            }
            else
            {
                fail("Downloaded file failed");
            }
            if (of.exists())
            {
                of.delete();
            }
        }
    }

    @Test
    public void t29_6_testDowloadFile6() throws IOException {
        System.out.println("downloadFile  as File ("+TESTFILE6+")");
        if (_nc != null)
        {
            File of= new File(System.getProperty("java.io.tmpdir")+File.separator+TESTFILE6);
            if (_nc.downloadFile(TESTFILE6, of.getParent()))
            {
                File srcFile= new File("src/test/resources/"+TESTFILE6);
                assertEquals("Downloaded content size does not match", of.length(), srcFile.length());
            }
            else
            {
                fail("Downloaded file failed");
            }
            if (of.exists())
            {
                of.delete();
            }
        }
    }

    @Test
    public void t29_6_testDowloadFile7() throws IOException {
        System.out.println("downloadFile  as File ("+TESTFILE7+")");
        if (_nc != null)
        {
            File of= new File(System.getProperty("java.io.tmpdir")+File.separator+TESTFILE7);
            if (_nc.downloadFile(TESTFILE7, of.getParent()))
            {
                File srcFile= new File("src/test/resources/"+TESTFILE7);
                assertEquals("Downloaded content size does not match", of.length(), srcFile.length());
            }
            else
            {
                fail("Downloaded file failed");
            }
            if (of.exists())
            {
                of.delete();
            }
        }
    }
    
    @Test
    public void t31_testRenameFile() {
        System.out.println("renameFile 31 ("+TESTFILE1+") to ("+TESTFILE1_RENAMED+")");
        if (_nc != null)
        {
            _nc.renameFile(TESTFILE1, TESTFILE1_RENAMED, true);
            
            boolean result = _nc.folderExists(TESTFILE1_RENAMED);
            assertTrue(result);

            result = _nc.folderExists(TESTFILE1);
            assertFalse(result);
            
            _nc.renameFile(TESTFILE1_RENAMED, TESTFILE1, true);
            
            result = _nc.folderExists(TESTFILE1);
            assertTrue(result);
        }
    }
    
    @Test
    public void t99_testRemoveFile() {
        System.out.println("removeFile 99 ("+TESTFILE1+")");
        if (_nc != null)
        {
            _nc.removeFile(TESTFILE1);
        }
    }

    @Test
    public void t99_2_testRemoveFile() {
        System.out.println("removeFile 99 2("+TESTFILE2+")");
        if (_nc != null)
        {
            _nc.removeFile(TESTFILE2);
        }
    }
    
    @Test
    public void t99_3_testRemoveFile() {
        System.out.println("removeFile 99 3("+TESTFILE3+")");
        if (_nc != null)
        {
            _nc.removeFile(TESTFILE3);
        }
    }
    
    @Test
    public void t99_4_testRemoveFile() {
        System.out.println("removeFile 99 4 ("+TESTFILE4+")");
        if (_nc != null)
        {
            _nc.removeFile(TESTFILE4);
        }
    }

    @Test
    public void t99_6_testRemoveFile() {
        System.out.println("removeFile 99 6 ("+TESTFILE6+")");
        if (_nc != null)
        {
            _nc.removeFile(TESTFILE6);
        }
    }
}
