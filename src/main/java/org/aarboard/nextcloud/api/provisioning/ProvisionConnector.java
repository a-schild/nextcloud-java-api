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
package org.aarboard.nextcloud.api.provisioning;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.provisioning.xml.XMLAnswer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author a.schild
 * 
 * https://docs.nextcloud.com/server/11.0/admin_manual/configuration_user/user_provisioning_api.html
 * 
 */
public class ProvisionConnector 
{
    private final static Log LOG = LogFactory.getLog(ProvisionConnector.class);

    private final static int   NC_OK= 100; // Nexclout OK message
    
    private final static String ROOT_PART= "ocs/v1.php/cloud/";
    private final static String USERS_PART= ROOT_PART+"users";
    private final static String GROUPS_PART= ROOT_PART+"groups";

    private final ServerConfig _serverConfig;

    public ProvisionConnector(ServerConfig serverConfig) {
        this._serverConfig = serverConfig;
    }
    
    /**
     * Return all users of this instance
     * 
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<String> getUsers() throws Exception
    {
        return getUsers(null, -1, -1);
    }

    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<String> getUsers(
            String search, int limit, int offset) throws Exception
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (limit != -1)
        {
            queryParams.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }
        if (offset != -1)
        {
            queryParams.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        }
        if (search != null)
        {
            queryParams.add(new BasicNameValuePair("search", search));
        }
        String queryAnswer= executeGet(USERS_PART, queryParams);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        XMLAnswer xa= new XMLAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            List<String> retVal= new LinkedList<>();
            for (String uName : xa.getElements())
            {
                retVal.add(uName);
            }
            return retVal;
        }
        return null;
    }
    
    public boolean createGroup(String groupId) throws Exception
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("groupid", groupId));
        String postAnswer= executePost(GROUPS_PART, postParams);
        if (postAnswer != null)
        {
            LOG.debug("Create group answer");
        }
        XMLAnswer xa= new XMLAnswer(postAnswer);
        return xa.getStatusCode() == NC_OK;
    }

    public boolean deleteGroup(String groupId) throws Exception
    {
        String postAnswer= executeDelete(GROUPS_PART, groupId);
        if (postAnswer != null)
        {
            LOG.debug(postAnswer);
        }
        XMLAnswer xa= new XMLAnswer(postAnswer);
        return xa.getStatusCode() == NC_OK;
    }

    
    public Collection<String> getGroups() throws Exception
    {
        return getGroups(null, -1, -1);
    }
    
    /**
     * Return matching users
     * 
     * @param search pass null when you don't wish to filter
     * @param limit pass -1 for no limit
     * @param offset pass -1 for no offset
     * @return 
     */
    public Collection<String> getGroups(String search, int limit, int offset) throws Exception
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (limit != -1)
        {
            queryParams.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }
        if (offset != -1)
        {
            queryParams.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        }
        if (search != null)
        {
            queryParams.add(new BasicNameValuePair("search", search));
        }

        String queryAnswer= executeGet(GROUPS_PART, queryParams);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        XMLAnswer xa= new XMLAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            List<String> retVal= new LinkedList<>();
            for (String uName : xa.getElements())
            {
                retVal.add(uName);
            }
            return retVal;
        }
        return null;
    }

    protected String executeGet(String part, List<NameValuePair> queryParams) throws Exception
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpHost targetHost = new HttpHost(_serverConfig.getServerName(), _serverConfig.getPort(), _serverConfig.isUseHTTPS() ? "https" : "http");
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
         = new UsernamePasswordCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        credsProvider.setCredentials(AuthScope.ANY, credentials);

        // Add AuthCache to the execution context
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        URI url= buildUrl(part, queryParams);
        
        HttpGet httpget = new HttpGet(url.toString());
        httpget.addHeader("OCS-APIRequest", "true");
        httpget.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpget.setProtocolVersion(HttpVersion.HTTP_1_1);

        CloseableHttpResponse response = httpclient.execute(httpget, context);
        try {
            StatusLine statusLine= response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    //long len = entity.getContentLength();
                    return EntityUtils.toString(entity);
                }                
            }
            else
            {
                return null;
            }
        } finally {
            response.close();
        }
        return null;
    }

    protected String executePost(String part, List<NameValuePair> postParams) throws Exception
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpHost targetHost = new HttpHost(_serverConfig.getServerName(), _serverConfig.getPort(), _serverConfig.isUseHTTPS()  ? "https" : "http");
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
         = new UsernamePasswordCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        credsProvider.setCredentials(AuthScope.ANY, credentials);

        // Add AuthCache to the execution context
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        URI url= buildUrl(part, postParams);
        
        HttpPost httpPost = new HttpPost(url.toString());
        httpPost.addHeader("OCS-APIRequest", "true");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);

        CloseableHttpResponse response = httpclient.execute(httpPost, context);
        try {
            StatusLine statusLine= response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    //long len = entity.getContentLength();
                    return EntityUtils.toString(entity);
                }                
            }
            else
            {
                return null;
            }
        } finally {
            response.close();
        }
        return null;
    }

    protected String executeDelete(String part1, String part2) throws Exception
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpHost targetHost = new HttpHost(_serverConfig.getServerName(), _serverConfig.getPort(), _serverConfig.isUseHTTPS() ? "https" : "http");
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
         = new UsernamePasswordCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        credsProvider.setCredentials(AuthScope.ANY, credentials);

        // Add AuthCache to the execution context
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        URI url= buildUrl(part1+"/"+part2, null);
        
        HttpDelete httpPost = new HttpDelete(url.toString());
        httpPost.addHeader("OCS-APIRequest", "true");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);

        CloseableHttpResponse response = httpclient.execute(httpPost, context);
        try {
            StatusLine statusLine= response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    //long len = entity.getContentLength();
                    return EntityUtils.toString(entity);
                }                
            }
            else
            {
                return null;
            }
        } finally {
            response.close();
        }
        return null;
    }
    
    protected URI buildUrl(String subPath, List<NameValuePair> queryParams) 
            throws URISyntaxException
    {
        URIBuilder uB= new URIBuilder()
        .setScheme(_serverConfig.isUseHTTPS() ? "https" : "http")
        .setHost(_serverConfig.getServerName())
        .setUserInfo(_serverConfig.getUserName(), _serverConfig.getPassword())
        .setPath(subPath);
        if (queryParams != null)
        {
            uB.addParameters(queryParams);
        }
        return uB.build();
    }
}
