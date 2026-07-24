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
package org.aarboard.nextcloud.api.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import org.apache.http.conn.socket.ConnectionSocketFactory;

/**
 * A {@link SardineImpl} whose secure (TLS) socket factory is supplied by the
 * caller, so WebDAV requests honour the same certificate-trust configuration as
 * the OCS client (self-signed / pinned certificates or trust-all).
 * <p>
 * Sardine builds its socket factory from the constructor via
 * {@link #createDefaultSecureSocketFactory()}, before subclass fields are
 * initialised, so the factory is handed over through a thread-local for the
 * duration of construction.
 *
 * @author a.schild
 */
class TrustingSardineImpl extends SardineImpl {

    private static final ThreadLocal<ConnectionSocketFactory> SECURE_SOCKET_FACTORY = new ThreadLocal<>();

    private TrustingSardineImpl(String username, String password) {
        super(username, password);
    }

    private TrustingSardineImpl(String bearerToken) {
        super(bearerToken);
    }

    static Sardine withBasicAuth(String username, String password, ConnectionSocketFactory secureSocketFactory) {
        SECURE_SOCKET_FACTORY.set(secureSocketFactory);
        try {
            return new TrustingSardineImpl(username, password);
        } finally {
            SECURE_SOCKET_FACTORY.remove();
        }
    }

    static Sardine withBearerToken(String bearerToken, ConnectionSocketFactory secureSocketFactory) {
        SECURE_SOCKET_FACTORY.set(secureSocketFactory);
        try {
            return new TrustingSardineImpl(bearerToken);
        } finally {
            SECURE_SOCKET_FACTORY.remove();
        }
    }

    @Override
    protected ConnectionSocketFactory createDefaultSecureSocketFactory() {
        ConnectionSocketFactory factory = SECURE_SOCKET_FACTORY.get();
        return factory != null ? factory : super.createDefaultSecureSocketFactory();
    }
}
