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

import java.io.StringReader;

import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.filesharing.SharesXMLAnswer;
import org.junit.Test;

/**
 * Security tests for {@link XMLAnswerParser}: a malicious/MITM'd server
 * response must not be able to trigger XXE, and valid XML must still parse.
 *
 * @author a.schild
 */
public class XMLAnswerParserSecurityTest {

    /**
     * A response declaring a DOCTYPE / external entity must be rejected (DTDs
     * are disabled), so the entity is never resolved (no file read / SSRF).
     */
    @Test(expected = NextcloudApiException.class)
    public void testExternalEntityIsNotResolved() {
        String malicious = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/hostname\">]>"
                + "<ocs><data><element><path>&xxe;</path></element></data></ocs>";
        XMLAnswerParser.getInstance(SharesXMLAnswer.class)
                .parseResponse(new StringReader(malicious));
    }

    /**
     * Regression guard: hardening the parser must not break normal parsing.
     */
    @Test
    public void testValidXmlStillParses() {
        String valid = "<?xml version=\"1.0\"?>"
                + "<ocs><meta><status>ok</status><statuscode>100</statuscode></meta>"
                + "<data><element><id>1</id><path>/test</path></element></data></ocs>";
        SharesXMLAnswer answer = XMLAnswerParser.getInstance(SharesXMLAnswer.class)
                .parseResponse(new StringReader(valid));
        assertNotNull(answer.getShares());
        assertEquals(1, answer.getShares().size());
        assertEquals("/test", answer.getShares().get(0).getPath());
    }
}
