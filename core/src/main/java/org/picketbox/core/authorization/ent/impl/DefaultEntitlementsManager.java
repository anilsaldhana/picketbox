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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.picketbox.core.UserContext;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.ent.EntitlementCollection;
import org.picketbox.core.authorization.ent.EntitlementStore;
import org.picketbox.core.authorization.ent.EntitlementsManager;
import org.picketbox.core.config.EntitlementsConfiguration;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.Role;

/**
 * Default implementation of the {@link EntitlementsManager}
 *
 * @author anil saldhana
 * @since Oct 25, 2012
 */
public class DefaultEntitlementsManager implements EntitlementsManager {

    private final EntitlementStore store;

    public DefaultEntitlementsManager(EntitlementsConfiguration configuration) {
        this.store = configuration.getEntitlementStore();
        initStore(configuration);
    }

    private void initStore(EntitlementsConfiguration configuration) {
        Map<Resource, Map<IdentityType, EntitlementCollection>> entitlements = configuration.getEntitlements();

        if (entitlements != null && !entitlements.isEmpty()) {
            Set<Entry<Resource, Map<IdentityType, EntitlementCollection>>> resources = entitlements.entrySet();

            for (Entry<Resource, Map<IdentityType, EntitlementCollection>> resourceEntry : resources) {
                Resource resource = resourceEntry.getKey();

                Map<IdentityType, EntitlementCollection> identityTypes = resourceEntry.getValue();

                if (identityTypes != null) {
                    Set<Entry<IdentityType, EntitlementCollection>> identityTypeEntrySet = identityTypes.entrySet();

                    for (Entry<IdentityType, EntitlementCollection> entry : identityTypeEntrySet) {
                        IdentityType identityType = entry.getKey();
                        EntitlementCollection entitlementsCollection = entry.getValue();

                        this.store.addEntitlements(resource, identityType, entitlementsCollection);
                    }
                }
            }
        }
    }

    /**
     * Return the {@link EntitlementStore}
     *
     * @return
     */
    public EntitlementStore getStore() {
        return this.store;
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