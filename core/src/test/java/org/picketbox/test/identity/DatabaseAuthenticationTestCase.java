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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.identity.jpa.EntityManagerPropagationContext;

/**
 * <p>
 * Tests the authenticaiton using a JPA-based Identity Store.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class DatabaseAuthenticationTestCase extends AbstractIdentityManagerTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void onSetup() throws Exception {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("picketbox-testing-pu");

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        EntityManagerPropagationContext.set(entityManager);
        
        super.onSetup();
    }

    @Override
    protected ConfigurationBuilder doGetConfigurationBuilder() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // configure the JPA identity store
        builder.identityManager().jpaStore();
        
        return builder;
    }
    
    @After
    public void onFinish() throws Exception {
        EntityManager entityManager = EntityManagerPropagationContext.get();

        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();

        EntityManagerPropagationContext.clear();
        this.entityManagerFactory.close();
    }

}
