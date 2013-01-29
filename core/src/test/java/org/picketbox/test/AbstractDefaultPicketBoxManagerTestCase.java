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

package org.picketbox.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.internal.Password;
import org.picketlink.idm.credential.internal.X509Cert;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;

/**
 * <p>
 * Base class for test cases that allows to create a fresh {@link PicketBoxManager} instance using some specific
 * {@link PicketBoxConfiguration}. This class also initializes the identity store with the default user information.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Creates a {@link PicketBoxManager}.
     * </p>
     *
     * @return
     */
    protected PicketBoxManager createManager(ConfigurationBuilder... builder) {
        ConfigurationBuilder configBuilder = null;

        if (builder.length == 0) {
            configBuilder = new ConfigurationBuilder();
        } else {
            configBuilder = builder[0];
        }

        PicketBoxManager picketboxManager = new DefaultPicketBoxManager(configBuilder.build());

        picketboxManager.start();

        initializeStore(picketboxManager.getIdentityManager());

        return picketboxManager;
    }

    /**
     * <p>
     * Initializes the identity manager store with users information.
     * </p>
     *
     * @param identityManager
     */
    protected void initializeStore(IdentityManager identityManager) {
        SimpleUser adminUser = new SimpleUser("admin");

        identityManager.add(adminUser);

        adminUser.setEmail("admin@picketbox.com");
        adminUser.setFirstName("The");
        adminUser.setLastName("Admin");
        
        //Get 30 years
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 100);
 
        identityManager.updateCredential(adminUser, new Password("admin".toCharArray()), new Date(), calendar.getTime());
        identityManager.updateCredential(adminUser, new X509Cert(getTestingCertificate()));
        
        Role roleDeveloper = new SimpleRole("developer");

        identityManager.add(roleDeveloper);

        Role roleAdmin = new SimpleRole("admin");

        identityManager.add(roleAdmin);

        Group groupCoreDeveloper = new SimpleGroup("PicketBox Group");

        identityManager.add(groupCoreDeveloper);

        identityManager.grantRole(adminUser, roleDeveloper);
        identityManager.grantRole(adminUser, roleAdmin);

        identityManager.addToGroup(adminUser, groupCoreDeveloper);

        SimpleUser jbidTestUser = new SimpleUser("jbid test");

        identityManager.add(jbidTestUser);

        identityManager.updateCredential(jbidTestUser, new X509Cert(getTestingCertificate()));

        identityManager.grantRole(jbidTestUser, roleDeveloper);
        identityManager.grantRole(jbidTestUser, roleAdmin);

        identityManager.addToGroup(jbidTestUser, groupCoreDeveloper);

        SimpleUser certUser = new SimpleUser("CN=jbid test, OU=JBoss, O=JBoss, C=US");

        identityManager.add(certUser);

        identityManager.updateCredential(certUser, new X509Cert(getTestingCertificate()));

        identityManager.grantRole(certUser, roleDeveloper);
        identityManager.grantRole(certUser, roleAdmin);

        identityManager.addToGroup(certUser, groupCoreDeveloper);
    }

    protected void assertRoles(UserContext authenticatedUser) {
        assertFalse(authenticatedUser.getRoles().isEmpty());
        assertTrue(authenticatedUser.getRoles().containsAll(
                Arrays.asList(new Role[] { new SimpleRole("developer"), new SimpleRole("admin") })));
    }

    protected void assertGroups(UserContext authenticatedUser) {
        assertFalse(authenticatedUser.getGroups().isEmpty());
        assertEquals(1, authenticatedUser.getGroups().size());
        assertEquals("PicketBox Group", authenticatedUser.getGroups().iterator().next().getName());
    }

    protected X509Certificate getTestingCertificate() {
        return getCertificate("servercert.txt");
    }

    protected X509Certificate getCertificate(String fileName) {
        // Certificate
        InputStream bis = getClass().getClassLoader().getResourceAsStream("cert/" + fileName);
        X509Certificate cert = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(bis);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load testing certificate.", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
        return cert;
    }

}
