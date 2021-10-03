/*
 * Copyright (C) 2021 scm11361
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
package org.aarboard.nextcloud.api.webdav.pathresolver;


public class WebDavpathResolverVersionImpl implements WebDavPathResolver
{

    private final String versionPath;

    private String pathPrefix;

    public WebDavpathResolverVersionImpl(String versionPath)
    {
        this.versionPath = versionPath;
    }

    public String getPathPrefix()
    {
        return pathPrefix;
    }

    public WebDavpathResolverVersionImpl setPathPrefix(String pathPrefix)
    {
        this.pathPrefix = pathPrefix;
        return this;
    }

    private String getWebdavPath()
    {
        return PathHelper.concatPathElements(true, pathPrefix, versionPath);
    }

    @Override
    public String getWebDavPath(String... remotePaths) {
        return PathHelper.concatPathElements(false, getWebdavPath(), remotePaths);
    }

}
