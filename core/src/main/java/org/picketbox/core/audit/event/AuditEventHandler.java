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

package org.picketbox.core.audit.event;

import java.util.HashMap;

import org.picketbox.core.audit.AuditEvent;
import org.picketbox.core.audit.AuditProvider;
import org.picketbox.core.audit.AuditType;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserAuthenticationFailedEvent;
import org.picketbox.core.authentication.event.UserNotAuthenticatedEvent;
import org.picketbox.core.event.EventObserver;

/**
 * <p>
 * This class acts as a handler for security related events, auditing each event using a specific {@link AuditProvider}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class AuditEventHandler {

    private AuditProvider auditProvider;

    public AuditEventHandler() {

    }

    public AuditEventHandler(AuditProvider auditProvider) {
        this.auditProvider = auditProvider;
    }

    @EventObserver
    public void onSuccessfulAuthentication(UserAuthenticatedEvent event) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        AuditEvent auditRecord = new AuditEvent(AuditType.AUTHENTICATION, map);

        auditRecord.setUserContext(event.getUserContext());
        auditRecord.setDescription("User " + event.getUserContext().getUser().getLoginName() + " was authenticated");

        getAuditProvider().audit(auditRecord);
    }

    @EventObserver
    public void onUnSuccessfulAuthentication(UserNotAuthenticatedEvent event) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        AuditEvent auditRecord = new AuditEvent(AuditType.AUTHENTICATION, map);

        auditRecord.setUserContext(event.getUserContext());
        auditRecord.setDescription("Invalid credentials for User " + event.getUserContext().getCredential().getUserName() + ".");

        getAuditProvider().audit(auditRecord);
    }

    @EventObserver
    public void onAuthenticationFailed(UserAuthenticationFailedEvent event) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        AuditEvent auditRecord = new AuditEvent(AuditType.AUTHENTICATION, map, event.getException());

        auditRecord.setUserContext(event.getUserContext());
        auditRecord.setDescription("Authentication Failed for User " + event.getUserContext().getCredential().getUserName() + ".");

        getAuditProvider().audit(auditRecord);
    }

    public AuditProvider getAuditProvider() {
        return this.auditProvider;
    }
}