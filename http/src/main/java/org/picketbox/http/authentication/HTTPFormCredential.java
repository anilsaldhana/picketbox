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
import org.picketbox.http.PicketBoxConstants;
import org.picketlink.idm.credential.PlainTextPassword;
import org.picketlink.idm.credential.UsernamePasswordCredentials;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class HTTPFormCredential extends AbstractUserCredential implements HttpServletCredential {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public HTTPFormCredential(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        String userName = this.request.getParameter(PicketBoxConstants.HTTP_FORM_J_USERNAME);

        if (userName != null) {
            setUserName(userName);
            setCredential(new UsernamePasswordCredentials(userName, new PlainTextPassword(request.getParameter(PicketBoxConstants.HTTP_FORM_J_PASSWORD).toCharArray())));
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
