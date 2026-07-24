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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContexts;

/**
 * Builds the {@link SSLContext} implied by a {@link ServerConfig}'s TLS trust
 * settings, so the OCS HTTP client and the WebDAV (Sardine) client apply the
 * same trust rules.
 *
 * @author a.schild
 */
public final class SslUtils {

    private SslUtils() {
    }

    /**
     * @param serverConfig the server configuration
     * @return an {@link SSLContext} for the configured trust settings, or
     *         {@code null} when the default JVM trust store should be used
     * @throws NextcloudApiException if the trust material cannot be built
     */
    public static SSLContext buildSslContext(ServerConfig serverConfig) {
        try {
            if (serverConfig.isTrustAllCertificates()) {
                return SSLContexts.custom()
                        .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                        .build();
            }
            if (serverConfig.hasTrustedCertificates()) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                int index = 0;
                for (X509Certificate certificate : serverConfig.getTrustedCertificates()) {
                    trustStore.setCertificateEntry("nextcloud-trusted-" + index++, certificate);
                }
                // Only the pinned certificates are trusted.
                return SSLContexts.custom().loadTrustMaterial(trustStore, null).build();
            }
            return null;
        } catch (GeneralSecurityException | IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Hostname verification is only disabled for the (insecure)
     * trust-all-certificates mode; pinned certificates keep it enabled.
     *
     * @param serverConfig the server configuration
     * @return true if hostname verification must be disabled
     */
    public static boolean isHostnameVerificationDisabled(ServerConfig serverConfig) {
        return serverConfig.isTrustAllCertificates();
    }
}
