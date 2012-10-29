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

package org.picketbox.test.audit;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.audit.providers.LogAuditProvider;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the auditing capabilities.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class AuditingTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests the default configuration for auditing. By default, PicketBox will use the {@link LogAuditProvider} to log audit
     * events.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testDefaultAuditingConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.audit().logProvider();
        
        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
    }
    
    /**
     * <p>
     * Tests the default configuration for auditing. By default, PicketBox will use the {@link LogAuditProvider} to log audit
     * events.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testCustomAuditingProvider() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        CustomAuditProvider customAuditProvider = new CustomAuditProvider();
        
        builder.audit().provider(customAuditProvider);
        
        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
        
        assertTrue(customAuditProvider.isAudited());
    }

}