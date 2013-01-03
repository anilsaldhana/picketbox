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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.http.PicketBoxConstants;
import org.picketbox.http.config.HTTPAuthenticationConfiguration;
import org.picketbox.http.config.HTTPFormConfiguration;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.model.User;

/**
 * Perform HTTP Form Authentication
 *
 * @author anil saldhana
 * @since July 9, 2012
 */
public class HTTPFormAuthentication extends AbstractHTTPAuthentication {

    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> info = new ArrayList<AuthenticationInfo>();

        info.add(new AuthenticationInfo("HTTP FORM Authentication Credential", "Authenticates users using the HTTP FORM Authentication scheme.", HTTPFormCredential.class));

        return info;
    }

    @Override
    protected boolean isAuthenticationRequest(HttpServletRequest request) {
        return request.getRequestURI().contains(PicketBoxConstants.HTTP_FORM_J_SECURITY_CHECK);
    }

    @Override
    protected Principal doHTTPAuthentication(HttpServletCredential credential) {
        HTTPFormCredential formCredential = (HTTPFormCredential) credential;

        if (formCredential.getCredential() != null) {
            User user = getIdentityManager().getUser(formCredential.getUserName());

            Credentials passwordCredential = formCredential.getCredential();

            getIdentityManager().validateCredentials(passwordCredential);

            if (user != null && passwordCredential.getStatus().equals(Status.VALID)) {
                return new PicketBoxPrincipal(user.getId());
            }
        }

        return null;
    }

    @Override
    protected void challengeClient(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        forwardLoginPage(request, response);
    }

    @Override
    public String getFormAuthPage() {
        HTTPAuthenticationConfiguration authenticationConfig = getAuthenticationConfig();

        if (authenticationConfig != null) {
            HTTPFormConfiguration formConfiguration = authenticationConfig.getFormConfiguration();

            if (formConfiguration != null && formConfiguration.getFormAuthPage() != null) {
                super.formAuthPage = formConfiguration.getFormAuthPage();
            }
        }

        return super.formAuthPage;
    }

    @Override
    public String getDefaultPage() {
        HTTPAuthenticationConfiguration authenticationConfig = getAuthenticationConfig();

        if (authenticationConfig != null) {
            HTTPFormConfiguration formConfiguration = authenticationConfig.getFormConfiguration();

            if (formConfiguration != null && formConfiguration.getDefaultPage() != null) {
                super.defaultPage = formConfiguration.getDefaultPage();
            }
        }

        return super.defaultPage;
    }

    @Override
    public String getFormErrorPage() {
        HTTPAuthenticationConfiguration authenticationConfig = getAuthenticationConfig();

        if (authenticationConfig != null) {
            HTTPFormConfiguration formConfiguration = authenticationConfig.getFormConfiguration();

            if (formConfiguration != null && formConfiguration.getErrorPage() != null) {
                super.formErrorPage = formConfiguration.getErrorPage();
            }
        }

        return super.formErrorPage;
    }
}