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

package org.picketbox.core.identity;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.model.Agent;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.Realm;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.Tier;
import org.picketlink.idm.model.User;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.spi.IdentityStoreInvocationContextFactory;
import org.picketlink.idm.spi.StoreFactory;

/**
 * <p>PicketBox default implementation for the PicketLink {@link IdentityManager} interface.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxIdentityManager implements IdentityManager {

    private IdentityManager delegate;

    public PicketBoxIdentityManager(IdentityManager identityManager) {
        this.delegate = identityManager;
    }

    public void bootstrap(IdentityConfiguration configuration, IdentityStoreInvocationContextFactory contextFactory) {
        delegate.bootstrap(configuration, contextFactory);
    }

    public void setIdentityStoreFactory(StoreFactory factory) {
        delegate.setIdentityStoreFactory(factory);
    }

    public void add(IdentityType identityType) {
        delegate.add(identityType);
    }

    public void update(IdentityType identityType) {
        delegate.update(identityType);
    }

    public void remove(IdentityType identityType) {
        delegate.remove(identityType);
    }

    public Agent getAgent(String id) {
        return delegate.getAgent(id);
    }

    public User getUser(String id) {
        return delegate.getUser(id);
    }

    public Group getGroup(String groupId) {
        return delegate.getGroup(groupId);
    }

    public Group getGroup(String groupName, Group parent) {
        return delegate.getGroup(groupName, parent);
    }

    public boolean isMember(IdentityType identityType, Group group) {
        return delegate.isMember(identityType, group);
    }

    public void addToGroup(IdentityType identityType, Group group) {
        delegate.addToGroup(identityType, group);
    }

    public void removeFromGroup(IdentityType identityType, Group group) {
        delegate.removeFromGroup(identityType, group);
    }

    public Role getRole(String name) {
        return delegate.getRole(name);
    }

    public boolean hasGroupRole(IdentityType identityType, Role role, Group group) {
        return delegate.hasGroupRole(identityType, role, group);
    }

    public void grantGroupRole(IdentityType identityType, Role role, Group group) {
        delegate.grantGroupRole(identityType, role, group);
    }

    public void revokeGroupRole(IdentityType identityType, Role role, Group group) {
        delegate.revokeGroupRole(identityType, role, group);
    }

    public boolean hasRole(IdentityType identityType, Role role) {
        return delegate.hasRole(identityType, role);
    }

    public void grantRole(IdentityType identityType, Role role) {
        delegate.grantRole(identityType, role);
    }

    public void revokeRole(IdentityType identityType, Role role) {
        delegate.revokeRole(identityType, role);
    }

    public <T extends IdentityType> IdentityQuery<T> createQuery(Class<T> identityType) {
        return delegate.createQuery(identityType);
    }

    public void validateCredentials(Credentials credentials) {
        delegate.validateCredentials(credentials);
    }

    public void updateCredential(Agent agent, Object value) {
        delegate.updateCredential(agent, value);
    }

    public IdentityType lookupIdentityByKey(String key) {
        return delegate.lookupIdentityByKey(key);
    }

    public void loadAttribute(IdentityType identityType, String attributeName) {
        delegate.loadAttribute(identityType, attributeName);
    }

    public void createRealm(Realm realm) {
        delegate.createRealm(realm);
    }

    public void removeRealm(Realm realm) {
        delegate.removeRealm(realm);
    }

    public Realm getRealm(String name) {
        return delegate.getRealm(name);
    }

    public void createTier(Tier tier) {
        delegate.createTier(tier);
    }

    public void removeTier(Tier tier) {
        delegate.removeTier(tier);
    }

    public Tier getTier(String id) {
        return delegate.getTier(id);
    }

    public IdentityManager forRealm(Realm realm) {
        return delegate.forRealm(realm);
    }

    public IdentityManager forTier(Tier tier) {
        return delegate.forTier(tier);
    }
}
