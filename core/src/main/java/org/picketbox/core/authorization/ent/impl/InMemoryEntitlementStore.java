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
import org.picketlink.idm.model.IdentityType;

/**
 * An implementation of {@link EntitlementStore} that resides in the memory
 *
 * @author anil saldhana
 * @since Oct 25, 2012
 */
public class InMemoryEntitlementStore implements EntitlementStore {
    private Map<Resource, Holder> map = new ConcurrentHashMap<Resource, Holder>();

    @Override
    public boolean addEntitlements(Resource resource, IdentityType identityType, EntitlementCollection collection) {
        Holder holder = this.map.get(resource);
        if (holder == null) {
            holder = new Holder();
            this.map.put(resource, holder);
        }
        holder.identityTypeMap.put(identityType, collection);
        return true;
    }

    @Override
    public EntitlementCollection entitlements(Resource resource, IdentityType identityType) {
        EntitlementCollection coll = null;
        Holder holder = this.map.get(resource);
        if (holder != null) {
            coll = holder.identityTypeMap.get(identityType);
        }
        if (coll == null) {
            coll = EntitlementCollection.EMPTY_COLLECTION;
        }
        return coll;
    }

    private static class Holder implements Serializable {
        private static final long serialVersionUID = 1L;
        private Map<IdentityType, EntitlementCollection> identityTypeMap = new HashMap<IdentityType, EntitlementCollection>();
    }
}