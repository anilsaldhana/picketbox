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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.security.cert.X509Certificate;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.credential.CertificateCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the different ways to authenticate users using a {@link UserCredential} instance..
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CertificateAuthenticationTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link CertificateCredential}. By default the
     * username used to authenticate the user is obtained from the {@link X509Certificate} Subject DN.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test
    public void testCertificateCredentialUsingSubjectDNAsPrincipal() throws AuthenticationException {
        PicketBoxManager picketBoxManager = createManager();

        UserContext authenticatingUser = new UserContext();

        X509Certificate certificate = getTestingCertificate();

        UserCredential credential = new CertificateCredential(certificate);

        authenticatingUser.setCredential(credential);

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        credential = new CertificateCredential(getCertificate("servercert2.txt"));

        authenticatingUser = new UserContext(credential);

        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link CertificateCredential}. In this case we
     * use the {@link X509Certificate} CN as the username.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test
    public void testCertificateCredentialUsingCNAsPrincipal() throws AuthenticationException {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.authentication().clientCert().useCNAsPrincipal();

        PicketBoxManager picketBoxManager = createManager(builder);

        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new CertificateCredential(getTestingCertificate()));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        authenticatingUser = new UserContext(new CertificateCredential(getCertificate("servercert2.txt")));

        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link CertificateCredential}. In this case we
     * try to validate the provided certificate against the identity store.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test
    public void testCertificateCredentialValidatingCertificate() throws AuthenticationException {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.authentication().clientCert().useCertificateValidation();

        PicketBoxManager picketBoxManager = createManager(builder);

        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new CertificateCredential(getTestingCertificate()));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        authenticatingUser = new UserContext(new CertificateCredential(getCertificate("servercert2.txt")));

        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

}