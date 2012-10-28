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

package org.picketbox.core.identity.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.UserContext;
import org.picketbox.core.identity.UserContextPopulator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;
import org.picketlink.idm.query.GroupQuery;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DefaultUserContextPopulator implements UserContextPopulator {

    private IdentityManager identityManager;

    public DefaultUserContextPopulator(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.identity.UserContextPopulator#getIdentity(org.picketbox.core.UserContext)
     */
    @Override
    public UserContext getIdentity(UserContext authenticatedUserContext) {
        if (authenticatedUserContext == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("authenticatedUserContext");
        }

        Principal principal = authenticatedUserContext.getPrincipal();

        User userFromIDM = getIdentityManager().getUser(principal.getName());
        Collection<Role> rolesFromIDM = getIdentityManager().getRoles(userFromIDM, null);

        authenticatedUserContext.setUser(userFromIDM);
        authenticatedUserContext.setRoles(rolesFromIDM);

        GroupQuery groupQuery = getIdentityManager().createGroupQuery();

        groupQuery.setRelatedUser(userFromIDM);

        List<Group> groups = groupQuery.executeQuery();

        authenticatedUserContext.setGroups(groups);

        return authenticatedUserContext;
    }

    public IdentityManager getIdentityManager() {
        return this.identityManager;
    }

    public void setIdentityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }
}
