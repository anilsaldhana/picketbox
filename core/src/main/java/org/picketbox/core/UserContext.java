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

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.credential.TrustedUsernameCredential;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionId;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

/**
 * An Application View of the authenticated/authorized User
 *
 * @author anil saldhana
 * @since Jul 12, 2012
 */
public class UserContext implements Serializable {

    private static final long serialVersionUID = -7767959770091515534L;

    private Subject subject;
    private User user;

    @SuppressWarnings("unchecked")
    private Collection<Role> roles = Collections.EMPTY_LIST;

    @SuppressWarnings("unchecked")
    private Collection<Group> groups = Collections.EMPTY_LIST;

    protected transient Map<String, Object> contextData = new HashMap<String, Object>();

    private transient PicketBoxSession session;

    private transient UserCredential credential;

    private AuthenticationResult authenticationResult;

    // TODO: how to deal with groups/nested groups etc

    public UserContext() {

    }

    public UserContext(SessionId<? extends Serializable> sessionId) {
        if (sessionId == null) {
            throw PicketBoxMessages.MESSAGES.invalidUserSession();
        }
        this.session = new PicketBoxSession(sessionId);
    }

    public UserContext(TrustedUsernameCredential credential) {
        this.credential = credential;
    }

    /**
     * get the user
     *
     * @return
     */
    public Principal getPrincipal() {
        return getPrincipal(true);
    }

    Principal getPrincipal(boolean userAuthenticatedRestriction) {
        if (userAuthenticatedRestriction && !isAuthenticated()) {
            throw PicketBoxMessages.MESSAGES.userNotAuthenticated();
        }

        return this.authenticationResult != null ? this.authenticationResult.getPrincipal() : null;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public UserContext setUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * Get the JAAS Subject if available
     *
     * @return
     */
    public Subject getSubject() {
        return this.subject;
    }

    /**
     * Set the JAAS Subject
     *
     * @param subject
     */
    public UserContext setSubject(Subject subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Get a read only map of contextual data
     *
     * @return
     */
    public Map<String, Object> getContextData() {
        return Collections.unmodifiableMap(this.contextData);
    }

    /**
     * Set context data
     *
     * @param contextData
     */
    public UserContext setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
        return this;
    }

    /**
     * @return
     */
    public boolean isAuthenticated() {
        boolean isAuthenticated = this.authenticationResult != null
                && this.authenticationResult.getStatus().equals(AuthenticationStatus.SUCCESS);

        if (isAuthenticated) {
            if (this.session != null && !this.session.isValid()) {
                isAuthenticated = false;
            }
        }

        return isAuthenticated;
    }

    public UserContext setSession(PicketBoxSession session) {
        this.session = session;
        return this;
    }

    public PicketBoxSession getSession() {
        return this.session;
    }

    public UserCredential getCredential() {
        return this.credential;
    }

    public UserContext setCredential(UserCredential credential) {
        this.credential = credential;
        return this;
    }

    /**
     * <p>
     * Invalidate the instance and clear its state.
     * </p>
     */
    public void invalidate() {
        this.credential = null;
        this.contextData.clear();
        this.roles = null;
        this.groups = null;
        this.subject = null;
        this.user = null;
        this.authenticationResult = null;

        if (this.session != null && this.session.isValid()) {
            try {
                this.session.invalidate();
            } catch (PicketBoxSessionException e) {
                throw PicketBoxMessages.MESSAGES.unableToInvalidateSession(e);
            }
        }
    }

    /**
     * <p>
     * Checks if this subject has the specified role.
     * </p>
     *
     * @param role
     * @return
     */
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            throw PicketBoxMessages.MESSAGES.userNotAuthenticated();
        }

        for (Role userRole : getRoles()) {
            if (role.equals(userRole.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the role names
     *
     * @return
     */
    public Collection<String> getRoleNames() {
        Set<String> roleNames = new HashSet<String>();
        for (Role userRole : getRoles()) {
            roleNames.add(userRole.getName());
        }

        return Collections.unmodifiableCollection(roleNames);
    }

    public UserContext setRoles(Collection<Role> roles) {
        this.roles = roles;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Collection<Role> getRoles() {
        if (this.roles == null) {
            this.roles = Collections.EMPTY_LIST;
        }

        return Collections.unmodifiableCollection(this.roles);
    }

    /**
     * Get the group names
     *
     * @return
     */
    public Collection<String> getGroupNames() {
        Set<String> groupNames = new HashSet<String>();
        for (Group userRole : getGroups()) {
            groupNames.add(userRole.getName());
        }

        return Collections.unmodifiableCollection(groupNames);
    }

    public UserContext setGroups(Collection<Group> groups) {
        this.groups = groups;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Collection<Group> getGroups() {
        if (this.groups == null) {
            this.groups = Collections.EMPTY_LIST;
        }

        return Collections.unmodifiableCollection(this.groups);
    }

    protected UserContext setAuthenticationResult(AuthenticationResult result) {
        this.authenticationResult = result;
        return this;
    }

    public AuthenticationResult getAuthenticationResult() {
        return this.authenticationResult;
    }

    @Override
    public String toString() {
        String userName  = null;

        if (isAuthenticated()) {
            userName = getPrincipal().getName();
        } else if (getCredential() != null) {
            userName = getCredential().getUserName();
        }

        return " Username: " + userName + "/ IsAuthenticated: " + this.isAuthenticated() + " / Credential: [" + this.credential + "] / Authentication Result: [" + this.authenticationResult + "] / Session: [" + this.session + "]";
    }
}
