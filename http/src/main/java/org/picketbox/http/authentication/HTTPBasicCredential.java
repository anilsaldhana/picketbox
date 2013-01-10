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

package org.picketbox.http.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.AbstractUserCredential;
import org.picketbox.core.util.Base64;
import org.picketbox.http.PicketBoxConstants;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class HTTPBasicCredential extends AbstractUserCredential implements HttpServletCredential {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public HTTPBasicCredential(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        String authorizationHeader = request.getHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER);

        if (authorizationHeader != null) {
            int whitespaceIndex = authorizationHeader.indexOf(' ');

            if (whitespaceIndex > 0) {
                String method = authorizationHeader.substring(0, whitespaceIndex);

                if (PicketBoxConstants.HTTP_BASIC.equalsIgnoreCase(method)) {
                    authorizationHeader = authorizationHeader.substring(whitespaceIndex + 1);
                    authorizationHeader = new String(Base64.decode(authorizationHeader));
                    int indexOfColon = authorizationHeader.indexOf(':');

                    if (indexOfColon > 0) {
                        String username = authorizationHeader.substring(0, indexOfColon);
                        String password = authorizationHeader.substring(indexOfColon + 1);

                        setUserName(username);
                        setCredential(new UsernamePasswordCredentials(username, new Password(password.toCharArray())));
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.http.authentication.HttpServletCredential#getRequest()
     */
    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /* (non-Javadoc)
     * @see org.picketbox.http.authentication.HttpServletCredential#getResponse()
     */
    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

}
