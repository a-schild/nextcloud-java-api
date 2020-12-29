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
package org.aarboard.nextcloud.api.webdav;

import java.nio.file.Paths;
import java.text.MessageFormat;

public class WebDavPathResolverBuilder
{

    private final static String PATH_TEMPLATE = "/{0}/";
    private final static String DEFAULT_WEB_DAV_BASE_PATH = "remote.php/webdav/";

    private WebDavPathResolverImpl result;

    public static WebDavPathResolverBuilder get()
    {
        return new WebDavPathResolverBuilder();
    }

    WebDavPathResolverBuilder()
    {
        result = new WebDavPathResolverImpl(DEFAULT_WEB_DAV_BASE_PATH);
    }

    /**
     * The remoteuser
     *
     * @param user the remoteuser
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder withUserName(String user)
    {
        if (null != user)
        {
            result.setUserName(user);
        }
        return this;
    }

    /**
     * E.g. nextcloud
     *
     * @param suffix of the basepath
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder withBasePathSuffix(String suffix)
    {
        if (null != suffix)
        {
            result.setPathSuffix(suffix);
        }

        return this;
    }

    /**
     * E.g. files,calendars
     *
     * @param prefix the suffix of the base path
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder withBasePathPrefix(String prefix)
    {
        if (null != prefix)
        {
            result.setPathPrefix(prefix);
        }

        return this;
    }

    /**
     * E.g. remote.php/dav
     *
     * @param path of the nextcloud webdav interface
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder withBasePath(String path)
    {
        if (null == path)
        {
            throw new IllegalArgumentException("WebDav base path cannot be null!");
        }

        result.setWebDavBasePath(path);
        return this;
    }

    /**
     * builds the resolver
     *
     * @return the resolver
     * @see WebDavPathResolver
     */
    public WebDavPathResolver build()
    {
        return result;
    }

    private static class WebDavPathResolverImpl implements WebDavPathResolver
    {

        private String webDavBasePath;
        private String userName;
        private String pathSuffix;
        private String pathPrefix;
        private String webdavPath = null;

        public WebDavPathResolverImpl(String webDavBasePath)
        {
            this.webDavBasePath = webDavBasePath;
            this.userName = "";
            this.pathSuffix = "";
            this.pathPrefix = "";
        }

        private void setPathPrefix(String pathPrefix)
        {
            this.pathPrefix = pathPrefix;
        }

        private void setWebDavBasePath(String webDavBasePath)
        {
            this.webDavBasePath = webDavBasePath;
        }

        private void setUserName(String userName)
        {
            this.userName = userName;
        }

        private void setPathSuffix(String pathSuffix)
        {
            this.pathSuffix = pathSuffix;
        }

        private String getWebdavPath()
        {
            if (null == this.webdavPath)
            {
                this.webdavPath = Paths.get(this.pathPrefix, this.webDavBasePath, this.pathSuffix, this.userName).toString();
            }
            return this.webdavPath;
        }

        @Override
        public String getWebDavFilesPath(String... remotePath)
        {
            return MessageFormat.format(PATH_TEMPLATE, Paths.get(getWebdavPath(), remotePath).toString());
        }

    }
}
