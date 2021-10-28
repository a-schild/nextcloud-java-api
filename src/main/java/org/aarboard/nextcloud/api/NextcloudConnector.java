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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.aarboard.nextcloud.api.config.ConfigConnector;
import org.aarboard.nextcloud.api.filesharing.FilesharingConnector;
import org.aarboard.nextcloud.api.filesharing.Share;
import org.aarboard.nextcloud.api.filesharing.SharePermissions;
import org.aarboard.nextcloud.api.filesharing.ShareType;
import org.aarboard.nextcloud.api.filesharing.SharesXMLAnswer;
import org.aarboard.nextcloud.api.filesharing.SingleShareXMLAnswer;
import org.aarboard.nextcloud.api.provisioning.*;
import org.aarboard.nextcloud.api.utils.*;
import org.aarboard.nextcloud.api.webdav.Files;
import org.aarboard.nextcloud.api.webdav.Folders;
import org.aarboard.nextcloud.api.webdav.ResourceProperties;
import org.aarboard.nextcloud.api.webdav.pathresolver.NextcloudVersion;
import org.aarboard.nextcloud.api.webdav.pathresolver.WebDavPathResolver;
import org.aarboard.nextcloud.api.webdav.pathresolver.WebDavPathResolverBuilder;

public class NextcloudConnector {

    private final ServerConfig _serverConfig;
    private final ProvisionConnector pc;
    private final FilesharingConnector fc;
    private final ConfigConnector cc;
    private final Folders fd;
    private final Files fl;

    /**
     * 
     * @param serverName dns name of server or IP address
     * @param useHTTPS   Use https instead of http?
     * @param port       On which port is the NC server running
     * @param loginName  User login name
     * @param password   User password
     */
    public NextcloudConnector(String serverName, boolean useHTTPS, int port, String loginName, String password) {
        this(serverName, useHTTPS, port, new AuthenticationConfig(loginName, password));
    }

    /**
     *
     * @param serverName Name or IP of server of your nextcloud instance
     * @param useHTTPS Set true when https should be used
     * @param port Use 443 for https and 80 for non-https in most cases
     * @param bearerToken Bearer token for login
     */
    public NextcloudConnector(String serverName, boolean useHTTPS, int port, String bearerToken) {
        this(serverName, useHTTPS, port, new AuthenticationConfig(bearerToken));
    }

    /**
     * @param serviceUrl url of the nextcloud instance, e.g.
     * https://nextcloud.instance.com:8443/cloud
     * @param loginName User for login
     * @param password Password for login
     */
    public NextcloudConnector(String serviceUrl, String loginName, String password) {
        this(serviceUrl, new AuthenticationConfig(loginName, password));
    }

    /**
     * @param serviceUrl url of the nextcloud instance, e.g.
     * https://nextcloud.instance.com:8443/cloud
     * @param bearerToken Bearer token for login
     */
    public NextcloudConnector(String serviceUrl, String bearerToken) {
        this(serviceUrl, new AuthenticationConfig(bearerToken));
    }

    /**
     * @param serviceUrl url of the nextcloud instance, e.g.
     * https://nextcloud.instance.com:8443/cloud
     * @param authenticationConfig Authentication config
     */
    public NextcloudConnector(String serviceUrl, AuthenticationConfig authenticationConfig) {
        try {
            URL _serviceUrl = new URL(serviceUrl);
            boolean useHTTPS = serviceUrl.startsWith("https");
            _serverConfig = new ServerConfig(_serviceUrl.getHost(), useHTTPS, _serviceUrl.getPort(),
                    authenticationConfig);
            if (!_serviceUrl.getPath().isEmpty()) {
                _serverConfig.setSubPathPrefix(_serviceUrl.getPath());
            }
            pc = new ProvisionConnector(_serverConfig);
            fc = new FilesharingConnector(_serverConfig);
            cc = new ConfigConnector(_serverConfig);
            fd = new Folders(_serverConfig);
            fl = new Files(_serverConfig);

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     *
     * @param serverName Name or IP of server of your nextcloud instance
     * @param useHTTPS Set true when https should be used
     * @param port Use 443 for https and 80 for non-https in most cases
     * @param authenticationConfig Authentication configuration for login
     */
    public NextcloudConnector(String serverName, boolean useHTTPS, int port,
            AuthenticationConfig authenticationConfig) {
        _serverConfig = new ServerConfig(serverName, useHTTPS, port, authenticationConfig);
        pc = new ProvisionConnector(_serverConfig);
        fc = new FilesharingConnector(_serverConfig);
        cc = new ConfigConnector(_serverConfig);
        fd = new Folders(_serverConfig);
        fl = new Files(_serverConfig);
    }

    /**
     * @return version of the nextcloud instance
     * @since 11.5
     */
    public String getServerVersion() {
        return fl.getServerVersion();
    }

    /**
     * @param resolver Resolves the WebDavPath of the server
     * @see WebDavPathResolver
     * @see WebDavPathResolverBuilder
     * @since 11.5
     */
    public void setWebDavPathResolver(final WebDavPathResolver resolver) {
        this.fd.setWebDavPathResolver(resolver);
        this.fl.setWebDavPathResolver(resolver);
    }

    /**
     * <p>
     * Set the type of Files and Folder connectors</p>
     * <p>
     * You can resolve VCARD and CALDAV paths as well</p>
     * <code>
     *  NextcloudConnector connector = ... <br>
     * connector.setWebDavPathResolver(WebDavPathResolverBuilder.TYPE.CALDAV);<br>
     * </code>
     *
     * @param type Type of the files/folders path
     * @see WebDavPathResolver
     * @see WebDavPathResolverBuilder
     * @since 11.5
     */
    public void setWebDavPathResolverAsType(final WebDavPathResolverBuilder.TYPE type) {

        WebDavPathResolver resolver = WebDavPathResolverBuilder.get(type)
                .ofVersion(NextcloudVersion.get(getServerVersion()))
                .withUserName(getCurrentUser().getId())
//                .withUserName(_serverConfig.getUserName())
                .withBasePathPrefix(_serverConfig.getSubPathPrefix()).build();

        this.fd.setWebDavPathResolver(resolver);
        this.fl.setWebDavPathResolver(resolver);
    }

    /**
     * Close the HTTP client. Perform this to cleanly shut down this
     * application.
     *
     * @throws IOException In case of IO errors
     */
    public void shutdown() throws IOException {
        ConnectorCommon.shutdown();
    }

    /**
     * Trust all HTTPS certificates presented by the server. This is e.g. used
     * to work against a Nextcloud instance with a self-signed certificate.
     *
     * @param trustAllCertificates Do we accep self signed certificates or not
     */
    public void trustAllCertificates(boolean trustAllCertificates) {
        _serverConfig.setTrustAllCertificates(trustAllCertificates);
    }

    /**
     * Subpath prefix to the Nextcloud service (if applicable). This is the case
     * if the Nextcloud installation is hosted within a subdirectory.
     *
     * @param subpathPrefix Prefix to the nextcloud installation, if not
     * installed in root
     */
    public void setSubpathPrefix(String subpathPrefix) {
        _serverConfig.setSubPathPrefix(subpathPrefix);
    }

    /**
     * Creates a user
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @return true if the operation succeeded
     */
    public boolean createUser(String userId, String password) {
        CompletableFuture<JsonVoidAnswer> result = createUserAsync(userId, password);
        return NextcloudResponseHelper.isStatusCodeOkay(result);
    }

    /**
     * Creates a user with corresponding user information
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @param displayName the display name of the user
     * @param email the email address of the user
     * @param quota the quota of the user
     * @param language the language of the user
     * @param groups the groups the user should be added to
     * @return true if the operation succeeded
     */
    public boolean createUser(String userId, String password, String displayName, String email, String quota, String language, List<String> groups) {
        CompletableFuture<JsonVoidAnswer> result = createUserAsync(userId, password,
                Optional.of(displayName), Optional.of(email), Optional.of(quota), Optional.of(language), groups);
        return NextcloudResponseHelper.isStatusCodeOkay(result);
    }

    /**
     * Creates a user asynchronously
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @return true if the operation succeeded
     */
    public CompletableFuture<JsonVoidAnswer> createUserAsync(String userId, String password) {
        return pc.createUserAsync(userId, password,
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Collections.emptyList());
    }

    /**
     * Creates a user with corresponding user information asynchronously
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @param displayName the display name of the user
     * @param email the email address of the user
     * @param quota the quota of the user
     * @param language the language of the user
     * @param groups the groups the user should be added to
     * @return true if the operation succeeded
     */
    public CompletableFuture<JsonVoidAnswer> createUserAsync(String userId, String password,
                                                             Optional<String> displayName, Optional<String> email,
                                                             Optional<String> quota, Optional<String> language, List<String> groups) {
        return pc.createUserAsync(userId, password, displayName, email, quota, language, groups);
    }

    /**
     * Deletes a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean deleteUser(String userId) {
        return pc.deleteUser(userId);
    }

    /**
     * Deletes a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> deleteUserAsync(String userId) {
        return pc.deleteUserAsync(userId);
    }

    /**
     * Enables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean enableUser(String userId) {
        return pc.enableUser(userId);
    }

    /**
     * Enables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> enableUserAsync(String userId) {
        return pc.enableUserAsync(userId);
    }

    /**
     * Disables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean disableUser(String userId) {
        return pc.disableUser(userId);
    }

    /**
     * Disables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> disableUserAsync(String userId) {
        return pc.disableUserAsync(userId);
    }

    /**
     * Gets all groups of a user
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getGroupsOfUser(String userId) {
        return pc.getGroupsOfUser(userId);
    }

    /**
     * Gets all groups of a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupListAnswer> getGroupsOfUserAsync(String userId) {
        return pc.getGroupsOfUserAsync(userId);
    }

    /**
     * Adds a user to a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean addUserToGroup(String userId, String groupId) {
        return pc.addUserToGroup(userId, groupId);
    }

    /**
     * Adds a user to a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> addUserToGroupAsync(String userId, String groupId) {
        return pc.addUserToGroupAsync(userId, groupId);
    }

    /**
     * Removes a user from a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean removeUserFromGroup(String userId, String groupId) {
        return pc.removeUserFromGroup(userId, groupId);
    }

    /**
     * Removes a user from a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> removeUserFromGroupAsync(String userId, String groupId) {
        return pc.removeUserFromGroupAsync(userId, groupId);
    }

    /**
     * Gets all groups this user is a subadministrator of
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getSubadminGroupsOfUser(String userId) {
        return pc.getSubadminGroupsOfUser(userId);
    }

    /**
     * Gets all groups this user is a subadministrator of asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonListAnswer> getSubadminGroupsOfUserAsync(String userId) {
        return pc.getSubadminGroupsOfUserAsync(userId);
    }

    /**
     * Promotes a user to a subadministrator of a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean promoteToSubadmin(String userId, String groupId) {
        return pc.promoteToSubadmin(userId, groupId);
    }

    /**
     * Promotes a user to a subadministrator of a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> promoteToSubadminAsync(String userId, String groupId) {
        return pc.promoteToSubadminAsync(userId, groupId);
    }

    /**
     * Remove subadministrator rights of a user for a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean demoteSubadmin(String userId, String groupId) {
        return pc.demoteSubadmin(userId, groupId);
    }

    /**
     * Remove subadministrator rights of a user for a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> demoteSubadminAsync(String userId, String groupId) {
        return pc.demoteSubadminAsync(userId, groupId);
    }

    /**
     * Sends the welcome email to a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean sendWelcomeMail(String userId) {
        return pc.sendWelcomeMail(userId);
    }

    /**
     * Sends the welcome email to a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> sendWelcomeMailAsync(String userId) {
        return pc.sendWelcomeMailAsync(userId);
    }

    /**
     * Gets all members of a group
     *
     * @param groupId unique identifier of the user
     * @return user IDs of members
     */
    public List<String> getMembersOfGroup(String groupId) {
        return pc.getMembersOfGroup(groupId);
    }

    /**
     * Gets all members of a group asynchronously
     *
     * @param groupId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getMembersOfGroupAsync(String groupId) {
        return pc.getMembersOfGroupAsync(groupId);
    }

    /**
     * Gets all members details of a group
     *
     * @param groupId unique identifier of the user
     * @return user IDs of members
     */
    public List<User> getMembersDetailsOfGroup(String groupId) {
        return pc.getMembersDetailsOfGroup(groupId);
    }

    /**
     * Gets all members details of a group asynchronously
     *
     * @param groupId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getMembersDetailsOfGroupAsync(String groupId) {
        return pc.getMembersDetailsOfGroupAsync(groupId);
    }

    /**
     * Gets all subadministrators of a group
     *
     * @param groupId unique identifier of the group
     * @return user IDs of subadministrators
     */
    public List<String> getSubadminsOfGroup(String groupId) {
        return pc.getSubadminsOfGroup(groupId);
    }

    /**
     * Gets all subadministrators of a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonListAnswer> getSubadminsOfGroupAsync(String groupId) {
        return pc.getSubadminsOfGroupAsync(groupId);
    }

    /**
     * Creates a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean createGroup(String groupId) {
        return pc.createGroup(groupId);
    }

    /**
     * Creates a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> createGroupAsync(String groupId) {
        return pc.createGroupAsync(groupId);
    }

    /**
     * Gets all user IDs of this instance
     *
     * @return all user IDs
     */
    public List<String> getUsers() {
        return pc.getAllUsers();
    }

    /**
     * Get all matching user IDs
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matched user IDs
     */
    public List<String> getUsers(String search, int limit, int offset) {
        return pc.getAllUsers(search, limit, offset);
    }

    /**
     * Gets all user IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getUsersAsync() {
        return pc.getAllUsersAsync();
    }

    /**
     * Get all matching user IDs asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getUsersAsync(String search, int limit, int offset) {
        return pc.getAllUsersAsync(search, limit, offset);
    }
    
    /**
     * Gets all user details of this instance
     *
     * @return all user details
     */
    public List<User> getUsersDetails() {
        return pc.getAllUserDetails();
    }

    /**
     * Get all matching user details
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matched user details
     */
    public List<User> getUsersDetails(String search, int limit, int offset) {
        return pc.getAllUserDetails(search, limit, offset);
    }

    /**
     * Gets all user details of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getUsersDetailsAsync() {
        return pc.getAllUserDetailsAsync();
    }

    /**
     * Get all matching user details asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getUsersDetailsAsync(String search, int limit, int offset) {
        return pc.getAllUserDetailsAsync(search, limit, offset);
    }

    /**
     * Gets all available information of one user
     *
     * @param userId unique identifier of the user
     * @return user object containing all information
     */
    public User getUser(String userId) {
        return pc.getUser(userId);
    }

    /**
     * Gets all available information of one user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsAnswer> getUserAsync(String userId) {
        return pc.getUserAsync(userId);
    }

    /**
     * Gets user details of currently logged in user
     *
     * @return all user details
     */
    public User getCurrentUser() {
        return pc.getCurrentUser();
    }

    /**
     * Changes a single attribute of a user
     *
     * @param userId unique identifier of the user
     * @param key the attribute to change
     * @param value the value to set
     * @return true if the operation succeeded
     */
    public boolean editUser(String userId, UserData key, String value) {
        return pc.editUser(userId, key, value);
    }

    /**
     * Changes a single attribute of a user asynchronously
     *
     * @param userId unique identifier of the user
     * @param key the attribute to change
     * @param value the value to set
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> editUserAsync(String userId, UserData key, String value) {
        return pc.editUserAsync(userId, key, value);
    }

    /**
     * Deletes a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean deleteGroup(String groupId) {
        return pc.deleteGroup(groupId);
    }

    /**
     * Deletes a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> deleteGroupAsync(String groupId) {
        return pc.deleteGroupAsync(groupId);
    }

    /**
     * Get all group IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupListAnswer> getGroupsAsync() {
        return pc.getGroupsAsync();
    }

    /**
     * Get all group IDs of this instance
     *
     * @return all group IDs
     */
    public List<String> getGroups() {
        return pc.getGroups();
    }

    /**
     * Get all matching group IDs
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matching group IDs
     */
    public List<String> getGroups(String search, int limit, int offset) {
        return pc.getGroups(search, limit, offset);
    }

    /**
     * Get all matching group IDs asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupListAnswer> getGroupsAsync(String search, int limit, int offset) {
        return pc.getGroupsAsync(search, limit, offset);
    }

    /**
     * Get all subfolders of the specified path
     *
     * @param path path of the folder
     * @return found subfolders
     * @deprecated The methods naming is somehow misleading, as it lists all
     * resources (subfolders and files) within the given {@code rootPath}.
     * Please use {@link #listFolderContent(String)} instead.
     */
    @Deprecated
    public List<String> getFolders(String path) {
        return fd.getFolders(path);
    }

    /**
     * Get all subfolders of the specified path
     *
     * @param path path of the folder
     * @return found subfolders
     */
    public List<String> listFolderContent(String path) {
        return fd.listFolderContent(path);
    }

    /**
     * List all file names and subfolders of the specified path traversing into
     * subfolders to the given depth.
     *
     * @param path path of the folder
     * @param depth depth of recursion while listing folder contents (use 0 for
     * single resource, 1 for directory listing, -1 for infinite recursion)
     * @return found file names and subfolders
     */
    public List<String> listFolderContent(String path, int depth) {
        return fd.listFolderContent(path, depth);
    }

    /**
     * List all file names and subfolders of the specified path traversing into
     * subfolders to the given depth.
     *
     * @param path path of the folder
     * @param depth depth of recursion while listing folder contents (use 0 for
     * single resource, 1 for directory listing, -1 for infinite recursion)
     * @param excludeFolderNames excludes the folder names from the result list
     * @return found file names and subfolders
     */
    public List<String> listFolderContent(String path, int depth, boolean excludeFolderNames) {
        return fd.listFolderContent(path, depth, excludeFolderNames, false);
    }

    /**
     * List all file names and subfolders of the specified path traversing into
     * subfolders to the given depth.
     *
     * @param path path of the folder
     * @param depth depth of recursion while listing folder contents (use 0 for
     * single resource, 1 for directory listing, -1 for infinite recursion)
     * @param excludeFolderNames excludes the folder names from the result list
     * @param returnFullPath returns the full path instead of just the filename
     * @return found file names and subfolders
     */
    public List<String> listFolderContent(String path, int depth, boolean excludeFolderNames, boolean returnFullPath) {
        return fd.listFolderContent(path, depth, excludeFolderNames, returnFullPath);
    }

    /**
     * Checks if the folder at the specified path exists
     *
     * @param path path of the folder
     * @return true if the folder exists
     */
    public boolean folderExists(String path) {
        return fd.exists(path);
    }

    /**
     * Creates a folder at the specified path
     *
     * @param path path of the folder
     */
    public void createFolder(String path) {
        fd.createFolder(path);
    }

    /**
     * Deletes the folder at the specified path
     *
     * @param path path of the folder
     */
    public void deleteFolder(String path) {
        fd.deleteFolder(path);
    }

    /**
     * method to rename/move files
     *
     * @param oldPath path of the file which should be renamed/moved
     * @param newPath path of the file which should be renamed/moved
     * @param overwriteExisting overwrite if target already exists
     */
    public void renameFile(String oldPath, String newPath, boolean overwriteExisting) {
        fd.renamePath(oldPath, newPath, overwriteExisting);
    }

    /**
     * Shares the specified path with the provided parameters
     *
     * @param path path to the file/folder which should be shared
     * @param shareType 0 = user; 1 = group; 3 = public link; 4 = email; 6 =
     * federated cloud share
     * @param shareWithUserOrGroupId user / group id / email with which the file
     * should be shared
     * @param publicUpload allow public upload to a public shared folder
     * (true/false)
     * @param password password to protect public link Share with
     * @param permissions 1 = read; 2 = update; 4 = create; 8 = delete; 16 =
     * share; 31 = all (default: 31, for public shares: 1)
     * @return created share on success
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions) {
        return fc.doShare(path, shareType, shareWithUserOrGroupId, publicUpload, password, permissions);
    }

    /**
     * Shares the specified path with the provided parameters asynchronously
     *
     * @param path path to the file/folder which should be shared
     * @param shareType 0 = user; 1 = group; 3 = public link; 4 = email; 6 =
     * federated cloud share
     * @param shareWithUserOrGroupId user / group id / email with which the file
     * should be shared
     * @param publicUpload allow public upload to a public shared folder
     * (true/false)
     * @param password password to protect public link Share with
     * @param permissions 1 = read; 2 = update; 4 = create; 8 = delete; 16 =
     * share; 31 = all (default: 31, for public shares: 1)
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SingleShareXMLAnswer> doShareAsync(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions) {
        return fc.doShareAsync(path, shareType, shareWithUserOrGroupId, publicUpload, password, permissions);
    }

    /**
     * Deletes a share
     *
     * @param shareId unique identifier of the share
     * @return true if the operation succeeded
     */
    public boolean deleteShare(int shareId) {
        return fc.deleteShare(shareId);
    }

    /**
     * Deletes a share asynchronously
     *
     * @param shareId unique identifier of the share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> deleteShareAsync(int shareId) {
        return fc.deleteShareAsync(shareId);
    }

    /**
     * Get all shares of this user
     *
     * @return all shares
     */
    public List<Share> getShares() {
        return fc.getShares();
    }

    /**
     * Get all shares of this user asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getSharesAsync() {
        return fc.getSharesAsync();
    }

    /**
     * Uploads a file at the specified path with the data from the InputStream
     *
     * @param srcFile The file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     */
    public void uploadFile(File srcFile, String remotePath) {
        fl.uploadFile(srcFile, remotePath);
    }

    /**
     * Uploads a file at the specified path with the data from the InputStream
     *
     * @param inputStream InputStream of the file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     *
     * @deprecated Since some nextcloud installations use fpm or fastcgi to
     * connect to php, here the uploads might get zero empty on the server Use a
     * (temp) file to upload the data, so the content length is known in advance
     * https://github.com/a-schild/nextcloud-java-api/issues/20
     */
    public void uploadFile(InputStream inputStream, String remotePath) {
        fl.uploadFile(inputStream, remotePath);
    }

    /**
     * Uploads a file at the specified path with the data from the InputStream
     * and continueHeader
     *
     * @param inputStream InputStream of the file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     * @param continueHeader to receive a possible error by the server before
     * any data is sent
     *
     *
     * @deprecated Since some nextcloud installations use fpm or fastcgi to
     * connect to php, here the uploads might get zero empty on the server Use a
     * (temp) file to upload the data, so the content length is known in advance
     * https://github.com/a-schild/nextcloud-java-api/issues/20
     */
    public void uploadFile(InputStream inputStream, String remotePath, boolean continueHeader) {
        fl.uploadFile(inputStream, remotePath, continueHeader);
    }

    /**
     * method to remove files
     *
     * @param path path of the file which should be removed
     */
    public void removeFile(String path) {
        fl.removeFile(path);
    }

    /**
     * Return if the file exists or not
     *
     * @param path path to the file
     * @return boolean value whether the file exists or not
     */
    public boolean fileExists(String path) {
        return fl.fileExists(path);
    }

    /**
     * Retrieve the file properties from the server
     *
     * @param path to the file you are interested in it
     * @param allProperties return all properties not only
     * <ul>
     * <li>contentLength</li>
     * <li>contentType</li>
     * <li>creation</li>
     * <li>displayName</li>
     * <li>etag(res</li>
     * <li>modified</li>
     * </ul>
     * @return properties of this resource
     *
     * @throws IOException 404 in case of resource not found on server
     */
    public ResourceProperties getProperties(String path,
            boolean allProperties) throws IOException {
        return fl.getProperties(path, allProperties);
    }

    /**
     * Gets all shares from a given file/folder
     *
     * @param path path to file/folder
     * @param reShares returns not only the shares from the current user but all
     * shares from the given file
     * @param subShares returns all shares within a folder, given that path
     * defines a folder
     * @return matching shares
     */
    public Collection<Share> getShares(String path, boolean reShares, boolean subShares) {
        return fc.getShares(path, reShares, subShares);
    }

    /**
     * Gets all shares from a given file/folder asynchronously
     *
     * @param path path to file/folder
     * @param reShares returns not only the shares from the current user but all
     * shares from the given file
     * @param subShares returns all shares within a folder, given that path
     * defines a folder
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getSharesAsync(String path, boolean reShares, boolean subShares) {
        return fc.getSharesAsync(path, reShares, subShares);
    }

    /**
     * Get share info for a single share
     *
     * @param shareId id of share (Not path of share)
     * @return the share if it has been found, otherwise null
     */
    public Share getShareInfo(int shareId) {
        return fc.getShareInfo(shareId);
    }

    /**
     * Get share info for a single share asynchronously
     *
     * @param shareId id of share (Not path of share)
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getShareInfoAsync(int shareId) {
        return fc.getShareInfoAsync(shareId);
    }

    /**
     * Changes a single attribute of a share
     *
     * @param shareId unique identifier of the share
     * @param key the attribute to change
     * @param value the value to set
     * @return true if the operation succeeded
     */
    public boolean editShare(int shareId, ShareData key, String value) {
        return fc.editShare(shareId, key, value);
    }

    /**
     * Changes a single attribute of a share asynchronously
     *
     * @param shareId unique identifier of the share
     * @param key the attribute to change
     * @param value the value to set
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> editShareAsync(int shareId, ShareData key, String value) {
        return fc.editShareAsync(shareId, key, value);
    }

    /**
     * Changes multiple attributes of a share at once
     *
     * @param shareId unique identifier of the share
     * @param values a Map containing the attributes to set
     * @return true if the operation succeeded
     */
    public boolean editShare(int shareId, Map<ShareData, String> values) {
        return fc.editShare(shareId, values);
    }

    /**
     * Changes multiple attributes of a share at once asynchronously
     *
     * @param shareId unique identifier of the share
     * @param values a Map containing the attributes to set
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> editShareAsync(int shareId, Map<ShareData, String> values) {
        return fc.editShareAsync(shareId, values);
    }

    /**
     * Download the file from the remotepath to the download path specified in
     * the
     *
     * @param remotepath Remotepath of the file to be downloaded from the
     * nextcloud server
     * @param downloadpath Local path where the file has to be downloaded in the
     * local machine
     * @return boolean true if sucessfull
     * @throws java.io.IOException In case of IO errors
     */
    public boolean downloadFile(String remotepath, String downloadpath) throws IOException {
        return fl.downloadFile(remotepath, downloadpath);
    }

    /**
     * Download the file from the remotepath to an InputStream
     *
     * @param remotepath Remotepath of the file to be downloaded from the
     * nextcloud server
     * @return InputStream (Don't forget to close the InputStream once you are
     * done with it)
     * @throws java.io.IOException In case of IO errors
     */
    public InputStream downloadFile(String remotepath) throws IOException {
        return fl.downloadFile(remotepath);
    }

    /**
     *
     * @param remotepath Remotepath of the folder to be downloaded from the
     * nextcloud server
     * @param downloadpath Local path where the folder has to be downloaded in
     * the local machine
     * @throws IOException In case of IO errors
     */
    public void downloadFolder(String remotepath, String downloadpath) throws IOException {
        fd.downloadFolder(remotepath, downloadpath);
    }

    /**
     * App-Configuration: Get all apps available for configuration
     *
     * @return list of all available apps
     */
    public List<String> getAppConfigApps() {
        return cc.getAppConfigApps();
    }

    /**
     * App-Configuration: Get all keys available for an app
     *
     * @param appConfigApp an app name as returned by
     * {@link #getAppConfigApps()}
     * @return All keys of this app
     */
    public List<String> getAppConfigAppKeys(String appConfigApp) {
        return cc.getAppConfigAppKeys(appConfigApp);
    }

    /**
     * App-Configuration: Get a key value for an app configuration
     *
     * @param appConfigApp an app name as returned by
     * {@link #getAppConfigApps()}
     * @param appConfigAppKey a key name as returned by
     * {@link #getAppConfigAppKeys(String)}
     * @return app config entry
     */
    public String getAppConfigAppKeyValue(String appConfigApp, String appConfigAppKey) {
        return cc.getAppConfigAppKeyValue(appConfigApp, appConfigAppKey);
    }

    /**
     * App-Configuration: Get a key value for an app configuration
     *
     * @param appConfigAppKeyPath config path to return
     * @return app config entry
     */
    public String getAppConfigAppKeyValue(String appConfigAppKeyPath) {
        return cc.getAppConfigAppKeyValue(appConfigAppKeyPath);
    }

    /**
     * App-Configuration: Edit a key value for an app configuration
     *
     * @param appConfigApp an app name as returned by
     * {@link #getAppConfigApps()}
     * @param appConfigAppKey a key name as returned by
     * {@link #getAppConfigAppKeys(String)}
     * @param value the value to set
     * @return true if sucessfully set
     */
    public boolean setAppConfigAppKeyValue(String appConfigApp, String appConfigAppKey, Object value) {
        return cc.setAppConfigAppKeyValue(appConfigApp, appConfigAppKey, value);
    }

    /**
     *
     * @param appConfigAppKeyPath the full appConfigAppKeyPath combining
     * appConfigApp and appConfigAppKey with "/"
     * @param value the value to set
     * @return Operation sucessfull
     */
    public boolean setAppConfigAppKeyValue(String appConfigAppKeyPath, Object value) {
        return cc.setAppConfigAppKeyValue(appConfigAppKeyPath, value);
    }

    /**
     * App-Configuration: Delete a key of an app configuration
     *
     * @param appConfigApp an app name as returned by
     * {@link #getAppConfigApps()}
     * @param appConfigAppkey a key name as returned by
     * {@link #getAppConfigAppKeys(String)}
     * @return Operation sucessfull
     */
    public boolean deleteAppConfigAppKeyEntry(String appConfigApp, String appConfigAppkey) {
        return cc.deleteAppConfigAppKeyEntry(appConfigApp, appConfigAppkey);
    }
}
