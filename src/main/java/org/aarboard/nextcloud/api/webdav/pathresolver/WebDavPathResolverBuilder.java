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
package org.aarboard.nextcloud.api.webdav.pathresolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 11.5
 */
public class WebDavPathResolverBuilder
{

    public final static String NEXTCLOUD_WEBDAV_BASE_PATH = "nextcloud.webdav.base.path";

    public final static String NEXTCLOUD_WEBDAV_BASE_PATH_SUFFIX = "nextcloud.webdav.base.suffix.path";

    public final static String NEXTCLOUD_WEBDAV_BASE_PATH_PREFIX = "nextcloud.webdav.base.prefix.path";

    public final static String NEXTCLOUD_USER_NAME = "nextcloud.userName";

    /**
     * Type of the resolver FILES, VCARD, CALDAV, VERSION
     */
    public enum TYPE
    {
        /**
         * specifies to calculate the files path of the nextcloud instance
         */
        FILES("files"),
        /**
         * VCARD path
         */
        VCARD("addressbooks/users"),
        /**
         * CALDAV
         */
        CALDAV("calendars"),
        /**
         * specifies to calculate the Version path of the nextcloud instance
         */
        VERSION("");

        private final String suffix;

        private TYPE(String suffix)
        {
            this.suffix = suffix;
        }

        public String getSuffix()
        {
            return suffix;
        }

    }

    private final static String PATH_TEMPLATE = "/{0}/";

    private WebDavPathResolver result;

    private TYPE type;

    /**
     * Defaults to the latest
     */
    private NextcloudVersion version = NextcloudVersion.get("20.0.4");

    private Map<String, String> valueMap = new HashMap<>();

    public static WebDavPathResolverBuilder get(final TYPE type)
    {
        return new WebDavPathResolverBuilder(type);
    }

    WebDavPathResolverBuilder(final TYPE type)
    {
        this.type = type;
    }

    /**
     *
     * @param version of the nextcloud instance
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder ofVersion(NextcloudVersion version)
    {
        this.version = version.ofType(type);
        return this;
    }

    /**
     * The remoteuser
     *
     * @param user the remoteuser (Must be the username and not the login name if they are not identical)
     * @return the WebDavPathResolverBuilder
     */
    public WebDavPathResolverBuilder withUserName(String user)
    {
        if (null != user)
        {
            valueMap.put(NEXTCLOUD_USER_NAME, user);
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
            valueMap.put(NEXTCLOUD_WEBDAV_BASE_PATH_SUFFIX, suffix);
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
            valueMap.put(NEXTCLOUD_WEBDAV_BASE_PATH_PREFIX, prefix);
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

        valueMap.put(NEXTCLOUD_WEBDAV_BASE_PATH, path);
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
        switch (type)
        {
            case FILES:
            case CALDAV:
            case VCARD:
                result = new FolderWebDavPathResolverImpl(valueMap.getOrDefault(NEXTCLOUD_WEBDAV_BASE_PATH, version.getWebdavBasePath()))
                        .setPathPrefix(valueMap.getOrDefault(NEXTCLOUD_WEBDAV_BASE_PATH_PREFIX, ""))
                        .setPathSuffix(version.isAppendSuffix() ? valueMap.getOrDefault(NEXTCLOUD_WEBDAV_BASE_PATH_SUFFIX, type.getSuffix()) : "")
                        .setUserName(version.isAppendUserName() ? valueMap.getOrDefault(NEXTCLOUD_USER_NAME, "") : "");

                break;
            case VERSION:
                result = new WebDavpathResolverVersionImpl(valueMap.getOrDefault(NEXTCLOUD_WEBDAV_BASE_PATH, "status.php"))//
                        .setPathPrefix(valueMap.getOrDefault(NEXTCLOUD_WEBDAV_BASE_PATH_PREFIX, ""));
                break;
            default:
                result = null;
                break;

        }

        return result;
    }

    private static class FolderWebDavPathResolverImpl implements WebDavPathResolver
    {

        private String webDavBasePath;
        private String userName;
        private String pathSuffix;
        private String pathPrefix;
        private String webdavPath = null;

        public FolderWebDavPathResolverImpl(String webDavBasePath)
        {
            this.webDavBasePath = webDavBasePath;
            this.userName = "";
            this.pathSuffix = "";
            this.pathPrefix = "";
        }

        private FolderWebDavPathResolverImpl setPathPrefix(String pathPrefix)
        {
            this.pathPrefix = pathPrefix;
            return this;
        }

        private FolderWebDavPathResolverImpl setWebDavBasePath(String webDavBasePath)
        {
            this.webDavBasePath = webDavBasePath;
            return this;
        }

        private FolderWebDavPathResolverImpl setUserName(String userName)
        {
            this.userName = userName;
            return this;
        }

        private FolderWebDavPathResolverImpl setPathSuffix(String pathSuffix)
        {
            this.pathSuffix = pathSuffix;
            return this;
        }

        private String getWebdavPath()
        {
            if (null == this.webdavPath)
            {
                // We can't use Paths here, since we need / as separator, and not a platform specific delimiter
                // which might be something else
                this.webdavPath= PathHelper.concatPathElements(
                        true,
                        this.pathPrefix,
                        this.webDavBasePath,
                        this.pathSuffix,
                        this.userName);
            }
            return this.webdavPath;
        }

        @Override
        public String getWebDavPath(String ... remotePath)
        {
            return PathHelper.concatPathElements(true, getWebdavPath(), remotePath);
        }
    }

}
