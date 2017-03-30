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

import java.util.Collection;
import java.util.List;
import org.aarboard.nextcloud.api.provisioning.ProvisionConnector;
import org.aarboard.nextcloud.api.webdav.Folders;

public class NextcloudConnector {
    
    private final ServerConfig    _serverConfig;
    
    public NextcloudConnector(String serverName, boolean useHTTPS, int port, String userName, String password ) 
    {
        _serverConfig= new ServerConfig(serverName, useHTTPS, port, userName, password);
    }
    
    public boolean createGroup(String groupId) throws Exception    
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.createGroup(groupId);
    }
    
    public Collection<String> getUsers() throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.getUsers();
    }
    
    public Collection<String> getUsers(
            String search, int limit, int offset) throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.getUsers(search, limit, offset);
    }
    
    
    public boolean deleteGroup(String groupId) throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.deleteGroup(groupId);
    }

    
    public Collection<String> getGroups() throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.getGroups();
    }
    
    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public Collection<String> getGroups(String search, int limit, int offset) throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.getGroups(search, limit, offset);
    }

    public List<String> getFolders(String path) throws Exception
    {
        Folders fc= new Folders(_serverConfig);
        return fc.getFolders(path);
    }
    
    public boolean folderExists(String path)  throws Exception
    {
        Folders fc= new Folders(_serverConfig);
        return fc.exists(path);
    }

    public void createFolder(String path) throws Exception
    {
        Folders fc= new Folders(_serverConfig);
        fc.createFolder(path);
    }

    public void deleteFolder(String path) throws Exception
    {
        Folders fc= new Folders(_serverConfig);
        fc.deleteFolder(path);
    }
    
}
