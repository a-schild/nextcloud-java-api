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

    public int getId() {
        return id;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public SharePermissions getSharePermissions() {
        return sharePermissions;
    }

    public String getFileOwnerId() {
        return fileOwnerId;
    }

    public String getFileOwnerDisplayName() {
        return fileOwnerDisplayName;
    }

    public String getPath() {
        return path;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getFileTarget() {
        return fileTarget;
    }

    public String getShareWithId() {
        return shareWithId;
    }

    public String getShareWithDisplayName() {
        return shareWithDisplayName;
    }

    public String getToken() {
        return token;
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
