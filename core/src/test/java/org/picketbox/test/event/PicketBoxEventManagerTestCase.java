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

package org.picketbox.test.event;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.authentication.event.UserAuthenticationEvent;
import org.picketbox.core.authentication.event.UserPreAuthenticationEvent;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.logout.event.UserLoggedOutEvent;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the {@link PicketBoxEventManager}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxEventManagerTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests is the {@link UserAuthenticationEvent} is properly handled when the user is successfully authenticated.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSuccesfulUserAuthenticatedEvent() throws Exception {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        MockUserAuthenticationEventHandler authenticationEventHandler = new MockUserAuthenticationEventHandler();

        configurationBuilder.authentication().eventManager().handler(authenticationEventHandler);

        PicketBoxManager picketBoxManager = getPicketBoxManager(configurationBuilder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
        assertTrue(authenticationEventHandler.isSuccessfulAuthentication());
    }

    /**
     * <p>
     * Tests is the {@link UserAuthenticationEvent} is properly handled when the user authentication fail.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testUnSuccessfulUserAuthenticatedEvent() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        MockUserAuthenticationEventHandler authenticationEventHandler = new MockUserAuthenticationEventHandler();

        builder.authentication().eventManager().handler(authenticationEventHandler);

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "badpasswd"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertFalse(subject.isAuthenticated());
        assertFalse(authenticationEventHandler.isSuccessfulAuthentication());
    }

    /**
     * <p>
     * Tests is the {@link UserLoggedOutEvent} is properly handled when the user is logged out.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testUserLoggedOutEvent() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        MockUserLoggedOutEventHandler logoutEventHandler = new MockUserLoggedOutEventHandler();

        builder.authentication().eventManager().handler(logoutEventHandler);

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());

        picketBoxManager.logout(subject);

        assertTrue(logoutEventHandler.isLoggedOut());
    }
    
    /**
     * <p>
     * Tests is the {@link UserPreAuthenticationEvent} is properly handled when the user is being authenticated.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testUserPreAuthenticationEvent() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        MockUserPreAuthenticationEventHandler preAuthenticationEventHandler = new MockUserPreAuthenticationEventHandler();

        builder.authentication().eventManager().handler(preAuthenticationEventHandler);

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
        assertTrue(preAuthenticationEventHandler.isInvoked());
        assertNotNull(subject.getContextData().get(MockUserPreAuthenticationEventHandler.PRE_AUTH_CONTEXT_DATA));
    }

}