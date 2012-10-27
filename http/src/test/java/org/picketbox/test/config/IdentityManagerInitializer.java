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

package org.picketbox.test.config;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.PasswordCredential;
import org.picketlink.idm.credential.X509CertificateCredential;
import org.picketlink.idm.file.internal.FileBasedIdentityStore;
import org.picketlink.idm.file.internal.FileUser;
import org.picketlink.idm.internal.DefaultIdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IdentityManagerInitializer {

    public static void initializeIdentityStore() {
        FileBasedIdentityStore theStore = new FileBasedIdentityStore();

        theStore.setAlwaysCreateFiles(false);

        IdentityManager identityManager = new DefaultIdentityManager(theStore);

        FileUser jbidTestUser = new FileUser("jbid test");

        identityManager.createUser(jbidTestUser);

        FileUser certUser = new FileUser("CN=jbid test, OU=JBoss, O=JBoss, C=US");

        identityManager.createUser(certUser);

        InputStream bis = Thread.currentThread().getContextClassLoader().getResourceAsStream("cert/servercert.txt");

        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
            identityManager.updateCredential(jbidTestUser, new X509CertificateCredential(cert));
            identityManager.updateCredential(certUser, new X509CertificateCredential(cert));
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException("Error updating user certificate.", e);
        }

        FileUser adminUser = new FileUser("Aladdin");

        identityManager.createUser(adminUser);

        adminUser.setEmail("Aladdin@picketbox.com");
        adminUser.setFirstName("The");
        adminUser.setLastName("Aladdin");

        identityManager.updateCredential(adminUser, new PasswordCredential("Open Sesame"));

        Role roleManager = identityManager.createRole("manager");
        Role roleConfidencial = identityManager.createRole("confidencial");

        Group groupCoreDeveloper = identityManager.createGroup("PicketBox Group");

        identityManager.grantRole(roleManager, adminUser, groupCoreDeveloper);
        identityManager.grantRole(roleConfidencial, adminUser, groupCoreDeveloper);
    }

}
