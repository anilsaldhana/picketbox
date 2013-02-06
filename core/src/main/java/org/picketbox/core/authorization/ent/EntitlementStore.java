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
package org.picketbox.core.authorization.ent;

import org.picketbox.core.authorization.Resource;
import org.picketlink.idm.model.IdentityType;

/**
 * A store of {@link Entitlement}
 *
 * @author anil saldhana
 * @since Oct 25, 2012
 */
public interface EntitlementStore {

    /**
     * Add a {@link EntitlementCollection} for a {@link Resource}
     *
     * @param resource
     * @param user
     * @param collection
     * @return
     */
    boolean addEntitlements(Resource resource, IdentityType identityType, EntitlementCollection collection);

    /**
     * <p>Returns a {@link EntitlementCollection} for the given {@link Resource} and {@link IdentityType}.</p>
     *
     * @param resource
     * @param identityType
     * @return
     */
    EntitlementCollection entitlements(Resource resource, IdentityType identityType);

}