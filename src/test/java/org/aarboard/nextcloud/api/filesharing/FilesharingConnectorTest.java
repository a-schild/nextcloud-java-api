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
package org.aarboard.nextcloud.api.filesharing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.aarboard.nextcloud.api.NextcloudConnector;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudOperationFailedException;
import org.aarboard.nextcloud.api.provisioning.ShareData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilesharingConnectorTest {
    private static final String TEST_FOLDER = "/sharing-test-folder";
    private static final String TESTUSER = "sharing-testuser";

    private static String serverName = null;
    private static String userName = null;
    private static String password = null;

    private static ServerConfig _sc = null;
    private static NextcloudConnector _nc = null;

    public FilesharingConnectorTest() {
    }

    @BeforeClass
    public static void setUp() {
        if (serverName != null)
        {
            _sc= new ServerConfig(serverName, true, 443, userName, password);
            _nc = new NextcloudConnector(serverName, true, 443, userName, password);
            _nc.createFolder(TEST_FOLDER);
            _nc.createUser(TESTUSER, "aBcDeFg123456");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (serverName != null)
        {
            _nc.deleteFolder(TEST_FOLDER);
            _nc.deleteUser(TESTUSER);
        }
    }

    @Test
    public void t01_testDoShare() {
        System.out.println("shareFolder");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.doShare(TEST_FOLDER, ShareType.USER, TESTUSER, null, null, null);
            assertNotNull(result);
        }
    }

    @Test
    public void t02_testGetShares() {
        System.out.println("getShares");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Collection<Share> result = instance.getShares();
            assertNotNull(result);
            assertTrue(result.stream().anyMatch(s -> TEST_FOLDER.equals(s.getPath()) && TESTUSER.equals(s.getShareWithId())));
        }
    }

    @Test
    public void t03_testGetSharesOfPath() {
        System.out.println("getSharesOfPath");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Collection<Share> result = instance.getShares(TEST_FOLDER, false, false);
            assertNotNull(result);

            Optional<Share> share = findShare(instance);
            assertTrue(share.isPresent());

            result = instance.getShares(TEST_FOLDER, true, false);
            assertNotNull(result);

            result = instance.getShares(TEST_FOLDER, false, true);
            assertNotNull(result);

            result = instance.getShares(TEST_FOLDER, true, true);
            assertNotNull(result);
        }
    }

    private Optional<Share> findShare(FilesharingConnector instance) {
        return instance.getShares(TEST_FOLDER, false, false).stream()
                .filter(s -> TEST_FOLDER.equals(s.getPath()) && TESTUSER.equals(s.getShareWithId())).findFirst();
    }

    @Test
    public void t04_testEditShare() {
        System.out.println("editShare");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            boolean result = instance.editShare(findShare(instance).get().getId(), ShareData.EXPIREDATE, "9999-01-01");
            assertTrue(result);
        }
    }

    @Test
    public void t05_testGetShareInfo() {
        System.out.println("getShareInfo");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.getShareInfo(findShare(instance).get().getId());
            assertNotNull(result);
            assertEquals(TEST_FOLDER, result.getPath());
            assertEquals(TESTUSER, result.getShareWithId());
            assertEquals(LocalDate.of(9999, 01, 01), result.getExpiration());

            try {
                instance.getShareInfo(89989899);
                fail("NextcloudOperationFailedException should be thrown!");
            } catch(NextcloudOperationFailedException ex) {
            }
        }
    }

    @Test
    public void t06_testDeleteShare() {
        System.out.println("deleteShare");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            boolean result = instance.deleteShare(findShare(instance).get().getId());
            assertTrue(result);
        }
    }
}
