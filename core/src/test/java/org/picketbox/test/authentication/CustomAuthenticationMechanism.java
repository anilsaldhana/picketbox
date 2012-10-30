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

package org.picketbox.test.authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CustomAuthenticationMechanism extends AbstractAuthenticationMechanism {

    private boolean invoked;
    
    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        ArrayList<AuthenticationInfo> info = new ArrayList<AuthenticationInfo>();
        
        info.add(new AuthenticationInfo("Custom Authentication Mechanism for testing.", "Custom Authentication Mechanism for testing.", CustomCredential.class));
        
        return info;
    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        CustomCredential customCredential = (CustomCredential) credential;
        
        if ("admin".equals(customCredential.getUserName())) {
            invoked = true;
            return new PicketBoxPrincipal(customCredential.getUserName());
        }        
        
        return null;
    }
    
    public boolean isInvoked() {
        return invoked;
    }
}
