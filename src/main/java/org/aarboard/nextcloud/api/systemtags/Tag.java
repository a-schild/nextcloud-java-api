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
package org.aarboard.nextcloud.api.systemtags;

/**
 * A Nextcloud system tag.
 *
 * @author a.schild
 */
public class Tag {

    private int id;
    private String name;
    private boolean userVisible;
    private boolean userAssignable;
    private boolean canAssign;

    public Tag() {
    }

    public Tag(int id, String name, boolean userVisible, boolean userAssignable, boolean canAssign) {
        this.id = id;
        this.name = name;
        this.userVisible = userVisible;
        this.userAssignable = userAssignable;
        this.canAssign = canAssign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUserVisible() {
        return userVisible;
    }

    public void setUserVisible(boolean userVisible) {
        this.userVisible = userVisible;
    }

    public boolean isUserAssignable() {
        return userAssignable;
    }

    public void setUserAssignable(boolean userAssignable) {
        this.userAssignable = userAssignable;
    }

    public boolean isCanAssign() {
        return canAssign;
    }

    public void setCanAssign(boolean canAssign) {
        this.canAssign = canAssign;
    }
}
