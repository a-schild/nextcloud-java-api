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
package org.aarboard.nextcloud.api.provisioning;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author a.schild
 *
 * <a href="https://docs.nextcloud.com/server/11.0/admin_manual/configuration_user/user_provisioning_api.html">...</a>
 *
 */
public class ProvisionConnector
{
    private final static String ROOT_PART= "ocs/v1.php/cloud/";
    private final static String USER_PART= ROOT_PART+"user";
    private final static String USERS_PART= ROOT_PART+"users";
    private final static String GROUPS_PART= ROOT_PART+"groups";

    private final ConnectorCommon connectorCommon;

    public ProvisionConnector(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
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
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("userid", userId));
        postParams.add(new BasicNameValuePair("password", password));
        if (displayName.isPresent()) {
            postParams.add(new BasicNameValuePair("displayName", displayName.get()));
        }
        if (email.isPresent()) {
            postParams.add(new BasicNameValuePair("email", email.get()));
        }
        if (quota.isPresent()) {
            postParams.add(new BasicNameValuePair("quota", quota.get()));
        }
        if (language.isPresent()) {
            postParams.add(new BasicNameValuePair("language", language.get()));
        }
        groups.forEach(group -> {
            postParams.add(new BasicNameValuePair("groups[]", group));
        });

        return connectorCommon.executePost(USERS_PART, postParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Deletes a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean deleteUser(String userId) {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteUserAsync(userId));
    }

    /**
     * Deletes a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> deleteUserAsync(String userId)
    {
        return connectorCommon.executeDelete(USERS_PART, userId, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Gets all user IDs of this instance
     *
     * @return all user IDs
     */
    public List<String> getAllUsers() {
        return getAllUsers(null, -1, -1);
    }

    /**
     * Gets all user IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getAllUsersAsync() {
        return getAllUsersAsync(null, -1, -1);
    }

    /**
     * Get all matching user IDs
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matched user IDs
     */
    public List<String> getAllUsers(String search, int limit, int offset) {
        return NextcloudResponseHelper.getAndCheckStatus(getAllUsersAsync(search, limit, offset)).getAllUsers();
    }

    /**
     * Get all matching user IDs asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getAllUsersAsync(String search, int limit, int offset) {
        NextcloudSearch nextcloudSearch = new NextcloudSearch(search, limit, offset);
        return connectorCommon.executeGet(USERS_PART, nextcloudSearch.asQueryParameters(), JsonAnswerParser.getInstance(UserListAnswer.class));
    }

    /**
     * Gets all user details of this instance
     *
     * @return all user details
     */
    public List<User> getAllUserDetails() {
        return getAllUserDetails(null, -1, -1);
    }
    
    /**
     * Gets all user details of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getAllUserDetailsAsync() {
        return getAllUserDetailsAsync(null, -1, -1);
    }

    /**
     * Get all matching user details
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matched user details
     */
    public List<User> getAllUserDetails(String search, int limit, int offset) {
        return NextcloudResponseHelper.getAndCheckStatus(getAllUserDetailsAsync(search, limit, offset)).getAllUserDetails();
    }

    /**
     * Get all matching user details asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getAllUserDetailsAsync(String search, int limit, int offset) {
        NextcloudSearch nextcloudSearch = new NextcloudSearch(search, limit, offset);
        return connectorCommon.executeGet(USERS_PART+"/details", nextcloudSearch.asQueryParameters(), JsonAnswerParser.getInstance(UserDetailsListAnswer.class));
    }

    /**
     * Gets all available information of one user
     *
     * @param userId unique identifier of the user
     * @return user object containing all information
     */
    public User getUser(String userId) {
        return NextcloudResponseHelper.getAndWrapException(getUserAsync(userId)).getUserDetails();
    }

    /**
     * Gets all available information of one user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsAnswer> getUserAsync(String userId) {
        return connectorCommon.executeGet(USERS_PART+"/"+userId, JsonAnswerParser.getInstance(UserDetailsAnswer.class));
    }

    /**
     * Get user details for the logged in user
     *
     * @return user details of logged in user
     */
    public User getCurrentUser() {
        return NextcloudResponseHelper.getAndCheckStatus(getCurrentUserAsync()).getUserDetails();
    }

    /**
     * Get user details for the logged in user asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsAnswer> getCurrentUserAsync() {
        return connectorCommon.executeGet(USER_PART, JsonAnswerParser.getInstance(UserDetailsAnswer.class));
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
        return NextcloudResponseHelper.isStatusCodeOkay(editUserAsync(userId, key, value));
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
        List<NameValuePair> queryParams= new LinkedList<>();
        queryParams.add(new BasicNameValuePair("key", key.name().toLowerCase()));
        queryParams.add(new BasicNameValuePair("value", value));
        return connectorCommon.executePut(USERS_PART, userId, queryParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Enables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean enableUser(String userId) {
        return NextcloudResponseHelper.isStatusCodeOkay(enableUserAsync(userId));
    }

    /**
     * Enables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> enableUserAsync(String userId) {
        return connectorCommon.executePut(USERS_PART, userId + "/enable", null, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Disables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean disableUser(String userId) {
        return NextcloudResponseHelper.isStatusCodeOkay(disableUserAsync(userId));
    }

    /**
     * Disables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> disableUserAsync(String userId) {
        return connectorCommon.executePut(USERS_PART, userId + "/disable", null, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Gets all groups of a user
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getGroupsOfUser(String userId) {
        return NextcloudResponseHelper.getAndCheckStatus(getGroupsOfUserAsync(userId)).getAllGroups();
    }

    /**
     * Gets all groups of a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupListAnswer> getGroupsOfUserAsync(String userId) {
        return connectorCommon.executeGet(USERS_PART + "/" + userId + "/groups", null, JsonAnswerParser.getInstance(GroupListAnswer.class));
    }

    /**
     * Adds a user to a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean addUserToGroup(String userId, String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(addUserToGroupAsync(userId, groupId));
    }

    /**
     * Adds a user to a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> addUserToGroupAsync(String userId, String groupId) {
        List<NameValuePair> queryParams = new LinkedList<>();
        queryParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/groups", queryParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Removes a user from a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean removeUserFromGroup(String userId, String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(removeUserFromGroupAsync(userId, groupId));
    }

    /**
     * Removes a user from a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> removeUserFromGroupAsync(String userId, String groupId) {
        List<NameValuePair> queryParams = new LinkedList<>();
        queryParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executeDelete(USERS_PART, userId + "/groups", queryParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Gets all groups this user is a subadministrator of
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getSubadminGroupsOfUser(String userId) {
        return NextcloudResponseHelper.getAndCheckStatus(getSubadminGroupsOfUserAsync(userId)).getResult();
    }

    /**
     * Gets all groups this user is a subadministrator of asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonListAnswer> getSubadminGroupsOfUserAsync(String userId) {
        return connectorCommon.executeGet(USERS_PART + "/" + userId + "/subadmins", null, JsonAnswerParser.getInstance(JsonListAnswer.class));
    }

    /**
     * Promotes a user to a subadministrator of a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean promoteToSubadmin(String userId, String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(promoteToSubadminAsync(userId, groupId));
    }

    /**
     * Promotes a user to a subadministrator of a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> promoteToSubadminAsync(String userId, String groupId) {
        List<NameValuePair> queryParams = new LinkedList<>();
        queryParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/subadmins", queryParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Remove subadministrator rights of a user for a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean demoteSubadmin(String userId, String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(demoteSubadminAsync(userId, groupId));
    }

    /**
     * Remove subadministrator rights of a user for a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> demoteSubadminAsync(String userId, String groupId) {
        List<NameValuePair> queryParams = new LinkedList<>();
        queryParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executeDelete(USERS_PART, userId + "/subadmins", queryParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Sends the welcome email to a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean sendWelcomeMail(String userId) {
        return NextcloudResponseHelper.isStatusCodeOkay(sendWelcomeMailAsync(userId));
    }

    /**
     * Sends the welcome email to a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> sendWelcomeMailAsync(String userId) {
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/welcome", JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Gets all members of a group
     *
     * @param groupId unique identifier of the user
     * @return user IDs of members
     */
    public List<String> getMembersOfGroup(String groupId) {
        return NextcloudResponseHelper.getAndCheckStatus(getMembersOfGroupAsync(groupId)).getAllUsers();
    }

    /**
     * Gets all members of a group asynchronously
     *
     * @param groupId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserListAnswer> getMembersOfGroupAsync(String groupId) {
        return connectorCommon.executeGet(GROUPS_PART + "/" + groupId + "/users", JsonAnswerParser.getInstance(UserListAnswer.class));
    }

    /**
     * Gets all members details of a group
     *
     * @param groupId unique identifier of the user
     * @return user IDs of members
     */
    public List<User> getMembersDetailsOfGroup(String groupId) {
        return NextcloudResponseHelper.getAndCheckStatus(getMembersDetailsOfGroupAsync(groupId)).getAllUserDetails();
    }

    /**
     * Gets all members details of a group asynchronously
     *
     * @param groupId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserDetailsListAnswer> getMembersDetailsOfGroupAsync(String groupId) {
        return connectorCommon.executeGet(GROUPS_PART + "/" + groupId + "/users/details", JsonAnswerParser.getInstance(UserDetailsListAnswer.class));
    }

    /**
     * Gets all subadministrators of a group
     *
     * @param groupId unique identifier of the group
     * @return user IDs of subadministrators
     */
    public List<String> getSubadminsOfGroup(String groupId) {
        return NextcloudResponseHelper.getAndCheckStatus(getSubadminsOfGroupAsync(groupId)).getResult();
    }

    /**
     * Gets all subadministrators of a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonListAnswer> getSubadminsOfGroupAsync(String groupId) {
        return connectorCommon.executeGet(GROUPS_PART + "/" + groupId + "/subadmins", null, JsonAnswerParser.getInstance(JsonListAnswer.class));
    }

    /**
     * Creates a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean createGroup(String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(createGroupAsync(groupId));
    }

    /**
     * Creates a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> createGroupAsync(String groupId) {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(GROUPS_PART, postParams, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Deletes a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean deleteGroup(String groupId) {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteGroupAsync(groupId));
    }

    /**
     * Deletes a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<JsonVoidAnswer> deleteGroupAsync(String groupId) {
        return connectorCommon.executeDelete(GROUPS_PART, groupId, null, JsonAnswerParser.getInstance(JsonVoidAnswer.class));
    }

    /**
     * Get all group IDs of this instance
     *
     * @return all group IDs
     */
    public List<String> getGroups() {
        return getGroups(null, -1, -1);
    }

    /**
     * Get all group IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupListAnswer> getGroupsAsync() {
        return getGroupsAsync(null, -1, -1);
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
        return NextcloudResponseHelper.getAndCheckStatus(getGroupsAsync(search, limit, offset)).getAllGroups();
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
        NextcloudSearch nextcloudSearch = new NextcloudSearch(search, limit, offset);
        return connectorCommon.executeGet(GROUPS_PART, nextcloudSearch.asQueryParameters(), JsonAnswerParser.getInstance(GroupListAnswer.class));
    }
}
