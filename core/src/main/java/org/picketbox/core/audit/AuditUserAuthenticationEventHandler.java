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

package org.picketbox.core.audit;

import java.util.Date;
import java.util.HashMap;

import org.picketbox.core.authentication.event.UserAuthenticationEvent;
import org.picketbox.core.authentication.event.UserAuthenticationEventHandler;
import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class AuditUserAuthenticationEventHandler implements UserAuthenticationEventHandler {

    private AuditProvider auditProvider;

    public AuditUserAuthenticationEventHandler(AuditProvider auditProvider) {
        this.auditProvider = auditProvider;
    }

    @Override
    public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
        return UserAuthenticationEvent.class;
    }

    @Override
    public void onSuccessfulAuthentication(UserAuthenticationEvent event) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("creationDate", new Date());
        map.put("user", event.getUserContext());
        map.put("details", "User authenticated.");

        this.auditProvider.audit(new AuditEvent(AuditType.AUTHENTICATION, map));
    }

    @Override
    public void onUnSuccessfulAuthentication(UserAuthenticationEvent event) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("creationDate", new Date());
        map.put("user", event.getUserContext());
        map.put("details", "Authentication failed.");

        this.auditProvider.audit(new AuditEvent(AuditType.AUTHENTICATION, map));
    }

}