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

package org.picketbox.test.session;

import static org.junit.Assert.assertNotNull;

import org.picketbox.core.event.EventObserver;
import org.picketbox.core.session.event.SessionCreatedEvent;
import org.picketbox.core.session.event.SessionExpiredEvent;
import org.picketbox.core.session.event.SessionGetAttributeEvent;
import org.picketbox.core.session.event.SessionInvalidatedEvent;
import org.picketbox.core.session.event.SessionSetAttributeEvent;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class MockSessionEventHandler {

    boolean onCreateCalled = false;
    boolean onSetAttributeCalled = false;
    boolean onGetAttributeCalled = false;
    boolean onInvalidateCalled = false;
    boolean onExpirationCalled = false;
    
    @EventObserver
    public void onCreate(SessionCreatedEvent event) {
        this.onCreateCalled = true;
        assertNotNull(event);
        assertNotNull(event.getSession());
    }

    @EventObserver
    public void onSetAttribute(SessionSetAttributeEvent event) {
        this.onSetAttributeCalled = true;
        assertNotNull(event);
        assertNotNull(event.getSession());
        assertNotNull(event.getAttributeName());
        assertNotNull(event.getAttributeValue());
    }

    @EventObserver
    public void onGetAttribute(SessionGetAttributeEvent event) {
        this.onGetAttributeCalled = true;
        assertNotNull(event);
        assertNotNull(event.getSession());
        assertNotNull(event.getAttributeName());
    }

    @EventObserver
    public void onInvalidate(SessionInvalidatedEvent event) {
        this.onInvalidateCalled = true;
        assertNotNull(event);
        assertNotNull(event.getSession());
    }

    @EventObserver
    public void onExpiration(SessionExpiredEvent event) {
        this.onExpirationCalled = true;
        assertNotNull(event);
        assertNotNull(event.getSession());
    }
    
}
