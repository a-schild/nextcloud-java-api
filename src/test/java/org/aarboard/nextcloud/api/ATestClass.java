/*
 * Copyright (C) 2019 andre
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

import org.junit.AfterClass;
import org.junit.Before;

/**
 *
 * @author andre
 */
public class ATestClass {
    public static final String TESTUSER = "testuser";
    public static final String TESTGROUP = "testgroup";
    public static final String TEST_FOLDER = "new-test-folder";
    public static final String TESTFILE1 = "test1.txt";
    public static final String TESTFILE2 = "test2.txt";
    public static final String TESTFILE3 = "test3-äöüÖÄÜ and with plus + sign \u32A2.txt";
    public static final String TESTFILE4 = "test4.txt";
    public static final String TESTFILE6 = "test6.txt";

    public static String serverName = null;
    public static String userName = null;
    public static String password = null;
    public static int    serverPort= 443;

    public static NextcloudConnector _nc;

    @Before
    public void setUp() {
        TestHelper th= new TestHelper();
        serverName= th.getServerName();
        userName= th.getUserName();
        password= th.getPassword();
        serverPort= th.getServerPort();
        if (serverName != null)
        {
            _nc = new NextcloudConnector(serverName, serverPort == 443, serverPort, userName, password);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (serverName != null)
        {
            try
            {
                _nc.removeFile(TESTFILE1);
            }
            catch (Exception ex)
            {
                // Catch any errors
            }
            try
            {
                _nc.removeFile(TESTFILE2);
            }
            catch (Exception ex)
            {
                // Catch any errors
            }
            try
            {
                _nc.removeFile(TESTFILE3);
            }
            catch (Exception ex)
            {
                // Catch any errors
            }
        }
    }
    
    
}
