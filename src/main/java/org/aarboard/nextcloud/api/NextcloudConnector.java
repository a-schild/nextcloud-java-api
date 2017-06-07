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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.aarboard.nextcloud.api.filesharing.FilesharingConnector;
import org.aarboard.nextcloud.api.filesharing.Share;
import org.aarboard.nextcloud.api.filesharing.SharePermissions;
import org.aarboard.nextcloud.api.filesharing.ShareType;
import org.aarboard.nextcloud.api.filesharing.SharesXMLAnswer;
import org.aarboard.nextcloud.api.filesharing.SingleShareXMLAnswer;
import org.aarboard.nextcloud.api.provisioning.GroupsXMLAnswer;
import org.aarboard.nextcloud.api.provisioning.ProvisionConnector;
import org.aarboard.nextcloud.api.provisioning.User;
import org.aarboard.nextcloud.api.provisioning.UserData;
import org.aarboard.nextcloud.api.provisioning.UserXMLAnswer;
import org.aarboard.nextcloud.api.provisioning.UsersXMLAnswer;
import org.aarboard.nextcloud.api.utils.ListXMLAnswer;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.aarboard.nextcloud.api.webdav.Files;
import org.aarboard.nextcloud.api.webdav.Folders;

public class NextcloudConnector {

    private final ServerConfig    _serverConfig;
    private final ProvisionConnector pc;
    private final FilesharingConnector fc;
    private final Folders fd;
    private final Files fl;

    public NextcloudConnector(String serverName, boolean useHTTPS, int port, String userName, String password)
    {
        _serverConfig= new ServerConfig(serverName, useHTTPS, port, userName, password);
        pc= new ProvisionConnector(_serverConfig);
        fc= new FilesharingConnector(_serverConfig);
        fd= new Folders(_serverConfig);
        fl= new Files(_serverConfig);
    }

    public boolean createUser(String userId, String password)
    {
        return pc.createUser(userId, password);
    }

    public CompletableFuture<XMLAnswer> createUserAsync(String userId, String password)
    {
        return pc.createUserAsync(userId, password);
    }

    public boolean deleteUser(String userId)
    {
        return pc.deleteUser(userId);
    }

    public CompletableFuture<XMLAnswer> deleteUserAsync(String userId)
    {
        return pc.deleteUserAsync(userId);
    }

    public boolean enableUser(String userId)
    {
        return pc.enableUser(userId);
    }

    public CompletableFuture<XMLAnswer> enableUserAsync(String userId)
    {
        return pc.enableUserAsync(userId);
    }

    public boolean disableUser(String userId)
    {
        return pc.disableUser(userId);
    }

    public CompletableFuture<XMLAnswer> disableUserAsync(String userId)
    {
        return pc.disableUserAsync(userId);
    }

    public List<String> getGroupsOfUser(String userId) {
    	return pc.getGroupsOfUser(userId);
    }

    public CompletableFuture<GroupsXMLAnswer> getGroupsOfUserAsync(String userId) {
    	return pc.getGroupsOfUserAsync(userId);
    }

    public boolean addUserToGroup(String userId, String groupId)
    {
        return pc.addUserToGroup(userId, groupId);
    }

    public CompletableFuture<XMLAnswer> addUserToGroupAsync(String userId, String groupId)
    {
        return pc.addUserToGroupAsync(userId, groupId);
    }

    public boolean removeUserFromGroup(String userId, String groupId)
    {
        return pc.removeUserFromGroup(userId, groupId);
    }

    public CompletableFuture<XMLAnswer> removeUserFromGroupAsync(String userId, String groupId)
    {
        return pc.removeUserFromGroupAsync(userId, groupId);
    }

    public List<String> getSubadminGroupsOfUser(String userId) {
    	return pc.getSubadminGroupsOfUser(userId);
    }

    public CompletableFuture<ListXMLAnswer> getSubadminGroupsOfUserAsync(String userId) {
    	return pc.getSubadminGroupsOfUserAsync(userId);
    }

    public boolean promoteToSubadmin(String userId, String groupId)
    {
        return pc.promoteToSubadmin(userId, groupId);
    }

    public CompletableFuture<XMLAnswer> promoteToSubadminAsync(String userId, String groupId)
    {
        return pc.promoteToSubadminAsync(userId, groupId);
    }

    public boolean demoteSubadmin(String userId, String groupId)
    {
        return pc.demoteSubadmin(userId, groupId);
    }

    public CompletableFuture<XMLAnswer> demoteSubadminAsync(String userId, String groupId)
    {
        return pc.demoteSubadminAsync(userId, groupId);
    }

    public boolean sendWelcomeMail(String userId)
    {
        return pc.sendWelcomeMail(userId);
    }

    public CompletableFuture<XMLAnswer> sendWelcomeMailAsync(String userId) {
    	return pc.sendWelcomeMailAsync(userId);
    }

    public List<String> getMembersOfGroup(String userId) {
    	return pc.getMembersOfGroup(userId);
    }

    public CompletableFuture<UsersXMLAnswer> getMembersOfGroupAsync(String userId) {
    	return pc.getMembersOfGroupAsync(userId);
    }

    public List<String> getSubadminsOfGroup(String userId) {
    	return pc.getSubadminsOfGroup(userId);
    }

    public CompletableFuture<ListXMLAnswer> getSubadminsOfGroupAsync(String userId) {
    	return pc.getSubadminsOfGroupAsync(userId);
    }

    public boolean createGroup(String groupId)
    {
        return pc.createGroup(groupId);
    }

    public CompletableFuture<XMLAnswer> createGroupAsync(String groupId)
    {
        return pc.createGroupAsync(groupId);
    }

    public Collection<String> getUsers()
    {
        return pc.getUsers();
    }

    public Collection<String> getUsers(
            String search, int limit, int offset)
    {
        return pc.getUsers(search, limit, offset);
    }

    public CompletableFuture<UsersXMLAnswer> getUsersAsync()
    {
        return pc.getUsersAsync();
    }

    public CompletableFuture<UsersXMLAnswer> getUsersAsync(
            String search, int limit, int offset)
    {
        return pc.getUsersAsync(search, limit, offset);
    }

    public User getUser(String userId)
    {
        return pc.getUser(userId);
    }

    public CompletableFuture<UserXMLAnswer> getUserAsync(String userId)
    {
        return pc.getUserAsync(userId);
    }

    public boolean editUser(String userId, UserData key, String value)
    {
        return pc.editUser(userId, key, value);
    }

    public CompletableFuture<XMLAnswer> editUserAsync(String userId, UserData key, String value)
    {
        return pc.editUserAsync(userId, key, value);
    }

    public boolean deleteGroup(String groupId)
    {
        return pc.deleteGroup(groupId);
    }

    public CompletableFuture<XMLAnswer> deleteGroupAsync(String groupId)
    {
        return pc.deleteGroupAsync(groupId);
    }

    public CompletableFuture<GroupsXMLAnswer> getGroupsAsync()
    {
        return pc.getGroupsAsync();
    }

    public Collection<String> getGroups()
    {
        return pc.getGroups();
    }

    /**
     * Return matching group ids
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public Collection<String> getGroups(String search, int limit, int offset)
    {
        return pc.getGroups(search, limit, offset);
    }

    public CompletableFuture<GroupsXMLAnswer> getGroupsAsync(String search, int limit, int offset)
    {
        return pc.getGroupsAsync(search, limit, offset);
    }

    public List<String> getFolders(String path)
    {
        return fd.getFolders(path);
    }

    public boolean folderExists(String path)
    {
        return fd.exists(path);
    }

    public void createFolder(String path)
    {
        fd.createFolder(path);
    }

    public void deleteFolder(String path)
    {
        fd.deleteFolder(path);
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
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions)
    {
        return fc.doShare(path, shareType, shareWithUserOrGroupId, publicUpload, password, permissions);
    }

    public CompletableFuture<SingleShareXMLAnswer> doShareAsync(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions)
    {
        return fc.doShareAsync(path, shareType, shareWithUserOrGroupId, publicUpload, password, permissions);
    }

    public Collection<Share> getShares()
    {
        return fc.getShares();
    }

    public CompletableFuture<SharesXMLAnswer> getSharesAsync()
    {
        return fc.getSharesAsync();
    }

    /**
     * 
     * @param fileInputStream      inputstream of the file which should be uploaded
     * @param remotePath           path where the file should be uploaded to
     */
    public void uploadFile(InputStream fileInputStream, String remotePath)
    {
        fl.uploadFile(fileInputStream, remotePath);
    }

    /**
     * 
     * @param remotePath
     */
    public void removeFile(String path){
        fl.removeFile(path);
    }

    /**
     * Return if the file exists ore not
     *
     * @param path path to the file
     * @return boolean value whether the file exists or not
     */
    public boolean fileExists(String path){
        return fl.fileExists(path);
    }

    /**
     * Return all shares of this user
     * 
     * @param path      path to file/folder
     * @param reShares  returns not only the shares from the current user but all shares from the given file
     * @param subShares returns all shares within a folder, given that path defines a folder
     * @return 
     */
    public Collection<Share> getShares(String path, boolean reShares, boolean subShares)
    {
        return fc.getShares(path, reShares, subShares);
    }

    public CompletableFuture<SharesXMLAnswer> getSharesAsync(String path, boolean reShares, boolean subShares)
    {
        return fc.getSharesAsync(path, reShares, subShares);
    }

    /**
     * Return share info for a single share
     * 
     * @param shareId      id of share (Not path of share)
     * @return 
     */
    public Share getShareInfo(int shareId)
    {
        return fc.getShareInfo(shareId);
    }

    public CompletableFuture<SharesXMLAnswer> getShareInfoAsync(int shareId)
    {
        return fc.getShareInfoAsync(shareId);
    }
}
