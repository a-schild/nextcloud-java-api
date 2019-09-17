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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.NextcloudResponseHelper;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.utils.ListXMLAnswer;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author a.schild
 *
 * https://docs.nextcloud.com/server/11.0/admin_manual/configuration_user/user_provisioning_api.html
 *
 */
public class ProvisionConnector
{
    private final static String ROOT_PART= "ocs/v1.php/cloud/";
    private final static String USERS_PART= ROOT_PART+"users";
    private final static String GROUPS_PART= ROOT_PART+"groups";

    private final ConnectorCommon connectorCommon;

    public ProvisionConnector(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
    }

    /**
     * Creates a user
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @return true if the operation succeeded
     */
    public boolean createUser(String userId, String password)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(createUserAsync(userId, password));
    }

    /**
     * Creates a user asynchronously
     *
     * @param userId unique identifier of the user
     * @param password password needs to meet nextcloud criteria or operation will fail
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> createUserAsync(String userId, String password)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("userid", userId));
        postParams.add(new BasicNameValuePair("password", password));
        return connectorCommon.executePost(USERS_PART, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Deletes a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean deleteUser(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteUserAsync(userId));
    }

    /**
     * Deletes a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> deleteUserAsync(String userId)
    {
        return connectorCommon.executeDelete(USERS_PART, userId, null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all user IDs of this instance
     *
     * @return all user IDs
     */
    public List<String> getUsers()
    {
        return getUsers(null, -1, -1);
    }

    /**
     * Gets all user IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UsersXMLAnswer> getUsersAsync()
    {
        return getUsersAsync(null, -1, -1);
    }

    /**
     * Get all matching user IDs
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return matched user IDs
     */
    public List<String> getUsers(
            String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getUsersAsync(search, limit, offset)).getUsers();
    }

    /**
     * Get all matching user IDs asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UsersXMLAnswer> getUsersAsync(
            String search, int limit, int offset)
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (limit != -1)
        {
            queryParams.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }
        if (offset != -1)
        {
            queryParams.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        }
        if (search != null)
        {
            queryParams.add(new BasicNameValuePair("search", search));
        }
        return connectorCommon.executeGet(USERS_PART, queryParams, XMLAnswerParser.getInstance(UsersXMLAnswer.class));
    }

    /**
     * Gets all available information of one user
     *
     * @param userId unique identifier of the user
     * @return user object containing all information
     */
    public User getUser(String userId)
    {
        return NextcloudResponseHelper.getAndWrapException(getUserAsync(userId)).getUser();
    }

    /**
     * Gets all available information of one user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UserXMLAnswer> getUserAsync(String userId)
    {
        return connectorCommon.executeGet(USERS_PART+"/"+userId, Collections.emptyList(), XMLAnswerParser.getInstance(UserXMLAnswer.class));
    }

    /**
     * Changes a single attribute of a user
     *
     * @param userId unique identifier of the user
     * @param key the attribute to change
     * @param value the value to set
     * @return true if the operation succeeded
     */
    public boolean editUser(String userId, UserData key, String value)
    {
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
    public CompletableFuture<XMLAnswer> editUserAsync(String userId, UserData key, String value)
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        queryParams.add(new BasicNameValuePair("key", key.name().toLowerCase()));
        queryParams.add(new BasicNameValuePair("value", value));
        return connectorCommon.executePut(USERS_PART, userId, queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Enables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean enableUser(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(enableUserAsync(userId));
    }

    /**
     * Enables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> enableUserAsync(String userId)
    {
        return connectorCommon.executePut(USERS_PART, userId + "/enable", null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Disables a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean disableUser(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(disableUserAsync(userId));
    }

    /**
     * Disables a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> disableUserAsync(String userId)
    {
        return connectorCommon.executePut(USERS_PART, userId + "/disable", null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all groups of a user
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getGroupsOfUser(String userId)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getGroupsOfUserAsync(userId)).getGroups();
    }

    /**
     * Gets all groups of a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupsXMLAnswer> getGroupsOfUserAsync(String userId)
    {
        return connectorCommon.executeGet(USERS_PART + "/" + userId + "/groups", null, XMLAnswerParser.getInstance(GroupsXMLAnswer.class));
    }

    /**
     * Adds a user to a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean addUserToGroup(String userId, String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(addUserToGroupAsync(userId, groupId));
    }

    /**
     * Adds a user to a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> addUserToGroupAsync(String userId, String groupId)
    {
        List<NameValuePair> queryParams= Collections.singletonList(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/groups", queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Removes a user from a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean removeUserFromGroup(String userId, String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(removeUserFromGroupAsync(userId, groupId));
    }

    /**
     * Removes a user from a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> removeUserFromGroupAsync(String userId, String groupId)
    {
        List<NameValuePair> queryParams= Collections.singletonList(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executeDelete(USERS_PART, userId + "/groups", queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all groups this user is a subadministrator of
     *
     * @param userId unique identifier of the user
     * @return matched group IDs
     */
    public List<String> getSubadminGroupsOfUser(String userId)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getSubadminGroupsOfUserAsync(userId)).getResult();
    }

    /**
     * Gets all groups this user is a subadministrator of asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<ListXMLAnswer> getSubadminGroupsOfUserAsync(String userId)
    {
        return connectorCommon.executeGet(USERS_PART + "/" + userId + "/subadmins", null, XMLAnswerParser.getInstance(ListXMLAnswer.class));
    }

    /**
     * Promotes a user to a subadministrator of a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean promoteToSubadmin(String userId, String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(promoteToSubadminAsync(userId, groupId));
    }

    /**
     * Promotes a user to a subadministrator of a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> promoteToSubadminAsync(String userId, String groupId)
    {
        List<NameValuePair> queryParams= Collections.singletonList(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/subadmins", queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Remove subadministrator rights of a user for a group
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean demoteSubadmin(String userId, String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(demoteSubadminAsync(userId, groupId));
    }

    /**
     * Remove subadministrator rights of a user for a group asynchronously
     *
     * @param userId unique identifier of the user
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> demoteSubadminAsync(String userId, String groupId)
    {
        List<NameValuePair> queryParams= Collections.singletonList(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executeDelete(USERS_PART, userId + "/subadmins", queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Sends the welcome email to a user
     *
     * @param userId unique identifier of the user
     * @return true if the operation succeeded
     */
    public boolean sendWelcomeMail(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(sendWelcomeMailAsync(userId));
    }

    /**
     * Sends the welcome email to a user asynchronously
     *
     * @param userId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> sendWelcomeMailAsync(String userId)
    {
        return connectorCommon.executePost(USERS_PART + "/" + userId + "/welcome", null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all members of a group
     *
     * @param groupId unique identifier of the user
     * @return user IDs of members
     */
    public List<String> getMembersOfGroup(String groupId)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getMembersOfGroupAsync(groupId)).getUsers();
    }

    /**
     * Gets all members of a group asynchronously
     *
     * @param groupId unique identifier of the user
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<UsersXMLAnswer> getMembersOfGroupAsync(String groupId)
    {
        return connectorCommon.executeGet(GROUPS_PART + "/" + groupId, null, XMLAnswerParser.getInstance(UsersXMLAnswer.class));
    }

    /**
     * Gets all subadministrators of a group
     *
     * @param groupId unique identifier of the group
     * @return user IDs of subadministrators
     */
    public List<String> getSubadminsOfGroup(String groupId)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getSubadminsOfGroupAsync(groupId)).getResult();
    }

    /**
     * Gets all subadministrators of a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<ListXMLAnswer> getSubadminsOfGroupAsync(String groupId)
    {
        return connectorCommon.executeGet(GROUPS_PART + "/" + groupId + "/subadmins", null, XMLAnswerParser.getInstance(ListXMLAnswer.class));
    }

    /**
     * Creates a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean createGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(createGroupAsync(groupId));
    }

    /**
     * Creates a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> createGroupAsync(String groupId)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(GROUPS_PART, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Deletes a group
     *
     * @param groupId unique identifier of the group
     * @return true if the operation succeeded
     */
    public boolean deleteGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteGroupAsync(groupId));
    }

    /**
     * Deletes a group asynchronously
     *
     * @param groupId unique identifier of the group
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> deleteGroupAsync(String groupId)
    {
        return connectorCommon.executeDelete(GROUPS_PART, groupId, null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Get all group IDs of this instance
     *
     * @return all group IDs
     */
    public List<String> getGroups()
    {
        return getGroups(null, -1, -1);
    }

    /**
     * Get all group IDs of this instance asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupsXMLAnswer> getGroupsAsync()
    {
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
    public List<String> getGroups(String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getGroupsAsync(search, limit, offset)).getGroups();
    }

    /**
     * Get all matching group IDs asynchronously
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<GroupsXMLAnswer> getGroupsAsync(String search, int limit, int offset)
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (limit != -1)
        {
            queryParams.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }
        if (offset != -1)
        {
            queryParams.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        }
        if (search != null)
        {
            queryParams.add(new BasicNameValuePair("search", search));
        }

        return connectorCommon.executeGet(GROUPS_PART, queryParams, XMLAnswerParser.getInstance(GroupsXMLAnswer.class));
    }
}
