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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.aarboard.nextcloud.api.ServerConfig;
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
 * https://docs.nextcloud.com/server/11/developer_manual/core/ocs-share-api.html
 * 
 */
public class FilesharingConnector 
{
    private final static Log LOG = LogFactory.getLog(FilesharingConnector.class);

    private final static int   NC_OK= 100; // Nexclout OK message
    
    private final static String ROOT_PART= "ocs/v1.php/apps/files_sharing/api/v1/";
    private final static String SHARES_PART= ROOT_PART+"shares";

    private final ServerConfig _serverConfig;

    public FilesharingConnector(ServerConfig serverConfig) {
        this._serverConfig = serverConfig;
    }
    
    /**
     * Return all shares of this user
     * 
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<Share> getShares() throws Exception
    {
        return getShares(null, false, false);
    }

    /**
     * Return all shares of this user
     * 
     * @param path      path to file/folder
     * @param reShares  returns not only the shares from the current user but all shares from the given file
     * @param subShares returns all shares within a folder, given that path defines a folder
     * @return 
     * @throws java.lang.Exception 
     */
    public Collection<Share> getShares(String path, boolean reShares, boolean subShares) throws Exception
    {
        List<NameValuePair> queryParams= new LinkedList<>();
        if (path != null)
        {
            queryParams.add(new BasicNameValuePair("path", path));
        }
        if (reShares)
        {
            queryParams.add(new BasicNameValuePair("reshares", "true"));
        }
        if (subShares)
        {
            queryParams.add(new BasicNameValuePair("subfiles", "true"));
        }
        String queryAnswer= executeGet(SHARES_PART, queryParams);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        SharesXMLAnswer xa= new SharesXMLAnswer();
        xa.parseAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            return xa.getShares();
        }
        return null;
    }

    /**
     * Return share info for a single share
     * 
     * @param shareId      id of chare (Not path of share)
     * @return 
     * @throws java.lang.Exception 
     */
    public Share getShareInfo(int shareId) throws Exception
    {
        String queryAnswer= executeGet(SHARES_PART+"/"+Integer.toString(shareId), null);
        if (queryAnswer != null)
        {
            LOG.debug(queryAnswer);
        }
        SharesXMLAnswer xa= new SharesXMLAnswer();
        xa.parseAnswer(queryAnswer);
        if (xa.getStatusCode() == NC_OK)
        {
            if (xa.getShares() == null)
            {
                return null;
            }
            else if (xa.getShares().size() == 1)
            {
                return xa.getShares().get(0);
            }
            else
            {
                LOG.warn("More than one share found, not possible <"+shareId+">");
                return null;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param path                  path to the file/folder which should be shared
     * @param shareType             0 = user; 1 = group; 3 = public link; 6 = federated cloud share
     * @param shareWithUserOrGroupId user / group id with which the file should be shared
     * @param publicUpload          allow public upload to a public shared folder (true/false)
     * @param password              password to protect public link Share with
     * @param permissions           1 = read; 2 = update; 4 = create; 8 = delete; 16 = share; 31 = all (default: 31, for public shares: 1)
     * @return new Share ID if success
     * @throws Exception 
     */
    public Share doShare(
            String path,
            ShareType shareType,
            String shareWithUserOrGroupId,
            Boolean publicUpload,
            String password,
            SharePermissions permissions) throws Exception
    {
        List<NameValuePair> postParams= new LinkedList<>();
        postParams.add(new BasicNameValuePair("path", path));
        postParams.add(new BasicNameValuePair("shareType", Integer.toString(shareType.getIntValue())));
        postParams.add(new BasicNameValuePair("shareWith", shareWithUserOrGroupId));
        if (publicUpload != null)
        {
            postParams.add(new BasicNameValuePair("publicUpload", publicUpload ? "true" : "false"));
        }
        if (password != null)
        {
            postParams.add(new BasicNameValuePair("password", password));
        }
        if (permissions != null)
        {
            postParams.add(new BasicNameValuePair("permissions", Integer.toString(permissions.getCurrentPermission())));
        }
        
        String postAnswer= executePost(SHARES_PART, postParams);
        if (postAnswer != null)
        {
            LOG.debug("Create share answer "+postAnswer);
            SingleShareXMLAnswer xa= new SingleShareXMLAnswer();
            xa.parseAnswer(postAnswer);
            if (xa.getStatusCode() == NC_OK)
            {
                return xa.getShare();
            }
            else
            {
                return null;
            }
        }
        else
        {
            LOG.debug("Create share failed for path "+path+" user/group "+shareWithUserOrGroupId);
        }
        return null;
    }
    
//
//    public boolean deleteGroup(String groupId) throws Exception
//    {
//        String postAnswer= executeDelete(GROUPS_PART, groupId);
//        if (postAnswer != null)
//        {
//            LOG.debug(postAnswer);
//        }
//        XMLAnswer xa= new XMLAnswer(postAnswer);
//        return xa.getStatusCode() == NC_OK;
//    }
//
//    
//    public Collection<String> getGroups() throws Exception
//    {
//        return getGroups(null, -1, -1);
//    }
//    
//    /**
//     * Return matching users
//     * 
//     * @param search pass null when you don't wish to filter
//     * @param limit pass -1 for no limit
//     * @param offset pass -1 for no offset
//     * @return 
//     */
//    public Collection<String> getGroups(String search, int limit, int offset) throws Exception
//    {
//        List<NameValuePair> queryParams= new LinkedList<>();
//        if (limit != -1)
//        {
//            queryParams.add(new BasicNameValuePair("limit", Integer.toString(limit)));
//        }
//        if (offset != -1)
//        {
//            queryParams.add(new BasicNameValuePair("offset", Integer.toString(offset)));
//        }
//        if (search != null)
//        {
//            queryParams.add(new BasicNameValuePair("search", search));
//        }
//
//        String queryAnswer= executeGet(GROUPS_PART, queryParams);
//        if (queryAnswer != null)
//        {
//            LOG.debug(queryAnswer);
//        }
//        XMLAnswer xa= new XMLAnswer(queryAnswer);
//        if (xa.getStatusCode() == NC_OK)
//        {
//            List<String> retVal= new LinkedList<>();
//            for (String uName : xa.getElements())
//            {
//                retVal.add(uName);
//            }
//            return retVal;
//        }
//        return null;
//    }

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
                LOG.warn("Post failed "+statusLine.getReasonPhrase()+" "+statusLine.getStatusCode());
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
