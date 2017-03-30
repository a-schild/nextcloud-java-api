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
 * https://docs.nextcloud.com/server/11/developer_manual/core/ocs-share-api.html
 * @author a.schild
 */
public class SharePermissions {

    public enum SingleRight {
        READ(1),
        UPDATE(2),
        CREATE(4),
        DELETE(8),
        SHARE(16);

        private final int intValue;

        private SingleRight(int iV) {
            intValue= iV;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    
    private final int currentPermission;

    public SharePermissions(int currentPermission) {
        this.currentPermission = currentPermission;
    }
    
    public boolean hasAllRights()
    {
        return currentPermission == (
                SingleRight.READ.getIntValue()+
                SingleRight.UPDATE.getIntValue()+
                SingleRight.CREATE.getIntValue()+
                SingleRight.DELETE.getIntValue()+
                SingleRight.SHARE.getIntValue()
                );
    }

    public int getCurrentPermission() {
        return currentPermission;
    }
    
    
}
