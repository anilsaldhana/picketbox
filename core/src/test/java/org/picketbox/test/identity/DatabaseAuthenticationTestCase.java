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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.identity.impl.JPAIdentityStoreContext;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the authenticaiton using a JPA-based Identity Store.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DatabaseAuthenticationTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    private EntityManagerFactory entityManagerFactory;
    private PicketBoxManager picketBoxManager;

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link UsernamePasswordCredential}.
     * </p>
     *
     * @throws AuthenticationException
     */
    @Test
    public void testUserNamePasswordCredential() throws AuthenticationException {
        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new UsernamePasswordCredential("admin", "admin"));

        // let's authenticate the user
        UserContext authenticatedUser = this.picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        this.picketBoxManager.logout(authenticatedUser);

        authenticatingUser.setCredential(new UsernamePasswordCredential("admin", "bad_passwd"));

        // let's authenticate the user
        authenticatedUser = this.picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    @Before
    public void onSetup() throws Exception {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("picketbox-testing-pu");

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        JPAIdentityStoreContext.set(entityManager);

        ConfigurationBuilder builder = new ConfigurationBuilder();

        // configure the JPA identity store
        /*
         * builder.identityManager().jpaStore().template(new JPATemplate() {
         *
         * @Override public EntityManager getEntityManager() { return JPAIdentityStoreContext.get(); } });
         */

        builder.identityManager().jpaStore().setEntityManager(entityManager);

        this.picketBoxManager = createManager(builder);

        entityManager.flush();
    }

    @After
    public void onFinish() throws Exception {
        EntityManager entityManager = JPAIdentityStoreContext.get();

        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();

        JPAIdentityStoreContext.clear();
        this.entityManagerFactory.close();
    }

}