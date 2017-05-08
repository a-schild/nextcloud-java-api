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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.aarboard.nextcloud.api.filesharing.FilesharingConnector;
import org.aarboard.nextcloud.api.filesharing.Share;
import org.aarboard.nextcloud.api.filesharing.SharePermissions;
import org.aarboard.nextcloud.api.filesharing.ShareType;
import org.aarboard.nextcloud.api.provisioning.Group;
import org.aarboard.nextcloud.api.provisioning.ProvisionConnector;
import org.aarboard.nextcloud.api.provisioning.User;
import org.aarboard.nextcloud.api.webdav.Folders;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

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
    
    public Collection<User> getUsers() throws Exception
    {
        ProvisionConnector pc= new ProvisionConnector(_serverConfig);
        return pc.getUsers();
    }
    
    public Collection<User> getUsers(
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

    
    public Collection<Group> getGroups() throws Exception
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
    public Collection<Group> getGroups(String search, int limit, int offset) throws Exception
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
    
    /**
     * 
     * @param path                  path to the file/folder which should be shared
     * @param shareType             0 = user; 1 = group; 3 = public link; 6 = federated cloud share
     * @param shareWithUserOrGroupId user / group id with which the file should be shared
     * @param publicUpload          allow public upload to a public shared folder (true/false)
     * @param password              password to protect public link Share with
     * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
     * @return new Share ID if success
     * @throws Exception 
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions) throws Exception
    {
        FilesharingConnector fc= new FilesharingConnector(_serverConfig);
        return fc.doShare(path, shareType, shareWithUserOrGroupId, publicUpload, password, permissions);
    }
    
    public Collection<Share> getShares() throws Exception
    {
        FilesharingConnector fc= new FilesharingConnector(_serverConfig);
        return fc.getShares();
    }    
    
    /**
     * 
     * @param fileInputStream      inputstream of the file which should be uploaded
     * @param remotePath           path where the file should be uploaded to
     * @throws Exception
     */
    public void uploadFile( FileInputStream fileInputStream, String remotePath) throws Exception
    {
 		String password = _serverConfig.getPassword();
 		String username = _serverConfig.getUserName();
 		String host = _serverConfig.getServerName();
 		
 		Sardine sardine = SardineFactory.begin(username, password);
 		sardine.enablePreemptiveAuthentication(host);

 		sardine.put(remotePath, fileInputStream);
    }

    /**
     * Return all shares of this user
     * 
     * @param path      path to file/folder
     * @param reShares  returns not only the shares from the current user but all shares from the given file
     * @param subShares returns all shares within a folder, given that path defines a folder
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<Share> getShares(String path, boolean reShares, boolean subShares) throws Exception
    {
        FilesharingConnector fc= new FilesharingConnector(_serverConfig);
        return fc.getShares(path, reShares, subShares);
    }    
    
    /**
     * Return share info for a single share
     * 
     * @param shareId      id of chare (Not path of share)
     * @return 
     * @throws java.lang.Exception 
     */
    public Share getShareInfo(int shareId) throws Exception
    {
        FilesharingConnector fc= new FilesharingConnector(_serverConfig);
        return fc.getShareInfo(shareId);
    }    
    
}
