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

package org.picketbox.http.test;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.picketbox.core.InitializedEvent;
import org.picketbox.core.event.EventObserver;
import org.picketbox.http.authentication.AbstractHTTPAuthentication;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.internal.Digest;
import org.picketlink.idm.credential.internal.Password;
import org.picketlink.idm.credential.internal.X509Cert;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;

/**
 * @author Pedro Silva
 *
 */
public class InitializationHandler {

    @EventObserver
    public void onInitialized(InitializedEvent event) {
        IdentityManager identityManager = event.getPicketBoxManager().getIdentityManager();
        
        SimpleUser jbidTestUser = new SimpleUser("jbid test");

        identityManager.add(jbidTestUser);

        SimpleUser certUser = new SimpleUser("CN=jbid test, OU=JBoss, O=JBoss, C=US");

        identityManager.add(certUser);

        InputStream bis = getClass().getClassLoader().getResourceAsStream("cert/servercert.txt");

        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
            identityManager.updateCredential(jbidTestUser, new X509Cert(cert));
            identityManager.updateCredential(certUser, new X509Cert(cert));
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException("Error updating user certificate.", e);
        }

        SimpleUser adminUser = new SimpleUser("Aladdin");

        identityManager.add(adminUser);

        adminUser.setEmail("Aladdin@picketbox.com");
        adminUser.setFirstName("The");
        adminUser.setLastName("Aladdin");

        String plainTextPassword = "Open Sesame";
        
        Password password = new Password(plainTextPassword.toCharArray());
        
        identityManager.updateCredential(adminUser, password);
        
        Digest digestPassword = new Digest();

        digestPassword.setRealm(AbstractHTTPAuthentication.DEFAULT_REALM);
        digestPassword.setUsername(adminUser.getLoginName());
        digestPassword.setPassword(plainTextPassword);
        
        identityManager.updateCredential(adminUser, digestPassword);

        Digest digest = new Digest();
        
        digest.setUsername(adminUser.getLoginName());
        digest.setRealm("testrealm@host.com");
        digest.setPassword(plainTextPassword);
        
        identityManager.updateCredential(adminUser, digest);
        
        Role roleManager = new SimpleRole("manager");
        
        identityManager.add(roleManager);
        
        Role roleConfidencial = new SimpleRole("confidencial");
        
        identityManager.add(roleConfidencial);
        
        Group groupCoreDeveloper = new SimpleGroup("PicketBox Group");
        
        identityManager.add(groupCoreDeveloper);

        identityManager.grantRole(adminUser, roleManager);
        identityManager.grantRole(adminUser, roleConfidencial);
        
        identityManager.addToGroup(adminUser, groupCoreDeveloper);
    }
    
}
