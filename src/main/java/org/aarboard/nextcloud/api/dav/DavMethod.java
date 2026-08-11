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
package org.aarboard.nextcloud.api.dav;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * A request with an arbitrary method name and a body, for the DAV verbs Apache
 * HttpClient does not ship (REPORT, MKCALENDAR, extended MKCOL).
 *
 * @author a.schild
 * @since 14.3
 */
class DavMethod extends HttpEntityEnclosingRequestBase {

    private final String method;

    DavMethod(String method, String uri) {
        this.method = method;
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return method;
    }
}
