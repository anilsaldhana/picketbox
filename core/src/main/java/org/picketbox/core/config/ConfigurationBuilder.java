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

package org.picketbox.core.config;

import org.picketbox.core.config.builder.AbstractConfigurationBuilder;
import org.picketbox.core.config.builder.AuditConfigurationBuilder;
import org.picketbox.core.config.builder.AuthenticationConfigurationBuilder;
import org.picketbox.core.config.builder.AuthorizationConfigurationBuilder;
import org.picketbox.core.config.builder.EventManagerConfigurationBuilder;
import org.picketbox.core.config.builder.IdentityManagerConfigurationBuilder;
import org.picketbox.core.config.builder.SessionManagerConfigurationBuilder;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class ConfigurationBuilder extends AbstractConfigurationBuilder<PicketBoxConfiguration> {

    protected AuthenticationConfigurationBuilder authentication;
    private IdentityManagerConfigurationBuilder identityManager;
    private AuthorizationConfigurationBuilder authorization;
    private EventManagerConfigurationBuilder eventManager;
    private SessionManagerConfigurationBuilder sessionManager;
    private AuditConfigurationBuilder audit;

    public ConfigurationBuilder() {
        this.builder = this;
        this.authentication = createAuthenticationBuilder();
        this.authorization = new AuthorizationConfigurationBuilder(this);
        this.identityManager = createIdentityManager();
        this.eventManager = new EventManagerConfigurationBuilder(this);
        this.sessionManager = new SessionManagerConfigurationBuilder(this);
        this.audit = new AuditConfigurationBuilder(this);
    }

    protected IdentityManagerConfigurationBuilder createIdentityManager() {
        return new IdentityManagerConfigurationBuilder(this);
    }

    protected AuthenticationConfigurationBuilder createAuthenticationBuilder() {
        return new AuthenticationConfigurationBuilder(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.config.AbstractConfigurationBuilder#authentication()
     */
    @Override
    public AuthenticationConfigurationBuilder authentication() {
        return this.authentication;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.config.AbstractConfigurationBuilder#authorization()
     */
    @Override
    public AuthorizationConfigurationBuilder authorization() {
        return this.authorization;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.config.AbstractConfigurationBuilder#identityManager()
     */
    @Override
    public IdentityManagerConfigurationBuilder identityManager() {
        return this.identityManager;
    }

    @Override
    public EventManagerConfigurationBuilder eventManager() {
        return this.eventManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.config.AbstractConfigurationBuilder#sessionManager()
     */
    @Override
    public SessionManagerConfigurationBuilder sessionManager() {
        return this.sessionManager;
    }

    @Override
    public AuditConfigurationBuilder audit() {
        return this.audit;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.config.AbstractConfigurationBuilder#setDefaults()
     */
    @Override
    protected void setDefaults() {
    }

    @Override
    public PicketBoxConfiguration doBuild() {
        return new PicketBoxConfiguration(this.authentication.build(), this.authorization.build(),
                this.identityManager.build(), this.sessionManager.build(), this.eventManager.build(), this.audit.build());
    }

}
