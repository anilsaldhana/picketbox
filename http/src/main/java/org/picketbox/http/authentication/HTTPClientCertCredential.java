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

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.AbstractUserCredential;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketlink.idm.credential.X509CertificateCredential;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class HTTPClientCertCredential extends AbstractUserCredential implements HttpServletCredential {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public HTTPClientCertCredential(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(PicketBoxConstants.HTTP_CERTIFICATE);

        if (certs != null && certs.length > 0) {
            X509Certificate clientCertificate = certs[0];

            setCredential(new X509CertificateCredential(clientCertificate));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.http.authentication.HttpServletCredential#getRequest()
     */
    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.http.authentication.HttpServletCredential#getResponse()
     */
    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

}
