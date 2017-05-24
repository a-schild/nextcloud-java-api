package org.aarboard.nextcloud.api.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
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
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ConnectorCommon
{
    private final static Log LOG = LogFactory.getLog(ConnectorCommon.class);

    private final ServerConfig serverConfig;

    public ConnectorCommon(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public <R> R executeGet(String part, List<NameValuePair> queryParams, ResponseParser<R> parser)
    {
        try {
            return tryExecuteGet(part, queryParams, parser);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    private <R> R tryExecuteGet(String part, List<NameValuePair> queryParams, ResponseParser<R> parser) throws IOException
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        final HttpClientContext context = prepareContext();

        URI url= buildUrl(part, queryParams);

        HttpGet httpget = new HttpGet(url.toString());
        httpget.addHeader("OCS-APIRequest", "true");
        httpget.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpget.setProtocolVersion(HttpVersion.HTTP_1_1);

        try(CloseableHttpResponse response = httpclient.execute(httpget, context)) {
            handleResponse(parser, response);
        }
        return null;
    }

    public <R> R executePost(String part, List<NameValuePair> postParams, ResponseParser<R> parser)
    {
        try {
            return tryExecutePost(part, postParams, parser);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    private <R> R tryExecutePost(String part, List<NameValuePair> postParams, ResponseParser<R> parser) throws IOException
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        final HttpClientContext context = prepareContext();

        URI url= buildUrl(part, postParams);

        HttpPost httpPost = new HttpPost(url.toString());
        httpPost.addHeader("OCS-APIRequest", "true");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);

        try(CloseableHttpResponse response = httpclient.execute(httpPost, context)) {
            handleResponse(parser, response);
        }
        return null;
    }

    public <R> R executeDelete(String part1, String part2, ResponseParser<R> parser)
    {
        try {
            return tryExecuteDelete(part1, part2, parser);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    private <R> R tryExecuteDelete(String part1, String part2, ResponseParser<R> parser) throws IOException
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        final HttpClientContext context = prepareContext();

        URI url= buildUrl(part1+"/"+part2, null);

        HttpDelete httpPost = new HttpDelete(url.toString());
        httpPost.addHeader("OCS-APIRequest", "true");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);

        try (CloseableHttpResponse response = httpclient.execute(httpPost, context)) {
            handleResponse(parser, response);
        }
        return null;
    }

    private HttpClientContext prepareContext() {
        HttpHost targetHost = new HttpHost(serverConfig.getServerName(), serverConfig.getPort(), serverConfig.isUseHTTPS() ? "https" : "http");
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
         = new UsernamePasswordCredentials(serverConfig.getUserName(), serverConfig.getPassword());
        credsProvider.setCredentials(AuthScope.ANY, credentials);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        return context;
    }

    private URI buildUrl(String subPath, List<NameValuePair> queryParams)
    {
        URIBuilder uB= new URIBuilder()
        .setScheme(serverConfig.isUseHTTPS() ? "https" : "http")
        .setHost(serverConfig.getServerName())
        .setUserInfo(serverConfig.getUserName(), serverConfig.getPassword())
        .setPath(subPath);
        if (queryParams != null)
        {
            uB.addParameters(queryParams);
        }
        try {
            return uB.build();
        } catch (URISyntaxException e) {
            throw new NextcloudApiException(e);
        }
    }

    private <R> R handleResponse(ResponseParser<R> parser, CloseableHttpResponse response) throws IOException
    {
        StatusLine statusLine= response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK)
        {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                Reader reader = new InputStreamReader(entity.getContent(), charset);
                return parser.parseResponse(reader);
            }
            else
            {
                LOG.warn("Request failed "+statusLine.getReasonPhrase()+" "+statusLine.getStatusCode());
            }
        }
        return null;
    }

    public interface ResponseParser<R> {
        public R parseResponse(Reader reader);
    }
}
