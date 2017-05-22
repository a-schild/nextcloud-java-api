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
package org.aarboard.nextcloud.api.filesharing;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author a.schild
 * 
 * https://docs.nextcloud.com/server/11/developer_manual/core/ocs-share-api.html
 * 
 */
public class FilesharingConnector 
{
    private final static Log LOG = LogFactory.getLog(FilesharingConnector.class);

    private final static int   NC_OK= 100; // Nextcloud OK message
    
    private final static String ROOT_PART= "ocs/v1.php/apps/files_sharing/api/v1/";
    private final static String SHARES_PART= ROOT_PART+"shares";

    private final ConnectorCommon connectorCommon;

    public FilesharingConnector(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
    }
    
    /**
     * Return all shares of this user
     * 
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<Share> getShares()
    {
        return getShares(null, false, false);
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
    public Collection<Share> getShares(String path, boolean reShares, boolean subShares)
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (path != null)
        {
            queryParams.add(new BasicNameValuePair("path", path));
        }
        if (reShares)
        {
            queryParams.add(new BasicNameValuePair("reshares", "true"));
        }
        if (subShares)
        {
            queryParams.add(new BasicNameValuePair("subfiles", "true"));
        }
        String queryAnswer= connectorCommon.executeGet(SHARES_PART, queryParams);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        SharesXMLAnswer xa= new SharesXMLAnswer();
        xa.parseAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            return xa.getShares();
        }
        return null;
    }

    /**
     * Return share info for a single share
     * 
     * @param shareId      id of chare (Not path of share)
     * @return 
     * @throws java.lang.Exception 
     */
    public Share getShareInfo(int shareId)
    {
        String queryAnswer= connectorCommon.executeGet(SHARES_PART+"/"+Integer.toString(shareId), null);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        SharesXMLAnswer xa= new SharesXMLAnswer();
        xa.parseAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            if (xa.getShares() == null)
            {
                return null;
            }
            else if (xa.getShares().size() == 1)
            {
                return xa.getShares().get(0);
            }
            else
            {
                LOG.warn("More than one share found, not possible <"+shareId+">");
                return null;
            }
        }
        return null;
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
            SharePermissions permissions)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("path", path));
        postParams.add(new BasicNameValuePair("shareType", Integer.toString(shareType.getIntValue())));
        postParams.add(new BasicNameValuePair("shareWith", shareWithUserOrGroupId));
        if (publicUpload != null)
        {
            postParams.add(new BasicNameValuePair("publicUpload", publicUpload ? "true" : "false"));
        }
        if (password != null)
        {
            postParams.add(new BasicNameValuePair("password", password));
        }
        if (permissions != null)
        {
            postParams.add(new BasicNameValuePair("permissions", Integer.toString(permissions.getCurrentPermission())));
        }
        
        String postAnswer= connectorCommon.executePost(SHARES_PART, postParams);
        if (postAnswer != null)
        {
            LOG.debug("Create share answer "+postAnswer);
            SingleShareXMLAnswer xa= new SingleShareXMLAnswer();
            xa.parseAnswer(postAnswer);
            if (xa.getStatusCode() == NC_OK)
            {
                return xa.getShare();
            }
            else
            {
                return null;
            }
        }
        else
        {
            LOG.debug("Create share failed for path "+path+" user/group "+shareWithUserOrGroupId);
        }
        return null;
    }
}
