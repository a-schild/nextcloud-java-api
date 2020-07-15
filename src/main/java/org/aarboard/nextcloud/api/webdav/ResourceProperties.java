/*
 * Copyright (C) 2020 a.schild
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
package org.aarboard.nextcloud.api.webdav;

import java.util.Date;

/**
 *
 * @author a.schild
 */
public class ResourceProperties {
    private long    contentLength= -1;
    private String  contentType= null;
    private Date    creation= null;
    private String  displayName= null;
    private String  etag= null;
    private Date    modified= null;
    
    private String resourceType;
    private String id;
    private String fileId;
    private boolean favorite;
    private String  commentsHref;
    private long    commentsCount;
    private long    commentsUnread;
    private String  ownerId;
    private String  ownerDisplayName;
    private String  shareTypes;
    private String  permissions;
    private String  checksums;
    private boolean hasPreview;
    private long    size; // Unlike contentlength, this property also works for folders reporting the size of everything in the folder

    public ResourceProperties() {
    }

    
    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     * @param etag the etag to set
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the contentLength
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * @param contentLength the contentLength to set
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * @return the creation
     */
    public Date getCreation() {
        return creation;
    }

    /**
     * @param creation the creation to set
     */
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    /**
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the favorite
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * @param favorite the favorite to set
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * @return the commentsHref
     */
    public String getCommentsHref() {
        return commentsHref;
    }

    /**
     * @param commentsHref the commentsHref to set
     */
    public void setCommentsHref(String commentsHref) {
        this.commentsHref = commentsHref;
    }

    /**
     * @return the commentsCount
     */
    public long getCommentsCount() {
        return commentsCount;
    }

    /**
     * @param commentsCount the commentsCount to set
     */
    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    /**
     * @return the commentsUnread
     */
    public long getCommentsUnread() {
        return commentsUnread;
    }

    /**
     * @param commentsUnread the commentsUnread to set
     */
    public void setCommentsUnread(long commentsUnread) {
        this.commentsUnread = commentsUnread;
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
     * @return the shareTypes
     */
    public String getShareTypes() {
        return shareTypes;
    }

    /**
     * @param shareTypes the shareTypes to set
     */
    public void setShareTypes(String shareTypes) {
        this.shareTypes = shareTypes;
    }

    /**
     * @return the checksums
     */
    public String getChecksums() {
        return checksums;
    }

    /**
     * @param checksums the checksums to set
     */
    public void setChecksums(String checksums) {
        this.checksums = checksums;
    }

    /**
     * @return the hasPreview
     */
    public boolean isHasPreview() {
        return hasPreview;
    }

    /**
     * @param hasPreview the hasPreview to set
     */
    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the permissions
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
