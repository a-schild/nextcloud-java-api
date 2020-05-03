/*
 * Copyright (C) 2018 a.schild
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

import org.junit.Ignore;

/**
 *
 * @author a.schild
 */
@Ignore
public class TestHelper {
    private String serverName= null;
    private String userName= null;
    private String password= null;
    private int     serverPort= 443;

    public TestHelper() {
        serverName= System.getProperty("nextcloud.api.test.servername");
        userName= System.getProperty("nextcloud.api.test.username");
        password= System.getProperty("nextcloud.api.test.password");
        String sPort= System.getProperty("nextcloud.api.test.serverport");
        if (sPort == null || sPort.isEmpty())
        {
            serverPort= 443;
        }
        else
        {
            serverPort= Integer.parseInt(sPort);
        }
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the serverPort
     */
    public int getServerPort() {
        return serverPort;
    }
}
