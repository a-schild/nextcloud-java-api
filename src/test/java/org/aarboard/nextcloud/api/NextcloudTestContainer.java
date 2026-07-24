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

import java.time.Duration;

import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Boots a throw-away Nextcloud server in a Docker container for the integration
 * tests and exposes it through the same {@code nextcloud.api.test.*} system
 * properties that {@link TestHelper} reads.
 * <p>
 * The container is started once per JVM and reused across all test classes;
 * Testcontainers' Ryuk sidecar removes it when the JVM exits.
 * <p>
 * The provisioning is best-effort and non-fatal:
 * <ul>
 *   <li>If the {@code nextcloud.api.test.servername} property is already set
 *       (an external server was provided via {@code settings.xml}), nothing is
 *       started and that server is used as before.</li>
 *   <li>If Docker is not available, nothing is started and the properties stay
 *       unset, so the tests skip silently exactly as they did previously.</li>
 * </ul>
 *
 * @author a.schild
 */
public final class NextcloudTestContainer {

    /**
     * Pinned image for reproducible runs. The apache variant serves on port 80
     * and self-installs on first boot when the admin credentials are supplied.
     */
    private static final String IMAGE = "nextcloud:31-apache";

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "admin-test-pw-123";

    private static boolean attempted = false;
    private static GenericContainer<?> container;

    private NextcloudTestContainer() {
    }

    /**
     * @return true if the value is a real, usable setting (not null, not blank,
     *         and not an unexpanded {@code ${...}} Maven placeholder).
     */
    static boolean isConfigured(String value) {
        return value != null && !value.trim().isEmpty() && !value.trim().startsWith("${");
    }

    /**
     * Ensures a Nextcloud server is available for the tests, starting a
     * container on the first call if needed. Safe to call repeatedly.
     */
    public static synchronized void ensureStarted() {
        if (attempted) {
            return;
        }
        attempted = true;

        // An external server was configured explicitly - use it, don't start Docker.
        // Note: the surefire config injects an (empty / unexpanded "${...}") value
        // for this property even when no settings.xml profile defines it, so a bare
        // null-check is not enough - it must be a real, non-blank host.
        if (isConfigured(System.getProperty("nextcloud.api.test.servername"))) {
            return;
        }

        // No Docker -> leave the properties unset so the tests skip gracefully.
        if (!DockerClientFactory.instance().isDockerAvailable()) {
            return;
        }

        container = new GenericContainer<>(DockerImageName.parse(IMAGE))
                .withExposedPorts(80)
                .withEnv("NEXTCLOUD_ADMIN_USER", ADMIN_USER)
                .withEnv("NEXTCLOUD_ADMIN_PASSWORD", ADMIN_PASSWORD)
                // Selecting a database is what triggers the image's unattended
                // install; without it Nextcloud stays uninstalled. SQLite keeps
                // the container self-contained (no extra DB service needed).
                .withEnv("SQLITE_DATABASE", "nextcloud")
                // Nextcloud strips the port before checking trusted domains, so
                // matching the host (localhost / 127.0.0.1) is enough even though
                // Testcontainers maps port 80 to a random host port.
                .withEnv("NEXTCLOUD_TRUSTED_DOMAINS", "localhost 127.0.0.1")
                .waitingFor(Wait.forHttp("/status.php")
                        .forStatusCode(200)
                        .forResponsePredicate(body -> body.contains("\"installed\":true"))
                        .withStartupTimeout(Duration.ofMinutes(5)));
        container.start();

        System.setProperty("nextcloud.api.test.servername", container.getHost());
        System.setProperty("nextcloud.api.test.serverport", String.valueOf(container.getMappedPort(80)));
        System.setProperty("nextcloud.api.test.username", ADMIN_USER);
        System.setProperty("nextcloud.api.test.password", ADMIN_PASSWORD);
    }
}
