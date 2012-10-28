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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.ent.EntitlementCollection;
import org.picketbox.core.authorization.ent.EntitlementStore;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

/**
 * An implementation of {@link EntitlementStore} that resides in the memory
 *
 * @author anil saldhana
 * @since Oct 25, 2012
 */
public class InMemoryEntitlementStore implements EntitlementStore {
    private Map<Resource, Holder> map = new ConcurrentHashMap<Resource, Holder>();

    @Override
    public boolean addUserEntitlements(Resource resource, User user, EntitlementCollection collection) {
        Holder holder = this.map.get(resource);
        if (holder == null) {
            holder = new Holder();
            this.map.put(resource, holder);
        }
        holder.userMap.put(user, collection);
        return true;
    }

    @Override
    public boolean addRoleEntitlements(Resource resource, Role role, EntitlementCollection collection) {
        Holder holder = this.map.get(resource);
        if (holder == null) {
            holder = new Holder();
            this.map.put(resource, holder);
        }
        holder.roleMap.put(role, collection);
        return true;
    }

    @Override
    public boolean addGroupEntitlements(Resource resource, Group group, EntitlementCollection collection) {
        Holder holder = this.map.get(resource);
        if (holder == null) {
            holder = new Holder();
            this.map.put(resource, holder);
        }
        holder.groupMap.put(group, collection);
        return true;
    }

    @Override
    public EntitlementCollection entitlements(Resource resource, User user) {
        EntitlementCollection coll = null;
        Holder holder = this.map.get(resource);
        if (holder != null) {
            coll = holder.userMap.get(user);
        }
        if (coll == null) {
            coll = EntitlementCollection.EMPTY_COLLECTION;
        }
        return coll;
    }

    @Override
    public EntitlementCollection entitlements(Resource resource, Role role) {
        EntitlementCollection coll = null;
        Holder holder = this.map.get(resource);
        if (holder != null) {
            coll = holder.roleMap.get(role);
        }
        if (coll == null) {
            coll = EntitlementCollection.EMPTY_COLLECTION;
        }
        return coll;
    }

    @Override
    public EntitlementCollection entitlements(Resource resource, Group group) {
        EntitlementCollection coll = null;
        Holder holder = this.map.get(resource);
        if (holder != null) {
            coll = holder.groupMap.get(group);
        }
        if (coll == null) {
            coll = EntitlementCollection.EMPTY_COLLECTION;
        }
        return coll;
    }

    private static class Holder implements Serializable {
        private static final long serialVersionUID = 1L;
        private Map<User, EntitlementCollection> userMap = new HashMap<User, EntitlementCollection>();
        private Map<Role, EntitlementCollection> roleMap = new HashMap<Role, EntitlementCollection>();
        private Map<Group, EntitlementCollection> groupMap = new HashMap<Group, EntitlementCollection>();
    }
}