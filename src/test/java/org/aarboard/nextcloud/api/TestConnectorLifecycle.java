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
 * Verifies the shared-HTTP-client lifecycle: closing one connector must not
 * shut the shared client down while another connector is still in use
 * (issue #87).
 *
 * @author a.schild
 */
public class TestConnectorLifecycle {

    @Test
    public void testClosingOneConnectorDoesNotBreakAnother() throws Exception {
        TestHelper th = new TestHelper();
        String serverName = th.getServerName();
        if (serverName == null) {
            return;
        }
        NextcloudConnector first = newConnector(th);
        NextcloudConnector second = newConnector(th);
        try {
            assertNotNull(first.getShares());
            assertNotNull(second.getShares());

            // Closing the first connector must not tear down the shared client
            // that the second one still relies on.
            first.close();

            assertNotNull(second.getShares());
        } finally {
            second.close();
        }
    }

    private NextcloudConnector newConnector(TestHelper th) {
        return new NextcloudConnector(th.getServerName(), th.getServerPort() == 443,
                th.getServerPort(), th.getUserName(), th.getPassword());
    }
}
