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

import java.util.Collection;
import org.aarboard.nextcloud.api.ServerConfig;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author a.schild
 */
public class FilesharingConnectorTest {
    
    private String serverName= null;
    private String userName= null;
    private String password= null;
    
    private ServerConfig _sc= null;
    
    public FilesharingConnectorTest() {
    }
    
    @Before
    public void setUp() {
        if (serverName != null)
        {
            _sc= new ServerConfig(serverName, true, 443, userName, password);
        }
    }

    /**
     * Test of getShares method, of class FilesharingConnector.
     */
    @Test
    public void testGetShares() throws Exception {
        System.out.println("getShares");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Collection<Share> result = instance.getShares();
            assertNotNull(result);
        }
    }
    
    /**
     * Test of getShares method, of class FilesharingConnector.
     */
    @Test
    public void testGetSharesOfPath() throws Exception {
        System.out.println("getSharesOfPath");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Collection<Share> result = instance.getShares("/Temp", false, false);
            assertNotNull(result);

            result = instance.getShares("/Temp", true, false);
            assertNotNull(result);

            result = instance.getShares("/Temp", false, true);
            assertNotNull(result);

            result = instance.getShares("/Temp", true, true);
            assertNotNull(result);
        }
    }

    /**
     * Test of getShareInfo method, of class FilesharingConnector.
     */
    @Test
    public void testGetShareInfo() throws Exception {
        System.out.println("getSharesOfPath");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.getShareInfo(8);
            assertNotNull(result);

            result = instance.getShareInfo(89989899);
            assertNull(result);
        }
    }
    
    
    /**
     * Test of shareFolder method, of class FilesharingConnector.
     */
    @Test
    public void testDoShare() throws Exception {
        System.out.println("shareFolder");
        if (_sc != null)
        {
            FilesharingConnector instance = new FilesharingConnector(_sc);
            Share result = instance.doShare("/Temp", ShareType.GROUP, "Project_123", null, null, null);
            assertNotNull(result);
        }
    }
    
}
