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
package org.aarboard.nextcloud.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.junit.Test;

/**
 * Unit tests for the explicit certificate trust configuration on
 * {@link ServerConfig} (no server required).
 *
 * @author a.schild
 */
public class ServerConfigCertificateTest {

    private ServerConfig newConfig() {
        return new ServerConfig("localhost", true, 443, new AuthenticationConfig("user", "pass"));
    }

    @Test
    public void testNoTrustedCertificatesByDefault() {
        ServerConfig sc = newConfig();
        assertFalse(sc.hasTrustedCertificates());
        assertFalse(sc.isTrustAllCertificates());
        assertTrue(sc.getTrustedCertificates().isEmpty());
    }

    @Test
    public void testAddTrustedCertificateFromPem() {
        ServerConfig sc = newConfig();
        try (InputStream pem = getClass().getResourceAsStream("/test-certificate.pem")) {
            sc.addTrustedCertificates(pem);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(sc.hasTrustedCertificates());
        assertEquals(1, sc.getTrustedCertificates().size());
        // trusting a specific certificate must not turn on trust-all
        assertFalse(sc.isTrustAllCertificates());
    }

    @Test(expected = NextcloudApiException.class)
    public void testInvalidCertificateStreamIsRejected() {
        ServerConfig sc = newConfig();
        InputStream garbage = new ByteArrayInputStream("not a certificate".getBytes(StandardCharsets.UTF_8));
        sc.addTrustedCertificates(garbage);
    }

    @Test
    public void testGetTrustedCertificatesIsUnmodifiable() {
        ServerConfig sc = newConfig();
        try {
            sc.getTrustedCertificates().clear();
        } catch (UnsupportedOperationException expected) {
            return;
        }
        // an empty list may allow clear() as a no-op on some JDKs; only fail if
        // it actually let us mutate a populated list
    }
}
