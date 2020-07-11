package org.aarboard.nextcloud.api.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContexts;

public class ConnectorCommon
{
    private final ServerConfig serverConfig;

    public ConnectorCommon(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public <R> CompletableFuture<R> executeGet(String part, List<NameValuePair> queryParams, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part, queryParams);

            HttpRequestBase request = new HttpGet(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public <R> CompletableFuture<R> executePost(String part, List<NameValuePair> postParams, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part, postParams);

            HttpRequestBase request = new HttpPost(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public <R> CompletableFuture<R> executePut(String part1, String part2, List<NameValuePair> putParams, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part1 + "/" + part2, putParams);

            HttpRequestBase request = new HttpPut(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    public <R> CompletableFuture<R> executeDelete(String part1, String part2, List<NameValuePair> deleteParams, ResponseParser<R> parser)
    {
        try {
            URI url= buildUrl(part1 + "/" + part2, deleteParams);

            HttpRequestBase request = new HttpDelete(url.toString());
            return executeRequest(parser, request);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    private URI buildUrl(String subPath, List<NameValuePair> queryParams)
    {
    	if(serverConfig.getSubpathPrefix()!=null) {
    		subPath = serverConfig.getSubpathPrefix()+"/"+subPath;
    	}
    	
        URIBuilder uB= new URIBuilder()
        .setScheme(serverConfig.isUseHTTPS() ? "https" : "http")
        .setHost(serverConfig.getServerName())
        .setPort(serverConfig.getPort())
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

    private <R> CompletableFuture<R> executeRequest(final ResponseParser<R> parser, HttpRequestBase request)
            throws IOException, ClientProtocolException
    {
        // https://docs.nextcloud.com/server/14/developer_manual/core/ocs-share-api.html
        request.addHeader("OCS-APIRequest", "true");
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setProtocolVersion(HttpVersion.HTTP_1_1);

        HttpClientContext context = prepareContext();

        CompletableFuture<R> futureResponse = new CompletableFuture<>();
        HttpAsyncClientSingleton.getInstance(serverConfig).execute(request, context, new ResponseCallback<>(parser, futureResponse));
        return futureResponse;
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

    private final class ResponseCallback<R> implements FutureCallback<HttpResponse>
    {
        private final ResponseParser<R> parser;
        private final CompletableFuture<R> futureResponse;

        private ResponseCallback(ResponseParser<R> parser, CompletableFuture<R> futureResponse)
        {
            this.parser = parser;
            this.futureResponse = futureResponse;
        }

        @Override
        public void completed(HttpResponse response)
        {
            try {
                R result = handleResponse(parser, response);
                futureResponse.complete(result);
            } catch(Exception ex) {
                futureResponse.completeExceptionally(ex);
            }
        }

        private R handleResponse(ResponseParser<R> parser, HttpResponse response) throws IOException
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
                throw new NextcloudApiException("Empty response received");
            }
            throw new NextcloudApiException(String.format("Request failed with %d %s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        }

        @Override
        public void failed(Exception ex)
        {
            futureResponse.completeExceptionally(ex);
        }

        @Override
        public void cancelled()
        {
            futureResponse.cancel(true);
        }
    }

	private static class HttpAsyncClientSingleton {
		private static CloseableHttpAsyncClient HTTPC_CLIENT;
		
		private HttpAsyncClientSingleton(){}
		
		public static CloseableHttpAsyncClient getInstance(ServerConfig serverConfig)
			throws IOException{
			if (HTTPC_CLIENT == null) {
				if (serverConfig.isTrustAllCertificates()) {
					try {
						SSLContext sslContext = SSLContexts.custom()
							.loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();
						HTTPC_CLIENT = HttpAsyncClients.custom()
							.setSSLHostnameVerifier((NoopHostnameVerifier.INSTANCE))
							.setSSLContext(sslContext)
							.build();
					} catch (KeyManagementException | NoSuchAlgorithmException
							| KeyStoreException e) {
						throw new IOException(e);
					} 
					
				} else {
					HTTPC_CLIENT = HttpAsyncClients.createDefault();
				}
				
				HTTPC_CLIENT.start();
			}
			return HTTPC_CLIENT;
		}
		
	}

    public interface ResponseParser<R>
    {
        public R parseResponse(Reader reader);
    }

    /**
     * Close the http client. Required for clean shutdown.
     * @throws IOException error on shutdown
     */
    public static void shutdown() throws IOException{
            if(HttpAsyncClientSingleton.HTTPC_CLIENT != null) {
                    HttpAsyncClientSingleton.getInstance(null).close();
            }		
    }
}
