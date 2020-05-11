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
package org.aarboard.nextcloud.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.aarboard.nextcloud.api.provisioning.User;
import org.aarboard.nextcloud.api.provisioning.UserData;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author a.schild
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestConfigConnector extends ATestClass {
    
    @Test
    public void t01_testGetConfigApps() {
        System.out.println("getConfigApps");
        if (_nc != null)
        {
            List<String> apps = _nc.getAppConfigApps();
            assertTrue(apps != null);
        }
    }

    @Test
    public void t02_testGetConfigAppsFiles() {
        System.out.println("getConfigAppsFiles");
        if (_nc != null)
        {
            List<String> appKeys = _nc.getAppConfigAppKeys("files");
            assertTrue(appKeys != null);
        }
    }

    @Test
    public void t03_testGetConfigAppsFilesVersion() {
        System.out.println("getConfigAppsFiles");
        if (_nc != null)
        {
            String version = _nc.getAppConfigAppKeyValue("files", "installed_version");
            assertTrue(version != null);
        }
    }
}
