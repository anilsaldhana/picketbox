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

package org.picketbox.core.authentication.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.UserCredential;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.model.User;

/**
 * <p>
 * A {@link AuthenticationMechanism} implementation for a UserName/Password based authentication.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class UserNamePasswordAuthenticationMechanism extends AbstractAuthenticationMechanism {

    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> arrayList = new ArrayList<AuthenticationInfo>();

        arrayList.add(new AuthenticationInfo("Username and Password authentication service.",
                "A simple authentication service using a username and password as credentials.",
                UsernamePasswordCredential.class));

        return arrayList;
    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        UsernamePasswordCredential userCredential = (UsernamePasswordCredential) credential;

        // try to retrieve the user from the configured identity store
        User user = getIdentityManager().getUser(userCredential.getUserName());

        Credentials passwordCredential = userCredential.getCredential();

        getIdentityManager().validateCredentials(passwordCredential);

        if (user != null && passwordCredential.getStatus().equals(Status.VALID)) {
            return new PicketBoxPrincipal(userCredential.getUserName());
        }

        return null;
    }
}