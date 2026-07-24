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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Exercises the bearer-token authentication path (the rest of the suite uses
 * basic auth). Uses an app password created by the test container, which
 * Nextcloud accepts as a bearer token.
 *
 * @author a.schild
 */
public class TestBearerAuth {

    @Test
    public void testBearerTokenAuthentication() {
        TestHelper th = new TestHelper();
        String serverName = th.getServerName();
        String token = clean(System.getProperty("nextcloud.api.test.apptoken"));
        if (serverName != null && token != null) {
            // 4-argument constructor uses the token as a bearer token
            try (NextcloudConnector nc = new NextcloudConnector(serverName,
                    th.getServerPort() == 443, th.getServerPort(), token)) {
                // An OCS call that requires authentication; succeeds only if the
                // Authorization: Bearer header is accepted by the server.
                assertNotNull(nc.getShares());
            } catch (Exception e) {
                throw new AssertionError("Bearer token authentication failed", e);
            }
        }
    }

    private static String clean(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().startsWith("${")) {
            return null;
        }
        return value.trim();
    }
}
