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
package org.aarboard.nextcloud.api.filesharing;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * A federated (server-to-server) share as returned by the OCS
 * {@code remote_shares} endpoints.
 *
 * @author a.schild
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteShare
{
    private int         id;
    private String      remote;
    @XmlElement(name = "remote_id")
    private String      remoteId;
    @XmlElement(name = "share_token")
    private String      shareToken;
    private String      name;
    private String      owner;
    private String      user;
    private String      mountpoint;
    private String      mimetype;
    private String      mtime;
    private String      permissions;
    private String      type;
    @XmlElement(name = "file_id")
    private String      fileId;
    private boolean     accepted;

    public int getId() {
        return id;
    }

    public String getRemote() {
        return remote;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public String getShareToken() {
        return shareToken;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getUser() {
        return user;
    }

    public String getMountpoint() {
        return mountpoint;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getMtime() {
        return mtime;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getType() {
        return type;
    }

    public String getFileId() {
        return fileId;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
