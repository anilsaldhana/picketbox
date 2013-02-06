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

package org.picketbox.test.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.internal.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author Pedro Silva
 *
 */
public abstract class AbstractIdentityManagerTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    private PicketBoxManager picketBoxManager;

    @Before
    public void onSetup() throws Exception {
        this.picketBoxManager = createManager(doGetConfigurationBuilder());
    }

    @After
    public void onFinish() throws Exception {

    }

    @Override
    protected void initializeStore(IdentityManager identityManager) {
        User mary = new SimpleUser("mary");

        identityManager.add(mary);

        identityManager.updateCredential(mary, new Password("mary123"));

        Role moderator = new SimpleRole("moderator");

        identityManager.add(moderator);

        Group picketboxDiscussions = new SimpleGroup("PicketBox Discussions");

        identityManager.add(picketboxDiscussions);

        identityManager.grantRole(mary, moderator);
        identityManager.addToGroup(mary, picketboxDiscussions);

        Role manager = new SimpleRole("manager");

        identityManager.add(manager);

        Group sales = new SimpleGroup("Sales");

        identityManager.add(sales);

        identityManager.grantGroupRole(mary, manager, sales);
    }

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        UserContext authenticatingContext = new UserContext();

        authenticatingContext.setCredential(new UsernamePasswordCredential("mary", "mary123"));

        UserContext authenticatedContext = getPicketBoxManager().authenticate(authenticatingContext);

        assertNotNull(authenticatedContext);
        assertTrue(authenticatedContext.isAuthenticated());
        assertNotNull(authenticatedContext.getUser());
        assertTrue(authenticatedContext.hasRole("moderator"));
        assertTrue(authenticatedContext.hasRole("manager"));
        assertTrue(authenticatedContext.hasGroup("PicketBox Discussions"));
        assertTrue(authenticatedContext.hasGroup("Sales"));
    }

    @Test
    public void testUnsuccessfulAuthentication() throws Exception {
        UserContext authenticatingContext = new UserContext();

        authenticatingContext.setCredential(new UsernamePasswordCredential("mary", "bad_password"));

        UserContext authenticatedContext = getPicketBoxManager().authenticate(authenticatingContext);

        assertNotNull(authenticatedContext);
        assertFalse(authenticatedContext.isAuthenticated());

        authenticatingContext.setCredential(new UsernamePasswordCredential("invalidUser", "password"));

        authenticatedContext = getPicketBoxManager().authenticate(authenticatingContext);

        assertNotNull(authenticatedContext);
        assertFalse(authenticatedContext.isAuthenticated());
    }

    @Test
    public void testUserCreatedEvent() throws Exception {
        IdentityManagementEventHandler handler = new IdentityManagementEventHandler();

        getPicketBoxManager().getEventManager().addHandler(handler);

        SimpleUser user = new SimpleUser("someUser");

        getIdentityManager().add(user);

        assertNotNull(handler.getCreatedUser());
        assertEquals(user, handler.getCreatedUser());
        assertEquals(user.getLoginName(), handler.getCreatedUser().getLoginName());
    }

    @Test
    public void testUserRemovedEvent() throws Exception {
        IdentityManagementEventHandler handler = new IdentityManagementEventHandler();

        getPicketBoxManager().getEventManager().addHandler(handler);

        SimpleUser user = new SimpleUser("someUser");

        getIdentityManager().add(user);
        getIdentityManager().remove(user);

        assertNotNull(handler.getRemovedUser());
        assertEquals(user, handler.getRemovedUser());
        assertEquals(user.getLoginName(), handler.getRemovedUser().getLoginName());
    }

    @Test
    public void testUserUpdatedEvent() throws Exception {
        IdentityManagementEventHandler handler = new IdentityManagementEventHandler();

        getPicketBoxManager().getEventManager().addHandler(handler);

        SimpleUser user = new SimpleUser("someUser");

        getIdentityManager().add(user);

        user.setAttribute(new Attribute<String>("name", "value"));

        getIdentityManager().update(user);

        assertNotNull(handler.getUpdatedUser());
        assertEquals(user, handler.getUpdatedUser());
        assertEquals(user.getLoginName(), handler.getUpdatedUser().getLoginName());
        assertNotNull(handler.getUpdatedUser().getAttribute("name"));
        assertEquals("value", handler.getUpdatedUser().getAttribute("name").getValue());
    }

    public PicketBoxManager getPicketBoxManager() {
        return this.picketBoxManager;
    }

    public IdentityManager getIdentityManager() {
        return getPicketBoxManager().getIdentityManager();
    }

    protected ConfigurationBuilder doGetConfigurationBuilder() {
        return new ConfigurationBuilder();
    }

}
