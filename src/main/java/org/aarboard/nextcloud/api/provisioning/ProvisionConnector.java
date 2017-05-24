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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.NextcloudResponseHelper;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
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

    public boolean createUser(String userId, String password)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(createUserAsync(userId, password));
    }

    public CompletableFuture<XMLAnswer> createUserAsync(String userId, String password)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("userid", userId));
        postParams.add(new BasicNameValuePair("password", password));
        return connectorCommon.executePost(USERS_PART, postParams, XMLAnswerParser.getDefaultInstance());
    }

    public boolean deleteUser(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteUserAsync(userId));
    }

    public CompletableFuture<XMLAnswer> deleteUserAsync(String userId)
    {
        return connectorCommon.executeDelete(USERS_PART, userId, XMLAnswerParser.getDefaultInstance());
    }

    /**
     * Return all users of this instance
     * 
     * @return 
     */
    public Collection<User> getUsers()
    {
        return getUsers(null, -1, -1);
    }

    public CompletableFuture<UsersXMLAnswer> getUsersAsync()
    {
        return getUsersAsync(null, -1, -1);
    }

    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public Collection<User> getUsers(
            String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndWrapException(getUsersAsync(search, limit, offset)).userList;
    }

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
        return connectorCommon.executeGet(USERS_PART, queryParams, UsersXMLAnswerParser.getInstance());
    }

    public boolean createGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(createGroupAsync(groupId));
    }

    public CompletableFuture<XMLAnswer> createGroupAsync(String groupId)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(GROUPS_PART, postParams, XMLAnswerParser.getDefaultInstance());
    }

    public boolean deleteGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteGroupAsync(groupId));
    }

    public CompletableFuture<XMLAnswer> deleteGroupAsync(String groupId)
    {
        return connectorCommon.executeDelete(GROUPS_PART, groupId, XMLAnswerParser.getDefaultInstance());
    }

    public List<Group> getGroups()
    {
        return getGroups(null, -1, -1);
    }

    public CompletableFuture<GroupsXMLAnswer> getGroupsAsync()
    {
        return getGroupsAsync(null, -1, -1);
    }

    /**
     * Return matching groups
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public List<Group> getGroups(String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndWrapException(getGroupsAsync(search, limit, offset)).groupList;
    }

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

        return connectorCommon.executeGet(GROUPS_PART, queryParams, GroupsXMLAnswerParser.getInstance());
    }
}
