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
package org.picketbox.core.authorization.ent.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.picketbox.core.authorization.ent.Entitlement;

/**
 * Abstract class for {@link Entitlement}
 *
 * @author anil saldhana
 * @since Oct 24, 2012
 */
public abstract class AbstractEntitlement implements Entitlement {
    protected Map<String, String> pair = new HashMap<String, String>();

    @Override
    public String json() {
        StringBuilder sb = new StringBuilder(OPEN_PAREN);
        Set<String> keys = this.pair.keySet();
        int size = keys.size();
        Iterator<String> iter = keys.iterator();

        for (int i = 0; i < size; i++) {
            String key = iter.next();
            sb.append(key).append(COLON).append(this.pair.get(key));
            if (i > 0) {
                sb.append(COMMA);
            }
        }
        sb.append(CLOSE_PAREN);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.pair == null) ? 0 : this.pair.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEntitlement other = (AbstractEntitlement) obj;
        if (this.pair == null) {
            if (other.pair != null)
                return false;
        } else if (!mapEquals(this.pair, other.pair))
            return false;
        return true;
    }

    private boolean mapEquals(Map<String, String> a, Map<String, String> b) {
        int sizeA = a.size();
        int sizeB = b.size();
        if (sizeA != sizeB) {
            return false;
        }
        Iterator<String> iter = a.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String val = a.get(key);
            String oppVal = b.get(key);
            if (val.equals(oppVal) == false) {
                return false;
            }
        }
        return true;
    }
}