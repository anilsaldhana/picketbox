/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketbox.core.util;

import org.picketbox.core.PicketBoxMessages;
import org.picketlink.idm.credential.DigestCredential;
import org.picketlink.idm.credential.DigestCredentialUtil;

/**
 * Utility class to support HTTP Digest Authentication
 *
 * @author anil saldhana
 * @since July 5, 2012
 */
public class HTTPDigestUtil {
    /**
     * Given the standard client response in HTTP/Digest mechanism, generate a set of string tokens that retains the quotes
     *
     * @param val
     * @return
     */
    public static String[] quoteTokenize(String val) {
        if (val == null)
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("val");

        // Derived from http://issues.apache.org/bugzilla/show_bug.cgi?id=37132
        return val.split(",(?=(?:[^\"]*\"[^\"]*\")+$)");
    }

    /**
     * @param token
     * @return
     */
    public static String userName(String token) {
        if (token.startsWith("Digest")) {
            token = token.substring(7).trim();
        }

        return extract(token, "username=");
    }

    /**
     * Given a digest token, extract the value
     *
     * @param token
     * @param key
     * @return
     */
    public static String extract(String token, String key) {
        String result = null;
        if (token.startsWith(key)) {

            int eq = token.indexOf("=");
            result = token.substring(eq + 1);
            if (result.startsWith("\"")) {
                result = result.substring(1);
            }
            if (result.endsWith("\"")) {
                int len = result.length();
                result = result.substring(0, len - 1);
            }
        }
        return result;
    }

    /**
     * Construct a {@link DigestHolder} from the tokens
     *
     * @param tokens
     * @return
     */
    public static DigestCredential digest(String[] tokens) {
        String username = null, realm = null, nonce = null, uri = null, qop = null, nc = null, cnonce = null, clientResponse = null, opaque = null, domain = null, stale = "false";

        int len = tokens.length;

        for (int i = 0; i < len; i++) {
            String token = tokens[i].trim();

            if (token.startsWith("Digest") || token.startsWith("username=")) {
                username = HTTPDigestUtil.userName(token);
            } else if (token.startsWith("realm")) {
                realm = HTTPDigestUtil.extract(token, "realm=");
            } else if (token.startsWith("nonce")) {
                nonce = HTTPDigestUtil.extract(token, "nonce=");
            } else if (token.startsWith("uri")) {
                uri = HTTPDigestUtil.extract(token, "uri=");
            } else if (token.startsWith("qop")) {
                qop = HTTPDigestUtil.extract(token, "qop=");
            } else if (token.startsWith("nc")) {
                nc = HTTPDigestUtil.extract(token, "nc=");
            } else if (token.startsWith("cnonce")) {
                cnonce = HTTPDigestUtil.extract(token, "cnonce=");
            } else if (token.startsWith("response")) {
                clientResponse = HTTPDigestUtil.extract(token, "response=");
            } else if (token.startsWith("opaque")) {
                opaque = HTTPDigestUtil.extract(token, "opaque=");
            } else if (token.startsWith("domain")) {
                domain = HTTPDigestUtil.extract(token, "domain=");
            } else if (token.startsWith("stale")) {
                stale = HTTPDigestUtil.extract(token, "stale=");
            }
        }
        // Construct a digest holder
        DigestCredential digestHolder = new DigestCredential();
        digestHolder.setUsername(username).setRealm(realm).setNonce(nonce).setUri(uri).setQop(qop).setNc(nc).setCnonce(cnonce)
                .setClientResponse(clientResponse).setOpaque(opaque);

        digestHolder.setStale(stale).setDomain(domain);

        return digestHolder;
    }

    public static String clientResponseValue(DigestCredential digest, char[] password) {
        return DigestCredentialUtil.clientResponseValue(digest, password);
    }

}