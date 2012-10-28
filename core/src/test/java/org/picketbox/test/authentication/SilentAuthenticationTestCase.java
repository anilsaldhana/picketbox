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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Assert;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.session.DefaultSessionId;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionId;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the silent authentication when using a valid {@link PicketBoxSession} identifier to create and authenticate a
 * {@link UserContext} instance.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SilentAuthenticationTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link PicketBoxSession} identifier. When
     * creating the {@link UserContext} with a valid {@link SessionId}, PicketBox should perform a silent authentication
     * restoring the {@link PicketBoxSession} with the specified identifier.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test
    public void testSuccessfulAuthentication() throws AuthenticationException {
        PicketBoxManager picketBoxManager = createManager();

        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new UsernamePasswordCredential("admin", "admin"));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        // let's check if the user session was properly created
        PicketBoxSession userSession = authenticatedUser.getSession();

        assertNotNull(userSession);
        assertNotNull(userSession.getId());

        // let's create a new UserContext using the previous session identifier
        authenticatingUser = new UserContext(userSession.getId());

        // try to perform a silent authentication using only the session identifier
        UserContext silentAuthenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(silentAuthenticatedUser);
        assertTrue(silentAuthenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        // let's check if the user session was properly created
        PicketBoxSession silentUserSession = silentAuthenticatedUser.getSession();

        assertNotNull(silentUserSession);
        assertNotNull(silentUserSession.getId());

        // check if the session instances are the same, althought the users instance are not the same
        Assert.assertNotSame(authenticatedUser, silentAuthenticatedUser);
        assertEquals(userSession, silentUserSession);
        assertEquals(userSession.getCreationDate(), silentUserSession.getCreationDate());
        assertRoles(silentAuthenticatedUser);
        assertGroups(authenticatedUser);
    }

    /**
     * <p>
     * Tests if the authentication fail when providing a invalid session identifier. In this case a
     * {@link AuthenticationException} is raised asking for the user credentials.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test(expected = AuthenticationException.class)
    public void testUnsuccessfulAuthenticationInvalidSessionId() throws AuthenticationException {
        PicketBoxManager picketBoxManager = createManager();

        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new UsernamePasswordCredential("admin", "admin"));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());

        // let's check if the user session was properly created
        PicketBoxSession userSession = authenticatedUser.getSession();

        assertNotNull(userSession);
        assertNotNull(userSession.getId());

        // let's logout the the user and destroy its session
        picketBoxManager.logout(authenticatedUser);

        // let's create a new UserContext using a invalid session identifier.
        authenticatingUser = new UserContext(userSession.getId());

        // try to perform a silent authentication using only the session identifier
        UserContext silentAuthenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(silentAuthenticatedUser);
        assertFalse(silentAuthenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Tests if the authentication fails when providing a invalid session identifier. In this case a
     * {@link AuthenticationException} is raised asking for the user credentials.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test(expected = AuthenticationException.class)
    public void testUnsuccessfulAuthenticationUnauthenticatedUser() throws AuthenticationException {
        PicketBoxManager picketBoxManager = createManager();

        UserContext authenticatingUser = new UserContext(new DefaultSessionId());

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Creates a {@link PicketBoxManager} with the Session Management support.
     * </p>
     *
     * @return
     */
    private PicketBoxManager createManager() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.sessionManager().inMemorySessionStore();

        return getPicketBoxManager(builder.build());
    }

}