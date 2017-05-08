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

/**
 *
 * @author a.schild
 */
public class Share 
{
    private int         id;
    private ShareType   shareType;
    private String      ownerId;
    private String      ownerDisplayName;
    private SharePermissions    sharePermissions;
    private String      fileOwnerId;
    private String      fileOwnerDisplayName;
    private String      path;
    private ItemType    itemType;
    private String      fileTarget;
    private String      shareWithId;
    private String      shareWithDisplayName;
    private String      token;

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
    * @param token the token to set
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
    
    
}
