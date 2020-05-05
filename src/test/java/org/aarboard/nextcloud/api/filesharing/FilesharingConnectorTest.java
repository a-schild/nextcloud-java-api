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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.aarboard.nextcloud.api.NextcloudConnector;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.TestHelper;
import org.aarboard.nextcloud.api.exception.NextcloudOperationFailedException;
import org.aarboard.nextcloud.api.filesharing.SharePermissions.SingleRight;
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
    private static final String TEST_FOLDER2 = "/sharing-test-folder2";
    private static final String TEST_FOLDER3 = "/sharing-test-folder3 + sign \u32A2";
    private static final String TESTUSER = "sharing-testuser";
    private static final String TESTUSER_EMAIL = "sharing@example.com";

    private static String serverName = null;
    private static String userName = null;
    private static String password = null;
    private static int    serverPort= 443;

    private static ServerConfig _sc = null;
    private static NextcloudConnector _nc = null;

    @BeforeClass
    public static void setUp() {
        TestHelper th= new TestHelper();
        serverName= th.getServerName();
        userName= th.getUserName();
        password= th.getPassword();
        serverPort= th.getServerPort();
        if (serverName != null)
        {
            _sc= new ServerConfig(serverName, serverPort == 443, serverPort, userName, password);
            _nc = new NextcloudConnector(serverName, serverPort == 443, serverPort, userName, password);
            _nc.createFolder(TEST_FOLDER);
            _nc.createFolder(TEST_FOLDER2);
            _nc.createFolder(TEST_FOLDER3);
            _nc.createUser(TESTUSER, "aBcDeFg123456");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (serverName != null)
        {
            _nc.deleteFolder(TEST_FOLDER);
            _nc.deleteFolder(TEST_FOLDER2);
            _nc.deleteFolder(TEST_FOLDER3);
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
    public void t02_testDoShareEmail() {
        System.out.println("shareFolderEmail");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.doShare(TEST_FOLDER, ShareType.EMAIL, TESTUSER_EMAIL, null, null, null);
            assertNotNull(result);
        }
    }

    @Test
    public void t03_testDoShare2() {
        System.out.println("shareFolder2");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            SharePermissions permissions = new SharePermissions(SingleRight.READ);
            Share result = instance.doShare(TEST_FOLDER2,ShareType.PUBLIC_LINK,"",true,"1234-myWebDav",permissions);
            assertNotNull(result);
        }
    }

    @Test
    public void t03_testDoShare3() {
        System.out.println("shareFolder3");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            SharePermissions permissions = new SharePermissions(SingleRight.READ);
            Share result = instance.doShare(TEST_FOLDER3,ShareType.PUBLIC_LINK,"",true,"1234-myWebDav",permissions);
            assertNotNull(result);
        }
    }

    
    @Test
    public void t03_testGetShares() {
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
    public void t05_testGetSharesOfPath() {
        System.out.println("getSharesOfPath");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Collection<Share> result = instance.getShares(TEST_FOLDER, false, false);
            assertNotNull(result);

            Optional<Share> share = findShare(instance);
            assertTrue(share.isPresent());
            assertEquals(LocalDate.of(9999, 1, 1), share.get().getExpiration());

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
    public void t06_testEditShare_Map() {
        System.out.println("editShare");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Map<ShareData, String> parameters = new HashMap<>();
            parameters.put(ShareData.EXPIREDATE, "9999-02-02");
            parameters.put(ShareData.PERMISSIONS, new SharePermissions(SingleRight.READ, SingleRight.UPDATE).toString());
            boolean result = instance.editShare(findShare(instance).get().getId(), parameters);
            assertTrue(result);
        }
    }

    @Test
    public void t07_testGetShareInfo() {
        System.out.println("getShareInfo");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.getShareInfo(findShare(instance).get().getId());
            assertNotNull(result);
            assertEquals(TEST_FOLDER, result.getPath());
            assertEquals(TESTUSER, result.getShareWithId());
            assertEquals(LocalDate.of(9999, 2, 2), result.getExpiration());
            assertTrue(result.getSharePermissions().hasRight(SingleRight.READ));
            assertTrue(result.getSharePermissions().hasRight(SingleRight.UPDATE));
            assertFalse(result.getSharePermissions().hasRight(SingleRight.CREATE));
            assertFalse(result.getSharePermissions().hasRight(SingleRight.DELETE));
            assertFalse(result.getSharePermissions().hasRight(SingleRight.SHARE));

            try {
                instance.getShareInfo(89989899);
                fail("NextcloudOperationFailedException should be thrown!");
            } catch(NextcloudOperationFailedException ex) {
            }
        }
    }

    @Test
    public void t08_testDeleteShare() {
        System.out.println("deleteShare");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            boolean result = instance.deleteShare(findShare(instance).get().getId());
            assertTrue(result);
        }
    }

}
