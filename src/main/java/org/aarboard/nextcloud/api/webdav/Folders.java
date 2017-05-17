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
package org.aarboard.nextcloud.api.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

/**
 *
 * @author a.schild
 */
public class Folders {
    
    private final String WEB_DAV_BASE_PATH= "remote.php/webdav/";

    private final ServerConfig  _serverConfig;

    public Folders(ServerConfig _serverConfig) {
        this._serverConfig = _serverConfig;
    }

    
    public List<String> getFolders(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;
        
        List<String> retVal= new LinkedList<>();
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        List<DavResource> resources;
        try {
            resources = sardine.list(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
        for (DavResource res : resources)
        {
            retVal.add(res.getName());
        }
        return retVal;
    }
    
    public boolean exists(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;
        
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            return sardine.exists(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public void createFolder(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;
        
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public void deleteFolder(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;
        
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            sardine.delete(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
}

