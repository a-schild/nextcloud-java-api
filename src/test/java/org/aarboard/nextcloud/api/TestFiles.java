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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            InputStream inputStream = new ByteArrayInputStream(TEST2_FILE_CONTENT.getBytes());
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
            assertTrue("Downloaded content size does not match", nCount == TEST3_FILE_CONTENT.length());
        }
    }

    @Test
    public void t29_3_testDowloadFile() throws IOException {
        System.out.println("downloadFile umlauts as File ("+TESTFILE1+")");
        if (_nc != null)
        {
            File of= new File(System.getProperty("java.io.tmpdir")+TESTFILE1);
            if (_nc.downloadFile(TESTFILE1, of.getParent()))
            {
                assertTrue("Downloaded content size does not match", of.length() == TEST1_FILE_CONTENT.length());
            }
            else
            {
                assertTrue("Downloaded file failed", false);
            }
            if (of.exists())
            {
                of.delete();
            }
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

}
