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

package org.picketbox.core.audit.providers;

import static org.picketbox.core.PicketBoxLogger.LOGGER;

import org.picketbox.core.audit.AbstractAuditProvider;
import org.picketbox.core.audit.AuditEvent;

/**
 * Audit Provider that just logs the audit event using a Logger. The flexibility of passing the audit log entries to a different
 * sink (database, jms queue, file etc) can be controlled in the logging configuration (Eg: log4j.xml in log4j)
 * <p>
 * Ensure that the appender is configured properly in the global log4j.xml for log entries to go to a log, separate from the
 * regular server logs.
 * </p>
 *
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @since Aug 21, 2006
 */
public class LogAuditProvider extends AbstractAuditProvider {

    @Override
    public void audit(AuditEvent auditEvent) {
        Exception e = auditEvent.getUnderlyingException();

        if (e != null) {
            if (isTraceEnabled())
                LOGGER.trace(auditEvent, e);
        } else {
            if (isTraceEnabled())
                LOGGER.trace(auditEvent);
        }
    }

    private boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }
}
