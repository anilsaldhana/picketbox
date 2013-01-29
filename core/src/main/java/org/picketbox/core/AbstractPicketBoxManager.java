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

package org.picketbox.core;

import static org.picketbox.core.PicketBoxLogger.LOGGER;
import static org.picketbox.core.PicketBoxMessages.MESSAGES;

import java.security.Principal;

import org.picketbox.core.audit.AbstractAuditProvider;
import org.picketbox.core.audit.AuditProvider;
import org.picketbox.core.audit.event.AuditEventHandler;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.TrustedUsernameCredential;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserAuthenticationFailedEvent;
import org.picketbox.core.authentication.event.UserNotAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserPreAuthenticationEvent;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.ent.EntitlementsManager;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.ConfigurationException;
import org.picketbox.core.identity.PicketBoxIdentityManager;
import org.picketbox.core.identity.UserContextPopulator;
import org.picketbox.core.identity.impl.DefaultUserContextPopulator;
import org.picketbox.core.logout.event.UserLoggedOutEvent;
import org.picketbox.core.session.DefaultSessionManager;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;
import org.picketlink.idm.IdentityManager;

/**
 * <p>
 * Base class for {@link PicketBoxManager} implementations.
 * </p>
 *
 * @author anil saldhana
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketBoxManager extends AbstractPicketBoxLifeCycle implements PicketBoxManager {

    private AuthenticationProvider authenticationProvider;
    private AuthorizationManager authorizationManager;
    private SessionManager sessionManager;
    private UserContextPopulator userContextPopulator;
    private IdentityManager identityManager;
    private PicketBoxConfiguration configuration;
    private PicketBoxEventManager eventManager;
    private AuditProvider auditProvider;

    @SuppressWarnings("unused")
    // TODO: handle entitlements
    private EntitlementsManager entitlementsManager;

    public AbstractPicketBoxManager(PicketBoxConfiguration configuration) {
        this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#authenticate(org.picketbox.core.UserContext)
     */
    @Override
    public UserContext authenticate(UserContext userContext) throws AuthenticationException {
        checkIfStarted();

        try {
            LOGGER.tracef("authenticating user [%s]", userContext);

            PicketBoxSession userSession = restoreSession(userContext);

            // if there is a valid session associate it with the subject and performs a silent authentication, trusting the
            // provided
            // principal.
            if (userSession != null) {
                UserContext restoredUserContext = userSession.getUserContext();
                Principal restoredPrincipal = restoredUserContext.getPrincipal(false);

                if (restoredPrincipal == null) {
                    throw new AuthenticationException("Principal not retrieved");
                }

                LOGGER.tracef("performing silent authentication and re-authenticating principal %s",
                        restoredPrincipal.getName());

                TrustedUsernameCredential credential = new TrustedUsernameCredential(restoredPrincipal.getName());

                userContext = new UserContext(credential);
            }

            // performs the authentication
            performAuthentication(userContext);

            if (userContext.isAuthenticated()) {
                performSuccessfulAuthentication(userContext, userSession);
                LOGGER.tracef("authenticated user is: [%s]", userContext);
            } else {
                LOGGER.tracef("user not authenticated: [%s]", userContext);
                performUnsuccessfulAuthentication(userContext);
            }
        } catch (Exception e) {
            getEventManager().raiseEvent(new UserAuthenticationFailedEvent(userContext, e));
            throw PicketBoxMessages.MESSAGES.authenticationFailed(e);
        }

        return userContext;
    }

    /**
     * <p>
     * Tries to restore the session associated with the given {@link UserContext}.
     * </p>
     *
     * @param userContext
     * @return
     */
    private PicketBoxSession restoreSession(UserContext userContext) {
        PicketBoxSession userSession = null;

        if (this.sessionManager != null) {
            PicketBoxLogger.LOGGER.trace("trying to restore previous created session.");

            userSession = this.sessionManager.restoreSession(userContext);

            if (userSession != null) {
                LOGGER.tracef("found session [%s]", userSession);
            } else {
                LOGGER.trace("session not associated with user.");
            }
        }

        return userSession;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#logout(org.picketbox.core.UserContext)
     */
    @Override
    public void logout(UserContext authenticatedUser) throws IllegalStateException {
        checkIfStarted();

        if (authenticatedUser.isAuthenticated()) {
            LOGGER.tracef("logging out and invalidating user [%s]", authenticatedUser);
            authenticatedUser.invalidate();
            getEventManager().raiseEvent(new UserLoggedOutEvent(authenticatedUser));
        } else {
            throw MESSAGES.invalidUserSession();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSecurityContext)
     */
    @Override
    public boolean authorize(UserContext authenticatedUser, Resource resource) {
        checkIfStarted();

        if (!authenticatedUser.isAuthenticated()) {
            throw MESSAGES.userNotAuthenticated();
        }

        try {
            if (this.authorizationManager == null) {
                return true;
            }

            LOGGER.tracef("Authorizing user to resource. Resource [$s] and User [%s]", resource, authenticatedUser);

            return this.authorizationManager.authorize(resource, authenticatedUser);
        } catch (Exception e) {
            throw MESSAGES.authorizationFailed(e);
        }
    }

    /**
     * <p>
     * Sub-classes can override this method to provde some pre-processing logic durint the authentication. Depending the return
     * value the authentication process is aborted or not.
     * </p>
     *
     * @param userContext
     * @return
     */
    protected boolean doPreAuthentication(UserContext userContext) {
        return true;
    }

    /**
     * <p>
     * Performs the authentication using the provided {@link UserCredential}.
     * </p>
     *
     * @param userContext
     * @return
     * @throws AuthenticationException
     */
    private void performAuthentication(UserContext userContext) throws AuthenticationException {
        UserCredential credential = userContext.getCredential();

        if (credential == null) {
            throw MESSAGES.invalidNullCredential();
        }

        AuthenticationResult result = null;

        if (doPreAuthentication(userContext)) {
            LOGGER.tracef("performing authentication for credential [%s]", credential);

            getEventManager().raiseEvent(new UserPreAuthenticationEvent(userContext));

            String[] mechanisms = this.authenticationProvider.getSupportedMechanisms();

            boolean supportedCredential = false;

            for (String mechanismName : mechanisms) {
                AuthenticationMechanism mechanism = this.authenticationProvider.getMechanism(mechanismName);

                if (mechanism.supports(credential)) {
                    LOGGER.tracef("using authentication mechanism [%s]", mechanism);

                    try {
                        result = mechanism.authenticate(credential);
                        supportedCredential = true;

                        if (result == null) {
                            LOGGER.warnf("mechanism [%s] returned a null AuthenticationResult. Unexpected behavior may occur.",
                                    mechanism);
                        }
                    } catch (AuthenticationException e) {
                        throw MESSAGES.authenticationFailed(e);
                    }
                }
            }

            if (!supportedCredential) {
                throw MESSAGES.unsupportedCredentialType(credential);
            }
        } else {
            LOGGER.tracef("doPreAuthentication method returned false. authentication will not me performed for user [%s]",
                    userContext);
        }

        if (result == null) {
            result = new AuthenticationResult();
        }

        userContext.setAuthenticationResult(result);
    }

    /**
     * <p>
     * Performs some post authentication steps when the authentication is successful.
     * </p>
     *
     * @param userContext
     * @param userSession
     *
     * @return
     */
    protected UserContext performSuccessfulAuthentication(UserContext userContext, PicketBoxSession userSession) {
        LOGGER.trace("user is authenticated. configuring security context.");

        if (userSession == null) {
            userSession = createSession(userContext);
        }

        userContext.setSession(userSession);
        userContext.setCredential(null);

        LOGGER.tracef("populating user context with populator [%s]", this.userContextPopulator);

        UserContext populatedUserContext = this.userContextPopulator.getIdentity(userContext);

        getEventManager().raiseEvent(new UserAuthenticatedEvent(userContext));

        return populatedUserContext;
    }

    /**
     * <p>
     * Performs some post authentication steps when the authentication fail.
     * </p>
     *
     * @param userContext
     */
    protected void performUnsuccessfulAuthentication(UserContext userContext) {
        getEventManager().raiseEvent(new UserNotAuthenticatedEvent(userContext));
    }

    /**
     * <p>
     * Creates a session for the authenticated {@link UserContext}. The subject must be authenticated, its isAuthenticated()
     * method should return true.
     * </p>
     *
     * @param securityContext the security context with environment specific information
     * @param authenticatedUserContext the authenticated subject
     * @return
     *
     * @throws IllegalArgumentException in the case the subject is not authenticated.
     */
    private PicketBoxSession createSession(UserContext authenticatedUserContext) throws IllegalArgumentException {
        if (!authenticatedUserContext.isAuthenticated()) {
            throw PicketBoxMessages.MESSAGES.userNotAuthenticated();
        }

        if (this.sessionManager == null) {
            LOGGER.tracef("no session created. sessions are NOT enabled.");
            return null;
        }

        PicketBoxSession session = this.sessionManager.create(authenticatedUserContext);

        LOGGER.tracef("created session [%s]", session);

        return session;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStart()
     */
    @Override
    protected void doStart() {
        if (this.configuration == null) {
            throw new ConfigurationException("No configuration provided. Manager could not be started.");
        }

        this.eventManager = this.configuration.getEventManager().getEventManager();

        this.authenticationProvider = new PicketBoxAuthenticationProvider(this);

        if (!this.configuration.getAuthorization().getManagers().isEmpty()) {
            this.authorizationManager = this.configuration.getAuthorization().getManagers().get(0);
        }

        this.identityManager = new PicketBoxIdentityManager(this.configuration.getIdentityManager());

        this.userContextPopulator = this.configuration.getIdentityManager().getUserPopulator();

        if (this.userContextPopulator == null) {
            this.userContextPopulator = new DefaultUserContextPopulator(this.identityManager);
        }

        this.sessionManager = this.configuration.getSessionManager().getManager();

        if (this.sessionManager == null && this.configuration.getSessionManager().getStore() != null) {
            this.sessionManager = new DefaultSessionManager(this);
        }

        if (this.sessionManager != null) {
            this.sessionManager.start();
        }

        if (this.configuration.getAuditConfig() != null && this.configuration.getAuditConfig().getProvider() != null) {
            this.auditProvider = this.configuration.getAuditConfig().getProvider();

            if (this.auditProvider instanceof AbstractAuditProvider) {
                ((AbstractAuditProvider) this.auditProvider).setPicketBoxManager(this);
            }

            this.eventManager.addHandler(new AuditEventHandler(this.auditProvider));
        }

        doConfigure();

        logConfiguration();

        LOGGER.startingPicketBox();

        if (this.authorizationManager != null) {
            this.authorizationManager.start();
        }

        this.eventManager.raiseEvent(new InitializedEvent(this));
    }

    /**
     * <p>
     * Subclasses can override this method to provide some additional processing before the startup.
     * </p>
     */
    protected void doConfigure() {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStop()
     */
    @Override
    protected void doStop() {
        if (this.authorizationManager != null) {
            this.authorizationManager.stop();
        }

        if (this.sessionManager != null) {
            this.sessionManager.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#getEventManager()
     */
    @Override
    public PicketBoxEventManager getEventManager() {
        return this.eventManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#getIdentityManager()
     */
    @Override
    public IdentityManager getIdentityManager() {
        return this.identityManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#getConfiguration()
     */
    @Override
    public PicketBoxConfiguration getConfiguration() {
        return this.configuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#getSessionManager()
     */
    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public AuditProvider getAuditProvider() {
        return this.auditProvider;
    }

    protected void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * <p>
     * Helper method to log the configuration.
     * </p>
     */
    private void logConfiguration() {
        LOGGER.debugInstanceUsage("Event Manager", this.eventManager);

        LOGGER.debugInstanceUsage("Authentication Provider", this.authenticationProvider);

        if (LOGGER.isDebugEnabled()) {
            String[] supportedMechanisms = this.authenticationProvider.getSupportedMechanisms();

            for (String string : supportedMechanisms) {
                LOGGER.trace(" Authentication Mechanism: " + string);
            }
        }

        LOGGER.debugInstanceUsage("Authorization Manager", this.authorizationManager);

        LOGGER.debugInstanceUsage("Identity Manager", this.identityManager);
        LOGGER.debugInstanceUsage(" Identity Store", this.configuration.getIdentityManager().getIdentityManagerConfiguration());

        LOGGER.debugInstanceUsage("User Context Populator", this.userContextPopulator);

        if (this.sessionManager != null) {
            LOGGER.debugInstanceUsage("Session Manager", this.sessionManager);
            LOGGER.debugInstanceUsage(" Session Store", this.configuration.getSessionManager().getStore());
        } else {
            LOGGER.trace("Session Management is DISABLED.");
        }

        if (this.auditProvider != null) {
            LOGGER.debugInstanceUsage("Audit Provider", this.auditProvider);
        } else {
            LOGGER.trace("Auditing is DISABLED.");
        }
    }
}
