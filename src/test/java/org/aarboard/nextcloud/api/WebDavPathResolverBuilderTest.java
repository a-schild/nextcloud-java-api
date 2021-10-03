/*
 * Copyright (C) 2020 scm11361
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

import org.aarboard.nextcloud.api.webdav.pathresolver.NextcloudVersion;
import org.aarboard.nextcloud.api.webdav.pathresolver.WebDavPathResolverBuilder;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class WebDavPathResolverBuilderTest
{

    @Test
    public void testBuilder_version_1()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.VERSION).build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/status.php", result);

    }

    @Test
    public void testBuilder_version_2()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.VERSION)
                .withBasePathSuffix("x").withUserName("x").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/status.php", result);

    }

    @Test
    public void testBuilder_version_3()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.VERSION)
                .withBasePathPrefix("nextcloud").withBasePathSuffix("x")
                .withUserName("x").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/nextcloud/status.php", result);

    }

    @Test
    public void testBuilder_version_4()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.VERSION)
                .withBasePath("/mypath/other_status.php")
                .withBasePathPrefix("nextcloud")
                .withBasePathSuffix("x")
                .withUserName("x").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/nextcloud/mypath/other_status.php", result);

    }

    @Test
    public void testBuilder_folder_1()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.FILES)
                .withBasePathPrefix("nextcloud").withBasePathSuffix("suf")
                .withUserName("user").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/nextcloud/remote.php/dav/suf/user/", result);

    }

    @Test
    public void testBuilder_folder_2()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.FILES)
                .ofVersion(NextcloudVersion.get("14.0.1"))
                .withBasePathPrefix("nextcloud").withBasePathSuffix("suf").withUserName("user").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/nextcloud/remote.php/webdav/", result);

    }

    @Test
    public void testBuilder_folder_3()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.FILES)
                .ofVersion(NextcloudVersion.get("20.0.1"))
                .withBasePathPrefix("nextcloud")
                .withUserName("user").build().getWebDavPath();

        assertNotNull(result);

        Assert.assertEquals("/nextcloud/remote.php/dav/files/user/", result);

    }

    @Test
    public void testBuilder_folder_4()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.CALDAV)
                .withBasePathSuffix("calendar")
                .ofVersion(NextcloudVersion.get("20.0.1"))
                .withBasePathPrefix("nextcloud")
                .withUserName("angus").build().getWebDavPath();

        assertNotNull(result);
        Assert.assertEquals("/nextcloud/remote.php/dav/calendar/angus/", result);

    }

    @Test
    public void testBuilder_folder_5()
    {
        String result = WebDavPathResolverBuilder.get(WebDavPathResolverBuilder.TYPE.CALDAV)
                .ofVersion(NextcloudVersion.get("20.0.1"))
                .withBasePathPrefix("nextcloud")
                .withUserName("angus").build().getWebDavPath();

        assertNotNull(result);
        Assert.assertEquals("/nextcloud/remote.php/dav/calendars/angus/", result);

    }
}
