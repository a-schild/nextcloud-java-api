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

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.MoreThanOneShareFoundException;
import org.aarboard.nextcloud.api.provisioning.ShareData;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.utils.NextcloudResponseHelper;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author a.schild
 *
 * <a href="https://docs.nextcloud.com/server/11/developer_manual/core/ocs-share-api.html">...</a>
 * When specifying paths, you don't have to specify any webdav roots, or 
 * remote.php ... stuff. Just use the root of your file store directly
 *
 */
public class FilesharingConnector
{

    private static final String ROOT_PART= "ocs/v1.php/apps/files_sharing/api/v1/";
    private static final String SHARES_PART= ROOT_PART+"shares";
    private static final String REMOTE_SHARES_PART= ROOT_PART+"remote_shares";

    private final ConnectorCommon connectorCommon;

    public FilesharingConnector(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
    }

    /**
     * Get all shares of this user
     *
     * @return all shares
     */
    public List<Share> getShares()
    {
        return getShares(null, false, false);
    }

    /**
     * Get all shares of this user asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getSharesAsync()
    {
        return getSharesAsync(null, false, false);
    }

    /**
     * Gets all shares from a given file/folder
     *
     * @param path      path to file/folder
     * @param reShares  returns not only the shares from the current user but all shares from the given file
     * @param subShares returns all shares within a folder, given that path defines a folder
     * @return matching shares
     */
    public List<Share> getShares(String path, boolean reShares, boolean subShares)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getSharesAsync(path,reShares,subShares)).getShares();
    }

    /**
     * Gets all shares from a given file/folder asynchronously
     *
     * @param path      path to file/folder
     * @param reShares  returns not only the shares from the current user but all shares from the given file
     * @param subShares returns all shares within a folder, given that path defines a folder
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getSharesAsync(String path, boolean reShares, boolean subShares)
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
        return connectorCommon.executeGet(SHARES_PART, queryParams, XMLAnswerParser.getInstance(SharesXMLAnswer.class));
    }

    /**
     * Get share info for a single share
     *
     * @param shareId      id of share (Not path of share)
     * @return the share if it has been found, otherwise null
     */
    public Share getShareInfo(int shareId)
    {
        SharesXMLAnswer xa= NextcloudResponseHelper.getAndCheckStatus(getShareInfoAsync(shareId));
        if (xa.getShares() == null)
        {
            return null;
        }
        else if (xa.getShares().size() == 1)
        {
            return xa.getShares().get(0);
        }
        throw new MoreThanOneShareFoundException(shareId);
    }

    /**
     * Get share info for a single share asynchronously
     *
     * @param shareId      id of share (Not path of share)
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SharesXMLAnswer> getShareInfoAsync(int shareId)
    {
        return connectorCommon.executeGet(SHARES_PART+"/"+Integer.toString(shareId), null, XMLAnswerParser.getInstance(SharesXMLAnswer.class));
    }

    /**
     * Shares the specified path with the provided parameters
     *
     * @param path                  path to the file/folder which should be shared
     * @param shareType             0 = user; 1 = group; 3 = public link; 4 = email; 6 = federated cloud share
     * @param shareWithUserOrGroupIdOrEmail user / group id / email with which the file should be shared
     * @param publicUpload          allow public upload to a public shared folder (true/false)
     * @param password              password to protect public link Share with
     * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
     * @return created share on success
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupIdOrEmail,
            Boolean publicUpload,
            String password,
            SharePermissions permissions)
    {
        return doShare(path, shareType, shareWithUserOrGroupIdOrEmail, publicUpload, password, permissions, null);
    }

    /**
     * Shares the specified path with the provided parameters
     *
     * @param path                  path to the file/folder which should be shared
     * @param shareType             0 = user; 1 = group; 3 = public link; 4 = email; 6 = federated cloud share
     * @param shareWithUserOrGroupIdOrEmail user / group id / email with which the file should be shared
     * @param publicUpload          allow public upload to a public shared folder (true/false)
     * @param password              password to protect public link Share with
     * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
     * @param expireDate            expiration date of the share, or null for no expiration
     * @return created share on success
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupIdOrEmail,
            Boolean publicUpload,
            String password,
            SharePermissions permissions,
            LocalDate expireDate)
    {
        return NextcloudResponseHelper.getAndCheckStatus(doShareAsync(path, shareType, shareWithUserOrGroupIdOrEmail, publicUpload, password, permissions, expireDate)).getShare();
    }

    /**
    * Shares the specified path with the provided parameters asynchronously
    *
    * @param path                  path to the file/folder which should be shared
    * @param shareType             0 = user; 1 = group; 3 = public link; 4 = email; 6 = federated cloud share
    * @param shareWithUserOrGroupIdOrEmail user / group id / email with which the file should be shared
    * @param publicUpload          allow public upload to a public shared folder (true/false)
    * @param password              password to protect public link Share with
    * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
    * @return a CompletableFuture containing the result of the operation
    */
    public CompletableFuture<SingleShareXMLAnswer> doShareAsync(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupIdOrEmail,
            Boolean publicUpload,
            String password,
            SharePermissions permissions)
    {
        return doShareAsync(path, shareType, shareWithUserOrGroupIdOrEmail, publicUpload, password, permissions, null);
    }

    /**
    * Shares the specified path with the provided parameters asynchronously
    *
    * @param path                  path to the file/folder which should be shared
    * @param shareType             0 = user; 1 = group; 3 = public link; 4 = email; 6 = federated cloud share
    * @param shareWithUserOrGroupIdOrEmail user / group id / email with which the file should be shared
    * @param publicUpload          allow public upload to a public shared folder (true/false)
    * @param password              password to protect public link Share with
    * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
    * @param expireDate            expiration date of the share, or null for no expiration
    * @return a CompletableFuture containing the result of the operation
    */
    public CompletableFuture<SingleShareXMLAnswer> doShareAsync(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupIdOrEmail,
            Boolean publicUpload,
            String password,
            SharePermissions permissions,
            LocalDate expireDate)
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("path", path));
        postParams.add(new BasicNameValuePair("shareType", Integer.toString(shareType.getIntValue())));
        postParams.add(new BasicNameValuePair("shareWith", shareWithUserOrGroupIdOrEmail));
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
        if (expireDate != null)
        {
            postParams.add(new BasicNameValuePair("expireDate", expireDate.toString()));
        }

        return connectorCommon.executePost(SHARES_PART, postParams, XMLAnswerParser.getInstance(SingleShareXMLAnswer.class));
    }

    /**
     * Changes a single attribute of a share
     *
     * @param shareId unique identifier of the share
     * @param key the attribute to change
     * @param value the value to set
     * @return true if the operation succeeded
     */
    public boolean editShare(int shareId, ShareData key, String value)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(editShareAsync(shareId, key, value));
    }

    /**
     * Changes a single attribute of a share asynchronously
     *
     * @param shareId unique identifier of the share
     * @param key the attribute to change
     * @param value the value to set
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> editShareAsync(int shareId, ShareData key, String value)
    {
        List<NameValuePair> queryParams= Collections.singletonList(new BasicNameValuePair(key.parameterName, value));
        return connectorCommon.executePut(SHARES_PART, Integer.toString(shareId), queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Changes multiple attributes of a share at once
     *
     * @param shareId unique identifier of the share
     * @param values a Map containing the attributes to set
     * @return true if the operation succeeded
     */
    public boolean editShare(int shareId, Map<ShareData,String> values)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(editShareAsync(shareId, values));
    }

    /**
     * Changes multiple attributes of a share at once asynchronously
     *
     * @param shareId unique identifier of the share
     * @param values a Map containing the attributes to set
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> editShareAsync(int shareId, Map<ShareData, String> values) {
        List<NameValuePair> queryParams = values.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey().parameterName, e.getValue())).collect(Collectors.toList());
        return connectorCommon.executePut(SHARES_PART, Integer.toString(shareId), queryParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Deletes a share
     *
     * @param shareId unique identifier of the share
     * @return true if the operation succeeded
     */
    public boolean deleteShare(int shareId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteShareAsync(shareId));
    }

    /**
     * Deletes a share asynchronously
     *
     * @param shareId unique identifier of the share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> deleteShareAsync(int shareId)
    {
        return connectorCommon.executeDelete(SHARES_PART, Integer.toString(shareId), null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * (Re)sends the share notification email to the recipients of a share
     *
     * @param shareId unique identifier of the share
     * @return true if the operation succeeded
     */
    public boolean sendShareEmail(int shareId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(sendShareEmailAsync(shareId));
    }

    /**
     * (Re)sends the share notification email to the recipients of a share asynchronously
     *
     * @param shareId unique identifier of the share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> sendShareEmailAsync(int shareId)
    {
        return connectorCommon.executePost(SHARES_PART+"/"+shareId+"/send-email", XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all accepted federated (server-to-server) shares of this user
     *
     * @return accepted remote shares
     */
    public List<RemoteShare> getRemoteShares()
    {
        return NextcloudResponseHelper.getAndCheckStatus(getRemoteSharesAsync()).getRemoteShares();
    }

    /**
     * Gets all accepted federated (server-to-server) shares of this user asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<RemoteSharesXMLAnswer> getRemoteSharesAsync()
    {
        return connectorCommon.executeGet(REMOTE_SHARES_PART, null, XMLAnswerParser.getInstance(RemoteSharesXMLAnswer.class));
    }

    /**
     * Gets information about a single accepted federated share
     *
     * @param remoteShareId id of the remote share
     * @return the remote share if found, otherwise null
     */
    public RemoteShare getRemoteShareInfo(int remoteShareId)
    {
        return NextcloudResponseHelper.getAndCheckStatus(getRemoteShareInfoAsync(remoteShareId)).getRemoteShare();
    }

    /**
     * Gets information about a single accepted federated share asynchronously
     *
     * @param remoteShareId id of the remote share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<SingleRemoteShareXMLAnswer> getRemoteShareInfoAsync(int remoteShareId)
    {
        return connectorCommon.executeGet(REMOTE_SHARES_PART+"/"+remoteShareId, null, XMLAnswerParser.getInstance(SingleRemoteShareXMLAnswer.class));
    }

    /**
     * Deletes (unshares) an accepted federated share
     *
     * @param remoteShareId id of the remote share
     * @return true if the operation succeeded
     */
    public boolean deleteRemoteShare(int remoteShareId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(deleteRemoteShareAsync(remoteShareId));
    }

    /**
     * Deletes (unshares) an accepted federated share asynchronously
     *
     * @param remoteShareId id of the remote share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> deleteRemoteShareAsync(int remoteShareId)
    {
        return connectorCommon.executeDelete(REMOTE_SHARES_PART, Integer.toString(remoteShareId), null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Gets all pending (not yet accepted) federated shares of this user
     *
     * @return pending remote shares
     */
    public List<RemoteShare> getPendingRemoteShares()
    {
        return NextcloudResponseHelper.getAndCheckStatus(getPendingRemoteSharesAsync()).getRemoteShares();
    }

    /**
     * Gets all pending (not yet accepted) federated shares of this user asynchronously
     *
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<RemoteSharesXMLAnswer> getPendingRemoteSharesAsync()
    {
        return connectorCommon.executeGet(REMOTE_SHARES_PART+"/pending", null, XMLAnswerParser.getInstance(RemoteSharesXMLAnswer.class));
    }

    /**
     * Accepts a pending federated share
     *
     * @param remoteShareId id of the pending remote share
     * @return true if the operation succeeded
     */
    public boolean acceptPendingRemoteShare(int remoteShareId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(acceptPendingRemoteShareAsync(remoteShareId));
    }

    /**
     * Accepts a pending federated share asynchronously
     *
     * @param remoteShareId id of the pending remote share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> acceptPendingRemoteShareAsync(int remoteShareId)
    {
        return connectorCommon.executePost(REMOTE_SHARES_PART+"/pending/"+remoteShareId, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Declines a pending federated share
     *
     * @param remoteShareId id of the pending remote share
     * @return true if the operation succeeded
     */
    public boolean declinePendingRemoteShare(int remoteShareId)
    {
        return NextcloudResponseHelper.isStatusCodeOkay(declinePendingRemoteShareAsync(remoteShareId));
    }

    /**
     * Declines a pending federated share asynchronously
     *
     * @param remoteShareId id of the pending remote share
     * @return a CompletableFuture containing the result of the operation
     */
    public CompletableFuture<XMLAnswer> declinePendingRemoteShareAsync(int remoteShareId)
    {
        return connectorCommon.executeDelete(REMOTE_SHARES_PART+"/pending", Integer.toString(remoteShareId), null, XMLAnswerParser.getInstance(XMLAnswer.class));
    }
}
