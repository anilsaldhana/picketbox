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
package org.picketbox.core.authorization.ent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a collection of {@link Entitlement}
 *
 * @author anil saldhana
 * @since Oct 24, 2012
 */
public class EntitlementCollection {
    public static final EntitlementCollection EMPTY_COLLECTION = new EntitlementCollection("EMPTY");

    protected String name;
    protected List<Entitlement> entitlements = new ArrayList<Entitlement>();

    /**
     * Construct {@link EntitlementCollection}
     *
     * @param name name of the collection
     */
    public EntitlementCollection(String name) {
        this.name = name;
    }

    /**
     * Get the name of the {@link EntitlementCollection}
     *
     * @return the name 
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add an {@link Entitlement}
     *
     * @param entitlement an {@link Entitlement}
     */
    public void add(Entitlement entitlement) {
        this.entitlements.add(entitlement);
    }

    /**
     * Add an {@link EntitlementCollection}
     *
     * @param entitlement
     */
    public void add(EntitlementCollection entitlementCollection) {
        this.entitlements.addAll(entitlementCollection.getEntitlements());
    }

    /**
     * Add all the entitlements from the list
     *
     * @param entitlements a {@link List} of {@link Entitlement}
     */
    public void addAll(List<Entitlement> entitlements) {
        this.entitlements.addAll(entitlements);
    }

    /**
     * Check whether {@link Entitlement} is present
     *
     * @param entitlement an {@link Entitlement}
     * @return
     */
    public boolean contains(Entitlement entitlement) {
        return this.entitlements.contains(entitlement);
    }

    /**
     * Remove an {@link Entitlement}
     *
     * @param entitlement
     */
    public void remove(Entitlement entitlement) {
        this.entitlements.remove(entitlement);
    }

    /**
     * Clear all the {@link Entitlement}
     */
    public void clear() {
        this.entitlements.clear();
    }

    /**
     * Get the list of {@link Entitlement}
     *
     * @return
     */
    public List<Entitlement> getEntitlements() {
        return Collections.unmodifiableList(this.entitlements);
    }

    /**
     * Set the list of {@link Entitlement}
     *
     * @param entitlements
     */
    public void setEntitlements(List<Entitlement> entitlements) {
        this.entitlements.clear();
        this.entitlements.addAll(entitlements);
    }

    /**
     * Quick way to create a collection
     *
     * @param name of the entitlement collection
     * @param entitlements array of {@link Entitlement} objects
     * @return a {@link EntitlementCollection}
     */
    public static EntitlementCollection create(String name, Entitlement[] entitlements) {
        EntitlementCollection coll = new EntitlementCollection(name);
        coll.addAll(Arrays.asList(entitlements));
        return coll;
    }
}