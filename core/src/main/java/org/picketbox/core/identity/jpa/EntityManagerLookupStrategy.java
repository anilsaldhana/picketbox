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

package org.picketbox.core.identity.jpa;

import javax.persistence.EntityManager;

import org.picketbox.core.PicketBoxMessages;

/**
 * <p>
 * This class determines how the {@link EntityManager} should be located when using the JPA-based Identity Store. The default
 * strategy is to get the {@link EntityManager} from the {@link EntityManagerPropagationContext}.
 * </p>
 * <p>
 * Different strategies could be implemented by extending this class and overriding the <code>lookupEntityManager()</code>
 * method. An example might beproviding a strategy implementation to lookup the {@link EntityManager} when using CDI.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class EntityManagerLookupStrategy {

    public EntityManager getEntityManager() {
        EntityManager entityManager = lookupEntityManager();

        if (entityManager == null) {
            throw PicketBoxMessages.MESSAGES.failedToLookupEntityManager(this);
        }

        return entityManager;
    }

    /**
     * <p>
     * Returns a {@link EntityManager} instance. Subclasses should override this method to provide different lookup strategies.
     * </p>
     *
     * @return
     */
    protected EntityManager lookupEntityManager() {
        return EntityManagerPropagationContext.get();
    }

}