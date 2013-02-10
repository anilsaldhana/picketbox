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

package org.picketbox.http.config;

import java.util.HashMap;
import java.util.Map;

import org.picketbox.http.resource.HTTPProtectedResourceManager;
import org.picketbox.http.resource.ProtectedResource;
import org.picketbox.http.resource.ProtectedResourceConstraint;
import org.picketbox.http.resource.ProtectedResourceManager;

/**
 * Protected Resource Configuration Builder
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class ProtectedResourceConfigurationBuilder extends AbstractPicketBoxHTTPConfigBuilder<ProtectedResourceConfig> {

    @SuppressWarnings("rawtypes")
    private ProtectedResourceManager manager;
    private Map<String, ProtectedResource> resources = new HashMap<String, ProtectedResource>();

    /**
     * Build a {@link ProtectedResourceConfigurationBuilder} using the {@link HTTPConfigurationBuilder}
     *
     * @param builder the {@link HTTPConfigurationBuilder}
     */
    public ProtectedResourceConfigurationBuilder(HTTPConfigurationBuilder builder) {
        super(builder);
    }

    /**
     * Set the {@link ProtectedResourceManager}
     *
     * @param manager
     * @return
     */
    @SuppressWarnings("rawtypes")
    public ProtectedResourceConfigurationBuilder manager(ProtectedResourceManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * Add a resource to the protected resources providing the constraint
     *
     * @param pattern
     * @param constraint a {@link ProtectedResourceConstraint}
     * @return
     */
    public ProtectedResourceConfigurationBuilder restrict(String pattern, ProtectedResourceConstraint constraint) {
        createProtectedResource(pattern, constraint);
        return this;
    }

    /**
     * Add a resource to the protected resources along with the roles that can access the resource
     *
     * @param pattern
     * @param roles
     * @return
     */
    public ProtectedResourceConfigurationBuilder allowedRoles(String pattern, String... roles) {
        ProtectedResource protectedResource = createProtectedResource(pattern, ProtectedResourceConstraint.AUTHORIZATION);

        protectedResource.setRoles(roles);

        return this;
    }

    public ProtectedResourceConfigurationBuilder allowedRoles(String pattern, ProtectedResourceConstraint constraint, String... roles) {
        ProtectedResource protectedResource = createProtectedResource(pattern, constraint);

        protectedResource.setRoles(roles);

        return this;
    }

    public ProtectedResourceConfigurationBuilder allowedGroups(String pattern, String... groups) {
        ProtectedResource protectedResource = createProtectedResource(pattern, ProtectedResourceConstraint.AUTHORIZATION);

        protectedResource.setGroups(groups);

        return this;
    }

    public ProtectedResourceConfigurationBuilder allowedGroups(String pattern, ProtectedResourceConstraint constraint, String... groups) {
        ProtectedResource protectedResource = createProtectedResource(pattern, constraint);

        protectedResource.setGroups(groups);

        return this;
    }

    @Override
    protected void setDefaults() {
        if (this.manager == null) {
            this.manager = new HTTPProtectedResourceManager();
        }
    }

    @Override
    protected ProtectedResourceConfig doBuild() {
        return new ProtectedResourceConfig(this.manager, this.resources.values());
    }

    private ProtectedResource createProtectedResource(String pattern, ProtectedResourceConstraint constraint) {
        ProtectedResource protectedResource = this.resources.get(pattern);

        if (protectedResource == null) {
            protectedResource = new ProtectedResource(pattern, constraint);
        }

        protectedResource.setConstraint(constraint);

        this.resources.put(pattern, protectedResource);

        return protectedResource;
    }
}