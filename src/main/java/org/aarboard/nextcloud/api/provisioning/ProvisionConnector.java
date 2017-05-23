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

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
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
    private final static int   NC_OK= 100; // Nextcloud OK message
    
    private final static String ROOT_PART= "ocs/v1.php/cloud/";
    private final static String USERS_PART= ROOT_PART+"users";
    private final static String GROUPS_PART= ROOT_PART+"groups";

    private final ConnectorCommon connectorCommon;

    public ProvisionConnector(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
    }

    public boolean createUser(String userId, String password)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("userid", userId));
        postParams.add(new BasicNameValuePair("password", password));
        XMLAnswer xa= new XMLAnswer();
        connectorCommon.executePost(USERS_PART, postParams, xa);
        return xa.getStatusCode() == NC_OK;
    }

    public boolean deleteUser(String userId)
    {
        XMLAnswer xa= new XMLAnswer();
        connectorCommon.executeDelete(USERS_PART, userId, xa);
        return xa.getStatusCode() == NC_OK;
    }

    /**
     * Return all users of this instance
     * 
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<User> getUsers()
    {
        return getUsers(null, -1, -1);
    }

    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<User> getUsers(
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
        UsersXMLAnswer xa= new UsersXMLAnswer();
        connectorCommon.executeGet(USERS_PART, queryParams, xa);
        if (xa.getStatusCode() == NC_OK)
        {
            return xa.getUsers();
        }
        return null;
    }
    
    public boolean createGroup(String groupId)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        XMLAnswer xa= new XMLAnswer();
        connectorCommon.executePost(GROUPS_PART, postParams, xa);
        return xa.getStatusCode() == NC_OK;
    }

    public boolean deleteGroup(String groupId)
    {
        XMLAnswer xa= new XMLAnswer();
        connectorCommon.executeDelete(GROUPS_PART, groupId, xa);
        return xa.getStatusCode() == NC_OK;
    }

    
    public Collection<Group> getGroups()
    {
        return getGroups(null, -1, -1);
    }
    
    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public Collection<Group> getGroups(String search, int limit, int offset)
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

        GroupsXMLAnswer xa= new GroupsXMLAnswer();
        connectorCommon.executeGet(GROUPS_PART, queryParams, xa);
        if (xa.getStatusCode() == NC_OK)
        {
            return xa.getGroups();
        }
        return null;
    }
}
