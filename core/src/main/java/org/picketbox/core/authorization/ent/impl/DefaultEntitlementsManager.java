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
package org.picketbox.core.authorization.ent.impl;

import java.util.Collection;

import org.picketbox.core.UserContext;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.ent.EntitlementCollection;
import org.picketbox.core.authorization.ent.EntitlementStore;
import org.picketbox.core.authorization.ent.EntitlementsManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;

/**
 * Default implementation of the {@link EntitlementsManager}
 *
 * @author anil saldhana
 * @since Oct 25, 2012
 */
public class DefaultEntitlementsManager implements EntitlementsManager {
    protected EntitlementStore store = new InMemoryEntitlementStore();

    /**
     * Return the {@link EntitlementStore}
     *
     * @return
     */
    public EntitlementStore store() {
        return this.store;
    }

    /**
     * Set the {@link EntitlementStore}
     *
     * @param theStore
     */
    public void setStore(EntitlementStore theStore) {
        this.store = theStore;
    }

    @Override
    public EntitlementCollection entitlements(Resource resource, UserContext userContext) {
        EntitlementCollection collection = new EntitlementCollection("ALL");
        collection.add(this.store.entitlements(resource, userContext.getUser()));
        Collection<Role> roles = userContext.getRoles();
        if (roles != null) {
            for (Role role : roles) {
                collection.add(this.store.entitlements(resource, role));
            }
        }
        Collection<Group> groups = userContext.getGroups();
        if (groups != null) {
            for (Group group : groups) {
                collection.add(this.store.entitlements(resource, group));
            }
        }
        return collection;
    }
}