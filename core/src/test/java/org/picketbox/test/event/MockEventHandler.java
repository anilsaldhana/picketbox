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

package org.picketbox.test.event;

import java.util.HashMap;

import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserAuthenticationFailedEvent;
import org.picketbox.core.authentication.event.UserNotAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserPreAuthenticationEvent;
import org.picketbox.core.event.EventObserver;
import org.picketbox.core.logout.event.UserLoggedOutEvent;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class MockEventHandler {

    private boolean successfulAuthentication;
    private boolean authenticationFailed;
    
    private boolean loggedOut;
    
    public static final String PRE_AUTH_CONTEXT_DATA = "PRE_AUTH_CONTEXT_DATA";

    private boolean preAuthenticationInvoked;

    @EventObserver
    public void onPreAuthentication(UserPreAuthenticationEvent event) {
        this.preAuthenticationInvoked = true;
        HashMap<String, Object> contextData = new HashMap<String, Object>();

        contextData.put(PRE_AUTH_CONTEXT_DATA, PRE_AUTH_CONTEXT_DATA);

        event.getUserContext().setContextData(contextData);
    }

    public boolean wasPreAuthenticationInvoked() {
        return this.preAuthenticationInvoked;
    }
    
    @EventObserver
    public void onLogout(UserLoggedOutEvent userLogOutEvent) {
        this.loggedOut = true;
    }

    public boolean wasLoggedOut() {
        return this.loggedOut;
    }
    
    @EventObserver
    public void onSuccessful(UserAuthenticatedEvent userAuthenticatedEvent) {
        this.successfulAuthentication = true;
    }

    @EventObserver
    public void onUnSuccessful(UserNotAuthenticatedEvent userAuthenticatedEvent) {
        this.successfulAuthentication = false;
    }

    @EventObserver
    public void onFailed(UserAuthenticationFailedEvent userAuthenticatedEvent) {
        this.authenticationFailed = false;
    }

    public boolean isSuccessfulAuthentication() {
        return this.successfulAuthentication;
    }

    public boolean isAuthenticationFailed() {
        return this.authenticationFailed;
    }

}
