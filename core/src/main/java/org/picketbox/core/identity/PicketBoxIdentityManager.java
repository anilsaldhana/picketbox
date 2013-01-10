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

import java.util.Date;

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

    private static final long serialVersionUID = 8582047228661746675L;
    private IdentityManager delegate;

    public PicketBoxIdentityManager(IdentityManager identityManager) {
        this.delegate = identityManager;
    }

    @Override
    public void bootstrap(IdentityConfiguration configuration, IdentityStoreInvocationContextFactory contextFactory) {
        this.delegate.bootstrap(configuration, contextFactory);
    }

    @Override
    public void setIdentityStoreFactory(StoreFactory factory) {
        this.delegate.setIdentityStoreFactory(factory);
    }

    @Override
    public void add(IdentityType identityType) {
        this.delegate.add(identityType);
    }

    @Override
    public void update(IdentityType identityType) {
        this.delegate.update(identityType);
    }

    @Override
    public void remove(IdentityType identityType) {
        this.delegate.remove(identityType);
    }

    @Override
    public Agent getAgent(String id) {
        return this.delegate.getAgent(id);
    }

    @Override
    public User getUser(String id) {
        return this.delegate.getUser(id);
    }

    @Override
    public Group getGroup(String groupId) {
        return this.delegate.getGroup(groupId);
    }

    @Override
    public Group getGroup(String groupName, Group parent) {
        return this.delegate.getGroup(groupName, parent);
    }

    @Override
    public boolean isMember(IdentityType identityType, Group group) {
        return this.delegate.isMember(identityType, group);
    }

    @Override
    public void addToGroup(IdentityType identityType, Group group) {
        this.delegate.addToGroup(identityType, group);
    }

    @Override
    public void removeFromGroup(IdentityType identityType, Group group) {
        this.delegate.removeFromGroup(identityType, group);
    }

    @Override
    public Role getRole(String name) {
        return this.delegate.getRole(name);
    }

    @Override
    public boolean hasGroupRole(IdentityType identityType, Role role, Group group) {
        return this.delegate.hasGroupRole(identityType, role, group);
    }

    @Override
    public void grantGroupRole(IdentityType identityType, Role role, Group group) {
        this.delegate.grantGroupRole(identityType, role, group);
    }

    @Override
    public void revokeGroupRole(IdentityType identityType, Role role, Group group) {
        this.delegate.revokeGroupRole(identityType, role, group);
    }

    @Override
    public boolean hasRole(IdentityType identityType, Role role) {
        return this.delegate.hasRole(identityType, role);
    }

    @Override
    public void grantRole(IdentityType identityType, Role role) {
        this.delegate.grantRole(identityType, role);
    }

    @Override
    public void revokeRole(IdentityType identityType, Role role) {
        this.delegate.revokeRole(identityType, role);
    }

    @Override
    public <T extends IdentityType> IdentityQuery<T> createQuery(Class<T> identityType) {
        return this.delegate.createQuery(identityType);
    }

    @Override
    public void validateCredentials(Credentials credentials) {
        this.delegate.validateCredentials(credentials);
    }

    @Override
    public void updateCredential(Agent agent, Object value) {
        this.delegate.updateCredential(agent, value);
    }
    
    @Override
    public void updateCredential(Agent agent, Object value, Date effectiveDate, Date expiryDate) {
        this.delegate.updateCredential(agent, value, effectiveDate, expiryDate);
    }

    @Override
    public IdentityType lookupIdentityByKey(String key) {
        return this.delegate.lookupIdentityByKey(key);
    }

    @Override
    public void loadAttribute(IdentityType identityType, String attributeName) {
        this.delegate.loadAttribute(identityType, attributeName);
    }

    @Override
    public void createRealm(Realm realm) {
        this.delegate.createRealm(realm);
    }

    @Override
    public void removeRealm(Realm realm) {
        this.delegate.removeRealm(realm);
    }

    @Override
    public Realm getRealm(String name) {
        return this.delegate.getRealm(name);
    }

    @Override
    public void createTier(Tier tier) {
        this.delegate.createTier(tier);
    }

    @Override
    public void removeTier(Tier tier) {
        this.delegate.removeTier(tier);
    }

    @Override
    public Tier getTier(String id) {
        return this.delegate.getTier(id);
    }

    @Override
    public IdentityManager forRealm(Realm realm) {
        return this.delegate.forRealm(realm);
    }

    @Override
    public IdentityManager forTier(Tier tier) {
        return this.delegate.forTier(tier);
    }
}