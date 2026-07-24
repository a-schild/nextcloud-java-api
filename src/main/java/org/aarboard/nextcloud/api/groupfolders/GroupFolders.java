/*
 * Copyright (C) 2026 a.schild
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
package org.aarboard.nextcloud.api.groupfolders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.utils.EmptyAnswerParser;
import org.aarboard.nextcloud.api.utils.JsonAnswerParser;
import org.aarboard.nextcloud.api.utils.NextcloudResponseHelper;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Access to the <a href="https://github.com/nextcloud/groupfolders">Group
 * Folders</a> app API. The app must be installed and enabled on the server.
 *
 * @author a.schild
 */
public class GroupFolders {

    private static final String GROUP_FOLDERS_ROOT = "index.php/apps/groupfolders/folders";

    private final ConnectorCommon connectorCommon;

    public GroupFolders(ServerConfig serverConfig) {
        this.connectorCommon = new ConnectorCommon(serverConfig);
    }

    /**
     * Creates a new group folder with the given mount point (name).
     *
     * @param mountPoint name/mount point of the group folder
     * @return the id of the newly created group folder
     */
    public int createGroupFolder(String mountPoint) {
        return NextcloudResponseHelper.getAndCheckStatus(createGroupFolderAsync(mountPoint)).getId();
    }

    public CompletableFuture<GroupFolderAnswer> createGroupFolderAsync(String mountPoint) {
        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("mountpoint", mountPoint));
        return connectorCommon.executePost(GROUP_FOLDERS_ROOT, postParams,
                JsonAnswerParser.getInstance(GroupFolderAnswer.class));
    }

    /**
     * Renames (changes the mount point of) a group folder.
     *
     * @param groupFolderId id of the group folder
     * @param newMountPoint new name/mount point
     */
    public void renameGroupFolder(int groupFolderId, String newMountPoint) {
        NextcloudResponseHelper.getAndCheckStatus(renameGroupFolderAsync(groupFolderId, newMountPoint));
    }

    public CompletableFuture<XMLAnswer> renameGroupFolderAsync(int groupFolderId, String newMountPoint) {
        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("mountpoint", newMountPoint));
        String url = String.format("%s/%d/mountpoint", GROUP_FOLDERS_ROOT, groupFolderId);
        return connectorCommon.executePost(url, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Deletes a group folder.
     *
     * @param groupFolderId id of the group folder
     */
    public void deleteGroupFolder(int groupFolderId) {
        NextcloudResponseHelper.getAndCheckStatus(deleteGroupFolderAsync(groupFolderId));
    }

    public CompletableFuture<XMLAnswer> deleteGroupFolderAsync(int groupFolderId) {
        return connectorCommon.executeDelete(GROUP_FOLDERS_ROOT, String.valueOf(groupFolderId),
                XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * @return all group folders visible to the current user
     */
    public Collection<GroupFolderInfo> getGroupFolders() {
        return NextcloudResponseHelper.getAndCheckStatus(getGroupFoldersAsync()).getAllGroupFolders();
    }

    public CompletableFuture<GroupFoldersListAnswer> getGroupFoldersAsync() {
        return connectorCommon.executeGet(GROUP_FOLDERS_ROOT,
                JsonAnswerParser.getInstance(GroupFoldersListAnswer.class));
    }

    /**
     * Grants a group access to a group folder.
     *
     * @param groupFolderId id of the group folder
     * @param group         group id to grant access to
     */
    public void grantAccess(int groupFolderId, String group) {
        NextcloudResponseHelper.getAndCheckStatus(grantAccessAsync(groupFolderId, group));
    }

    public CompletableFuture<XMLAnswer> grantAccessAsync(int groupFolderId, String group) {
        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("group", group));
        String url = String.format("%s/%d/groups", GROUP_FOLDERS_ROOT, groupFolderId);
        return connectorCommon.executePost(url, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Revokes a group's access to a group folder.
     *
     * @param groupFolderId id of the group folder
     * @param group         group id to revoke access from
     */
    public void revokeAccess(int groupFolderId, String group) {
        NextcloudResponseHelper.getAndCheckStatus(revokeAccessAsync(groupFolderId, group));
    }

    public CompletableFuture<XMLAnswer> revokeAccessAsync(int groupFolderId, String group) {
        return connectorCommon.executeDelete(GROUP_FOLDERS_ROOT,
                String.format("%d/groups/%s", groupFolderId, group),
                XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Sets the permissions a group has on a group folder.
     *
     * @param groupFolderId id of the group folder
     * @param group         group id
     * @param permissions   permission bit mask (1 = read, 2 = update, 4 =
     *                      create, 8 = delete, 16 = share, 31 = all)
     */
    public void setGroupPermissions(int groupFolderId, String group, int permissions) {
        NextcloudResponseHelper.getAndCheckStatus(setGroupPermissionsAsync(groupFolderId, group, permissions));
    }

    public CompletableFuture<XMLAnswer> setGroupPermissionsAsync(int groupFolderId, String group, int permissions) {
        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("permissions", String.valueOf(permissions)));
        String url = String.format("%s/%d/groups/%s", GROUP_FOLDERS_ROOT, groupFolderId, group);
        return connectorCommon.executePost(url, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }

    /**
     * Sets the quota of a group folder.
     *
     * @param groupFolderId id of the group folder
     * @param quota         quota in bytes, or {@code -3} for an unlimited quota
     */
    public void setQuota(int groupFolderId, long quota) {
        NextcloudResponseHelper.getAndCheckStatus(setQuotaAsync(groupFolderId, quota));
    }

    public CompletableFuture<XMLAnswer> setQuotaAsync(int groupFolderId, long quota) {
        List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("quota", String.valueOf(quota)));
        String url = String.format("%s/%d/quota", GROUP_FOLDERS_ROOT, groupFolderId);
        return connectorCommon.executePost(url, postParams, XMLAnswerParser.getInstance(XMLAnswer.class));
    }
}
