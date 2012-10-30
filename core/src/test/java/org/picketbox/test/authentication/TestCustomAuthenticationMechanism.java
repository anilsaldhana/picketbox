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

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the configuration of a custom {@link AuthenticationMechanism}.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class TestCustomAuthenticationMechanism extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests if the configuration of a custom {@link AuthenticationMechanism} is working properly. This method configures the
     * custom mechanism, performs a simple authentication and checks if the mechanisms was invoked. 
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testCustomAuthenticationmechanism() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        CustomAuthenticationMechanism customMechanism = new CustomAuthenticationMechanism();

        // register the custom authentication mechanism
        builder.authentication().mechanism(customMechanism);

        // creates and start the manager
        PicketBoxManager manager = createManager(builder);

        // creates the credential supported by the custom mechanism
        CustomCredential credential = new CustomCredential("admin");

        // creates an authenticating context with the credential
        UserContext authenticatingContext = new UserContext(credential);

        // performs the authentication
        UserContext authenticatedContext = manager.authenticate(authenticatingContext);

        assertTrue(authenticatedContext.isAuthenticated());
        
        // check if the custom mechanisms was invoked during the authentication
        assertTrue(customMechanism.isInvoked());
    }

}
