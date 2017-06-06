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

import java.time.Instant;
import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.aarboard.nextcloud.api.utils.InstantXmlAdapter;
import org.aarboard.nextcloud.api.utils.LocalDateXmlAdapter;

/**
 *
 * @author a.schild
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Share
{
    private int         id;
    @XmlElement(name = "share_type")
    private ShareType   shareType;
    @XmlElement(name = "uid_owner")
    private String      ownerId;
    @XmlElement(name = "displayname_owner")
    private String      ownerDisplayName;
    @XmlElement(name = "permissions")
    @XmlJavaTypeAdapter(SharePermissionsAdapter.class)
    private SharePermissions    sharePermissions;
    @XmlElement(name = "uid_file_owner")
    private String      fileOwnerId;
    @XmlElement(name = "displayname_file_owner")
    private String      fileOwnerDisplayName;
    private String      path;
    @XmlElement(name = "item_type")
    private ItemType    itemType;
    @XmlElement(name = "file_target")
    private String      fileTarget;
    @XmlElement(name = "share_with")
    private String      shareWithId;
    @XmlElement(name = "share_with_displayname")
    private String      shareWithDisplayName;
    private String      token;
    @XmlElement(name = "stime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant     shareTime;
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    private LocalDate   expiration;
    private String url;
    private String mimetype;

    public Share() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the shareType
     */
    public ShareType getShareType() {
        return shareType;
    }

    /**
     * @param shareType the shareType to set
     */
    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId the ownerId to set
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the ownerDisplayName
     */
    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    /**
     * @param ownerDisplayName the ownerDisplayName to set
     */
    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    /**
     * @return the sharePermissions
     */
    public SharePermissions getSharePermissions() {
        return sharePermissions;
    }

    /**
     * @param sharePermissions the sharePermissions to set
     */
    public void setSharePermissions(SharePermissions sharePermissions) {
        this.sharePermissions = sharePermissions;
    }

    /**
     * @return the fileOwnerId
     */
    public String getFileOwnerId() {
        return fileOwnerId;
    }

    /**
     * @param fileOwnerId the fileOwnerId to set
     */
    public void setFileOwnerId(String fileOwnerId) {
        this.fileOwnerId = fileOwnerId;
    }

    /**
     * @return the fileOwnerDisplayName
     */
    public String getFileOwnerDisplayName() {
        return fileOwnerDisplayName;
    }

    /**
     * @param fileOwnerDisplayName the fileOwnerDisplayName to set
     */
    public void setFileOwnerDisplayName(String fileOwnerDisplayName) {
        this.fileOwnerDisplayName = fileOwnerDisplayName;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the itemType
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * @param itemType the itemType to set
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the fileTarget
     */
    public String getFileTarget() {
        return fileTarget;
    }

    /**
     * @param fileTarget the fileTarget to set
     */
    public void setFileTarget(String fileTarget) {
        this.fileTarget = fileTarget;
    }

    /**
     * @return the shareWithId
     */
    public String getShareWithId() {
        return shareWithId;
    }

    /**
     * @param shareWithId the shareWithId to set
     */
    public void setShareWithId(String shareWithId) {
        this.shareWithId = shareWithId;
    }

    /**
     * @return the shareWithDisplayName
     */
    public String getShareWithDisplayName() {
        return shareWithDisplayName;
    }

    /**
     * @param shareWithDisplayName the shareWithDisplayName to set
     */
    public void setShareWithDisplayName(String shareWithDisplayName) {
        this.shareWithDisplayName = shareWithDisplayName;
    }

    /**
    * @return the token
    */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    public Instant getShareTime() {
        return shareTime;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public String getUrl() {
        return url;
    }

    public String getMimetype() {
        return mimetype;
    }

    private static final class SharePermissionsAdapter extends XmlAdapter<Integer, SharePermissions>
    {
        @Override
        public Integer marshal(SharePermissions sharePermissions)
        {
            return sharePermissions.getCurrentPermission();
        }

        @Override
        public SharePermissions unmarshal(Integer v)
        {
            return new SharePermissions(v);
        }
    }
}
