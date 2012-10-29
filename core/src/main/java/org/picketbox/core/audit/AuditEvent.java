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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Holds audit information.
 * </p>
 *
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @since Aug 21, 2006
 */
public class AuditEvent {

    private String auditType;

    private Map<String, Object> contextMap = new HashMap<String, Object>();

    private Exception underlyingException = null;

    public AuditEvent(String type) {
        this.auditType = type;
    }

    public AuditEvent(String type, Map<String, Object> map) {
        this(type);
        this.contextMap = map;
    }

    public AuditEvent(String type, Map<String, Object> map, Exception ex) {
        this(type, map);
        this.underlyingException = ex;
    }

    /**
     * Return the Audit Level
     *
     * @return
     */
    public String getAuditType() {
        return this.auditType;
    }

    /**
     * Get the Contextual Map
     *
     * @return Map that is final
     */
    public Map<String, Object> getContextMap() {
        return this.contextMap;
    }

    /**
     * Set a non-modifiable Context Map
     *
     * @param cmap Map that is final
     */
    public void setContextMap(final Map<String, Object> cmap) {
        this.contextMap = cmap;
    }

    /**
     * Get the Exception part of the audit
     *
     * @return
     */
    public Exception getUnderlyingException() {
        return this.underlyingException;
    }

    /**
     * Set the exception on which an audit is happening
     *
     * @param underlyingException
     */
    public void setUnderlyingException(Exception underlyingException) {
        this.underlyingException = underlyingException;
    }

    @Override
    public String toString() {
        StringBuilder sbu = new StringBuilder();
        sbu.append("[").append(this.auditType).append("]");
        sbu.append(dissectContextMap());
        return sbu.toString();
    }

    /**
     * Provide additional information about the entities in the context map
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private String dissectContextMap() {
        StringBuilder sbu = new StringBuilder();
        if (this.contextMap != null) {
            for (String key : this.contextMap.keySet()) {
                sbu.append(key).append("=");
                Object obj = this.contextMap.get(key);
                if (obj instanceof Object[]) {
                    Object[] arr = (Object[]) obj;
                    obj = Arrays.asList(arr);
                }
                if (obj instanceof Collection) {
                    Collection<Object> coll = (Collection<Object>) obj;
                    for (Object o : coll) {
                        sbu.append(o).append(";");
                    }
                } else
                    sbu.append(obj).append(";");
            }
        }
        return sbu.toString();
    }

}
