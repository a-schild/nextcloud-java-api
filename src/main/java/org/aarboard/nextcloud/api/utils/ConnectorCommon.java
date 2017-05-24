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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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
            URI url= buildUrl(part, queryParams);

            HttpRequestBase request = new HttpGet(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public <R> R executePost(String part, List<NameValuePair> postParams, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part, postParams);

            HttpRequestBase request = new HttpPost(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public <R> R executeDelete(String part1, String part2, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part1+"/"+part2, null);

            HttpRequestBase request = new HttpDelete(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
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

    private <R> R executeRequest(ResponseParser<R> parser, HttpRequestBase request)
            throws IOException, ClientProtocolException
    {
        request.addHeader("OCS-APIRequest", "true");
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = prepareContext();

        try (CloseableHttpResponse response = httpclient.execute(request, context)) {
            return handleResponse(parser, response);
        }
    }

    private HttpClientContext prepareContext()
    {
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

    public interface ResponseParser<R>
    {
        public R parseResponse(Reader reader);
    }
}
