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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

/**
 * Nextcloud Version represent the version of the nextcloud server. It covers
 * path behaviours of different versions.
 * <p/>
 * Get a new instance:
 * <p/>
 * <code>
 *  NextcloudVersion version = NextcloudVersion.get("20.0.4");
 * </code>
 *
 * @since 11.5
 */
public class NextcloudVersion
{

    private final static String PROPERTIES_PATH_PATTERN = "/org/aarboard/nextcloud/api/webdav/pathresolver/webdavpathresolver_{0}.properties";
    private final static String APPEND_USERNAME_PATTERN = "nextcloud.webdav.base.{0}.suffix.append.username";

    private final String versionValue;

    /**
     * defaults to 20
     */
    private Integer compatibleVersion = 20;
    private String major = "0";
    private String minor = "0";
    private String patch = "0";
    private String revision = "0";

    private Properties configProperties = new Properties();
    /**
     * defaults to files
     */
    private WebDavPathResolverBuilder.TYPE webdavType = WebDavPathResolverBuilder.TYPE.FILES;

    NextcloudVersion(String versionValue)
    {
        this.versionValue = versionValue;
        paresVersionString(versionValue);
        loadValues();
    }

    /**
     * static create factory method
     * Value need to be of form
     * major.minor.path(.revision) E.g. 20.4.0.0 or 14.1.3
     *
     * @param value of NextCloudInstance
     * @return a versionbn instance
     * @since 11.5
     */
    public static NextcloudVersion get(final String value)
    {
        if (null == value)
        {
            throw new IllegalArgumentException("Version value cannot be null !");
        }

        if (value.trim().isEmpty())
        {
            throw new IllegalArgumentException("Version value cannot be empty !");
        }

        return new NextcloudVersion(value);
    }

    public NextcloudVersion ofType(WebDavPathResolverBuilder.TYPE _type)
    {
        this.webdavType = _type;

        return this;
    }

    public String getWebdavBasePath()
    {
        return configProperties.getProperty("nextcloud.webdav.base.path");
    }

    /**
     *
     * @return true if usernae shall appendend to path, false otherwise
     */
    public boolean isAppendUserName()
    {
        return Boolean.parseBoolean(configProperties.getProperty(MessageFormat.format(APPEND_USERNAME_PATTERN, this.webdavType.name().toLowerCase()), "false"));
    }

    /**
     *
     * @return true if appendSuffix shall appendend to path, false otherwise
     */
    public boolean isAppendSuffix()
    {
        return Boolean.parseBoolean(configProperties.getProperty(MessageFormat.format(APPEND_USERNAME_PATTERN, this.webdavType.name().toLowerCase()), "false"));
    }

    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch + "." + revision + ", compatible with [" + compatibleVersion + "]";
    }

    private void paresVersionString(String value)
    {
        String[] values = value.split("\\.");
        if (values.length > 0)
        {
            major = values[0];
            compatibleVersion = Integer.parseInt(major);
        }

        if (values.length > 1)
        {
            minor = values[1];
        }

        if (values.length > 2)
        {
            patch = values[2];
        }

        if (values.length > 3)
        {
            revision = values[3];
        }

        if (compatibleVersion > 14)
        {
            compatibleVersion = 20;
        }
        else
        {
            compatibleVersion = 14;
        }

    }

    private void loadValues()
    {
        try (InputStream inStream = getClass().getResourceAsStream(MessageFormat.format(PROPERTIES_PATH_PATTERN, compatibleVersion.toString())))
        {
            configProperties.load(inStream);
        }
        catch (IOException ex)
        {
            throw new NextcloudApiException(ex);
        }
    }
}
