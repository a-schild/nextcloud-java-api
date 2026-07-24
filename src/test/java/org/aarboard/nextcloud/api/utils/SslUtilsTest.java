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
package org.aarboard.nextcloud.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.aarboard.nextcloud.api.AuthenticationConfig;
import org.aarboard.nextcloud.api.ServerConfig;
import org.junit.Test;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * Verifies {@link SslUtils} both at unit level and against a real TLS handshake
 * with a self-signed server (no Docker required).
 *
 * @author a.schild
 */
public class SslUtilsTest {

    private ServerConfig config() {
        return new ServerConfig("localhost", true, 443, new AuthenticationConfig("u", "p"));
    }

    @Test
    public void testNoCustomTrustReturnsNull() {
        assertNull(SslUtils.buildSslContext(config()));
    }

    @Test
    public void testTrustAllReturnsContext() {
        ServerConfig sc = config();
        sc.setTrustAllCertificates(true);
        assertNotNull(SslUtils.buildSslContext(sc));
        // trust-all disables hostname verification; pinned/default do not
        org.junit.Assert.assertTrue(SslUtils.isHostnameVerificationDisabled(sc));
    }

    @Test
    public void testPinnedCertificateAcceptedOverTls() throws Exception {
        HttpsServer server = startHttpsServer();
        try {
            int port = server.getAddress().getPort();

            // Client that pins the server's self-signed certificate
            ServerConfig pinned = config();
            try (InputStream pem = getClass().getResourceAsStream("/test-server-cert.pem")) {
                pinned.addTrustedCertificates(pem);
            }
            SSLContext clientContext = SslUtils.buildSslContext(pinned);
            assertNotNull(clientContext);

            int status = get("https://localhost:" + port + "/", clientContext);
            assertEquals(200, status);
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void testUntrustedCertificateRejectedOverTls() throws Exception {
        HttpsServer server = startHttpsServer();
        try {
            int port = server.getAddress().getPort();
            // Default trust store (SslUtils returns null) must reject the
            // self-signed certificate.
            assertNull(SslUtils.buildSslContext(config()));
            try {
                get("https://localhost:" + port + "/", SSLContext.getDefault());
                fail("Self-signed certificate should not be trusted by default");
            } catch (SSLException expected) {
                // expected
            }
        } finally {
            server.stop(0);
        }
    }

    private int get(String url, SSLContext sslContext) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection.getResponseCode();
    }

    private HttpsServer startHttpsServer() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream ks = getClass().getResourceAsStream("/test-server-keystore.p12")) {
            keyStore.load(ks, "changeit".toCharArray());
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "changeit".toCharArray());
        SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), null, null);

        HttpsServer server = HttpsServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        server.createContext("/", exchange -> {
            byte[] body = "ok".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        return server;
    }
}
