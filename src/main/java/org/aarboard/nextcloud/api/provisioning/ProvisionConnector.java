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
import java.util.Collections;
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
        return connectorCommon.executePost(USERS_PART, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    public boolean deleteUser(String userId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteUserAsync(userId));
    }

    public CompletableFuture<XMLAnswer> deleteUserAsync(String userId)
    {
        return connectorCommon.executeDelete(USERS_PART, userId, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Return all users of this instance
     *
     * @return
     */
    public Collection<String> getUsers()
    {
        return getUsers(null, -1, -1);
    }

    public CompletableFuture<UsersXMLAnswer> getUsersAsync()
    {
        return getUsersAsync(null, -1, -1);
    }

    /**
     * Return matching user ids
     *
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return
     */
    public Collection<String> getUsers(
            String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getUsersAsync(search, limit, offset)).getUsers();
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
        return connectorCommon.executeGet(USERS_PART, queryParams, XMLAnswerParser.getInstance(UsersXMLAnswer.class));
    }

    public User getUser(String userId)
    {
        return NextcloudResponseHelper.getAndWrapException(getUserAsync(userId)).getUser();
    }

    public CompletableFuture<UserXMLAnswer> getUserAsync(String userId)
    {
        return connectorCommon.executeGet(USERS_PART+"/"+userId, Collections.emptyList(), XMLAnswerParser.getInstance(UserXMLAnswer.class));
    }

    public boolean createGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(createGroupAsync(groupId));
    }

    public CompletableFuture<XMLAnswer> createGroupAsync(String groupId)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        return connectorCommon.executePost(GROUPS_PART, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    public boolean deleteGroup(String groupId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteGroupAsync(groupId));
    }

    public CompletableFuture<XMLAnswer> deleteGroupAsync(String groupId)
    {
        return connectorCommon.executeDelete(GROUPS_PART, groupId, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    public List<String> getGroups()
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
    public List<String> getGroups(String search, int limit, int offset)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getGroupsAsync(search, limit, offset)).getGroups();
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

        return connectorCommon.executeGet(GROUPS_PART, queryParams, XMLAnswerParser.getInstance(GroupsXMLAnswer.class));
    }
}
