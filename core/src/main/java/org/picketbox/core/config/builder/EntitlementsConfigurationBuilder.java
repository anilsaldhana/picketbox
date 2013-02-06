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

package org.picketbox.core.config.builder;

import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.ent.EntitlementCollection;
import org.picketbox.core.authorization.ent.EntitlementStore;
import org.picketbox.core.authorization.ent.impl.InMemoryEntitlementStore;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.EntitlementsConfiguration;
import org.picketlink.idm.model.IdentityType;

/**
 * @author Pedro Silva
 *
 */
public class EntitlementsConfigurationBuilder extends AbstractConfigurationBuilder<EntitlementsConfiguration> {

    private final EntitlementsConfiguration configuration;

    public EntitlementsConfigurationBuilder(ConfigurationBuilder builder) {
        super(builder);
        this.configuration = new EntitlementsConfiguration();
    }

    @Override
    protected void setDefaults() {
        if (this.configuration.getEntitlementStore() == null) {
            this.configuration.setEntitlementStore(new InMemoryEntitlementStore());
        }
    }

    public EntitlementsConfigurationBuilder store(EntitlementStore entitlementStore) {
        this.configuration.setEntitlementStore(entitlementStore);
        return this;
    }

    @Override
    protected EntitlementsConfiguration doBuild() {
        return this.configuration;
    }

    public void add(Resource resource, IdentityType identityType, EntitlementCollection entitlements) {
        this.configuration.addEntitlements(resource, identityType, entitlements);
    }

}
