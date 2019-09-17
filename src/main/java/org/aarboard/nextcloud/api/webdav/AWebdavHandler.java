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
package org.aarboard.nextcloud.api.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import java.io.IOException;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author a.schild
 */
public abstract class AWebdavHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AWebdavHandler.class);

    public static final int  FILE_BUFFER_SIZE= 4096;
    private static final String WEB_DAV_BASE_PATH = "remote.php/webdav/";
    
    private final ServerConfig _serverConfig;

    public AWebdavHandler(ServerConfig serverConfig) {
        _serverConfig = serverConfig;
    }
    
    /**
     * Build the full URL for the webdav access to a resource
     * 
     * @param remotePath remote path for file (Not including remote.php/webdav/)
     * @return Full URL including http....
     */
    protected String buildWebdavPath(String remotePath)
    {
        URIBuilder uB= new URIBuilder()
        .setScheme(_serverConfig.isUseHTTPS() ? "https" : "http")
        .setHost(_serverConfig.getServerName())
        .setPort(_serverConfig.getPort())
        .setPath( WEB_DAV_BASE_PATH + remotePath);
        return uB.toString();
    }
    
    /**
     * Create a authenticate sardine connector
     * 
     * @return sardine connector to server including authentication
     */
    protected Sardine buildAuthSardine()
    {
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());
        
        return sardine;
    }
    
    /**
     * method to check if a remote object already exists
     *
     * @param remotePath path of the file/folder
     * @return boolean value if the given file/folder exists or not
     */
    public boolean pathExists(String remotePath) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            return sardine.exists(path);
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch (IOException ex)
            {
                LOG.warn("error in closing sardine connector", ex);
            }
        }
    }
    
    /**
     * Deletes the file/folder at the specified path
     *
     * @param remotePath path of the file/folder
     */
    public void deletePath(String remotePath)
    {
        String path=  buildWebdavPath( remotePath );

        Sardine sardine = buildAuthSardine();
        try {
            sardine.delete(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch (IOException ex)
            {
                LOG.warn("error in closing sardine connector", ex);
            }
        }
    }
}
