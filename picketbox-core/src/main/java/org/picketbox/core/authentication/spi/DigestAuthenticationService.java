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

package org.picketbox.core.authentication.spi;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.authentication.api.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.api.AuthenticationInfo;
import org.picketbox.core.authentication.api.AuthenticationResult;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DigestAuthenticationService extends AbstractAuthenticationService {

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.api.AuthenticationService#getAuthenticationInfo()
     */
    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> arrayList = new ArrayList<AuthenticationInfo>();

        arrayList.add(new AuthenticationInfo("HTTP Digest authentication service.",
                "A simple authentication service using HTTP Digest.", DigestAuthHandler.class));

        return arrayList;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.picketbox.core.authentication.spi.AbstractAuthenticationService#doAuthenticate(org.picketbox.core.authentication.
     * AuthenticationManager, org.picketbox.core.authentication.api.AuthenticationCallbackHandler,
     * org.picketbox.core.authentication.api.AuthenticationResult)
     */
    @Override
    protected Principal doAuthenticate(AuthenticationManager authenticationManager,
            AuthenticationCallbackHandler callbackHandler, AuthenticationResult result) throws AuthenticationException {
        TokenCallback tokenCallback = new TokenCallback();

        try {
            callbackHandler.handle(new Callback[] { tokenCallback });
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }

        return authenticationManager.authenticate((DigestHolder) tokenCallback.getToken());
    }

}
