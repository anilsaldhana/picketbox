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

import java.io.IOException;
import java.io.StringReader;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.CertificateCredential;
import org.picketbox.core.config.AuthenticationConfiguration;
import org.picketbox.core.config.ClientCertConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.credential.internal.X509CertificateCredentials;

/**
 * <p>
 * A {@link AuthenticationMechanism} implementation for a X.509 Certificate based authentication.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CertificateAuthenticationMechanism extends AbstractAuthenticationMechanism {

    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> arrayList = new ArrayList<AuthenticationInfo>();

        arrayList.add(new AuthenticationInfo("Certificate authentication service.",
                "A authentication service using certificates.", CertificateCredential.class));

        return arrayList;

    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        CertificateCredential certCredential = (CertificateCredential) credential;
        X509CertificateCredentials x509Credential = (X509CertificateCredentials) certCredential.getCredential();
        X509Certificate clientCertificate = x509Credential.getCertificate().getValue();

        String username = getUserName(clientCertificate);

        Principal principal = null;

        if (isUseCertificateValidation()) {
            getIdentityManager().validateCredentials(x509Credential);

            if (x509Credential.getStatus().equals(Status.VALID)) {
                principal = new PicketBoxPrincipal(username);
            }
        } else if (getIdentityManager().getUser(username) != null) {
            principal = new PicketBoxPrincipal(username);
        }

        if (principal == null) {
            invalidCredentials(result);
        }

        return principal;
    }

    private String getUserName(X509Certificate clientCertificate) {
        String username = getCertificatePrincipal(clientCertificate).getName();

        if (isUseCNAsPrincipal()) {
            Properties prop = new Properties();
            try {
                prop.load(new StringReader(username.replaceAll(",", "\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            username = prop.getProperty("CN");
        }
        return username;
    }

    private Principal getCertificatePrincipal(X509Certificate cert) {
        Principal certprincipal = cert.getSubjectDN();

        if (certprincipal == null) {
            certprincipal = cert.getIssuerDN();
        }
        return certprincipal;
    }

    public boolean isUseCertificateValidation() {
        ClientCertConfiguration clientCertConfig = getClientCertAuthenticationConfig();

        if (clientCertConfig != null) {
            return clientCertConfig.isUseCertificateValidation();
        }

        return false;
    }

    private ClientCertConfiguration getClientCertAuthenticationConfig() {
        AuthenticationConfiguration authenticationConfig = getPicketBoxManager().getConfiguration().getAuthentication();

        if (authenticationConfig != null) {
            return authenticationConfig.getCertConfiguration();
        }

        return null;
    }

    public boolean isUseCNAsPrincipal() {
        ClientCertConfiguration clientCertConfig = getClientCertAuthenticationConfig();

        if (clientCertConfig != null) {
            return clientCertConfig.isUseCNAsPrincipal();
        }

        return false;
    }

}
