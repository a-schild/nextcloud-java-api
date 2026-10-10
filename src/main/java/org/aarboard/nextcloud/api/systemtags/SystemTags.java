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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.webdav.AWebdavHandler;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Access to Nextcloud <a href="https://docs.nextcloud.com/server/latest/developer_manual/client_apis/WebDAV/tags.html">system
 * tags</a> via the WebDAV {@code systemtags} / {@code systemtags-relations}
 * endpoints: list, create and delete tags, and assign/remove tags on a file
 * (identified by its numeric file id, obtainable from
 * {@code getProperties(path, true).getFileId()}).
 *
 * @author a.schild
 */
public class SystemTags extends AWebdavHandler {

    private static final String SYSTEMTAGS = "remote.php/dav/systemtags/";
    private static final String SYSTEMTAGS_RELATIONS = "remote.php/dav/systemtags-relations/files/";
    private static final String NS_OC = "http://owncloud.org/ns";

    private static final QName PROP_ID = new QName(NS_OC, "id", "oc");
    private static final QName PROP_DISPLAY_NAME = new QName(NS_OC, "display-name", "oc");
    private static final QName PROP_USER_VISIBLE = new QName(NS_OC, "user-visible", "oc");
    private static final QName PROP_USER_ASSIGNABLE = new QName(NS_OC, "user-assignable", "oc");
    private static final QName PROP_CAN_ASSIGN = new QName(NS_OC, "can-assign", "oc");

    public SystemTags(ServerConfig serverConfig) {
        super(serverConfig);
    }

    /**
     * @return all system tags on the server
     */
    public List<Tag> getTags() {
        return propfindTags(buildDavUrl(SYSTEMTAGS));
    }

    /**
     * @param fileId numeric file id (see {@code getProperties(path, true).getFileId()})
     * @return the tags assigned to the given file
     */
    public List<Tag> getTagsForFile(long fileId) {
        return propfindTags(buildDavUrl(SYSTEMTAGS_RELATIONS + fileId));
    }

    /**
     * Creates a new system tag.
     *
     * @param name           display name of the tag
     * @param userVisible    whether the tag is visible to users
     * @param userAssignable whether users can assign the tag
     * @return the id of the created tag
     */
    public int createTag(String name, boolean userVisible, boolean userAssignable) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("userVisible", userVisible);
        body.put("userAssignable", userAssignable);

        HttpPost post = new HttpPost(buildDavUrl(SYSTEMTAGS));
        post.setHeader("Authorization", authorizationHeader());
        try {
            post.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body),
                    ContentType.APPLICATION_JSON));
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }

        try (CloseableHttpClient client = buildSyncClient();
                CloseableHttpResponse response = client.execute(post)) {
            int status = response.getStatusLine().getStatusCode();
            if (status != 201) {
                throw new NextcloudApiException("Creating system tag failed with status " + status);
            }
            Header location = response.getFirstHeader("Content-Location");
            if (location == null) {
                location = response.getFirstHeader("Location");
            }
            if (location == null) {
                throw new NextcloudApiException("Creating system tag returned no location header");
            }
            String href = location.getValue().replaceAll("/$", "");
            return Integer.parseInt(href.substring(href.lastIndexOf('/') + 1));
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Deletes a system tag.
     *
     * @param tagId id of the tag
     */
    public void deleteTag(int tagId) {
        davDelete(buildDavUrl(SYSTEMTAGS + tagId));
    }

    /**
     * Assigns a system tag to a file.
     *
     * @param fileId numeric file id
     * @param tagId  id of the tag
     */
    public void assignTag(long fileId, int tagId) {
        Sardine sardine = buildAuthSardine();
        try {
            sardine.put(buildDavUrl(SYSTEMTAGS_RELATIONS + fileId + "/" + tagId), new byte[0]);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            shutdownSardine(sardine);
        }
    }

    /**
     * Removes a system tag from a file.
     *
     * @param fileId numeric file id
     * @param tagId  id of the tag
     */
    public void removeTag(long fileId, int tagId) {
        davDelete(buildDavUrl(SYSTEMTAGS_RELATIONS + fileId + "/" + tagId));
    }

    private void davDelete(String url) {
        Sardine sardine = buildAuthSardine();
        try {
            sardine.delete(url);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            shutdownSardine(sardine);
        }
    }

    private List<Tag> propfindTags(String url) {
        Sardine sardine = buildAuthSardine();
        try {
            Set<QName> props = new HashSet<>();
            props.add(PROP_ID);
            props.add(PROP_DISPLAY_NAME);
            props.add(PROP_USER_VISIBLE);
            props.add(PROP_USER_ASSIGNABLE);
            props.add(PROP_CAN_ASSIGN);

            List<Tag> tags = new ArrayList<>();
            for (DavResource resource : sardine.propfind(url, 1, props)) {
                Map<QName, String> customProps = resource.getCustomPropsNS();
                String id = customProps.get(PROP_ID);
                if (id == null || id.isEmpty()) {
                    // the collection itself has no id
                    continue;
                }
                Tag tag = new Tag();
                tag.setId(Integer.parseInt(id));
                tag.setName(customProps.get(PROP_DISPLAY_NAME));
                tag.setUserVisible(toBoolean(customProps.get(PROP_USER_VISIBLE)));
                tag.setUserAssignable(toBoolean(customProps.get(PROP_USER_ASSIGNABLE)));
                tag.setCanAssign(toBoolean(customProps.get(PROP_CAN_ASSIGN)));
                tags.add(tag);
            }
            return tags;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            shutdownSardine(sardine);
        }
    }

    private static boolean toBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

}
