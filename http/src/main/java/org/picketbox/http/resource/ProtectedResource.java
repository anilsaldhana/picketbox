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

package org.picketbox.http.resource;

import org.picketbox.core.UserContext;

/**
 * <p>
 * This class defines how a protected resource is configured. Protected resources have an access pattern and a security
 * constraint.
 * </p>
 * <p>
 * The access pattern defines what should be protected. If the protected resource is a web resources, it will be the URI for
 * that resource.
 * </p>
 * <p>
 * The security constraint defines how the resource should be protected. If it requires only authenticated users, if anonymous
 * access is permited or if it requires previous authorization.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class ProtectedResource {

    public static final String ANY_RESOURCE_PATTERN = "/*";

    public static final ProtectedResource DEFAULT_RESOURCE = new ProtectedResource(ANY_RESOURCE_PATTERN,
            ProtectedResourceConstraint.ALL);

    /**
     * <p>
     * Defines the pattern used to protecte a specific resource. It must begin with /.
     * </p>
     */
    private String pattern;

    /**
     * <p>
     * Security constraints to be applied. See {@link ProtectedResourceConstraint} for the possible values.
     * </p>
     */
    private String constraint = ProtectedResourceConstraint.ALL.name();

    private String[] roles;
    private String[] groups;

    public ProtectedResource(String pattern, ProtectedResourceConstraint constraint) {
        this.pattern = pattern;
        this.constraint = constraint.name();
    }

    public ProtectedResource(String pattern, ProtectedResourceConstraint constraint, String[] roles) {
        this(pattern, constraint);
        this.roles = roles;
    }

    public ProtectedResource(String pattern, ProtectedResourceConstraint constraint, String[] roles, String[] groups) {
        this(pattern, constraint, roles);
        this.groups = groups;
    }

    public String[] getRoles() {
        return this.roles;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * @return the constraint
     */
    public String getConstraint() {
        return this.constraint;
    }

    /**
     * <p>
     * Checks if the resources requires authentication.
     * </p>
     *
     * @return
     */
    public boolean requiresAuthentication() {
        if (getConstraint().equals(ProtectedResourceConstraint.NOT_PROTECTED.name())) {
            return false;
        }

        return getConstraint().equals(ProtectedResourceConstraint.ALL.name())
                || getConstraint().equals(ProtectedResourceConstraint.AUTHENTICATION.name())
                || getConstraint().equals(ProtectedResourceConstraint.AUTHORIZATION.name());
    }

    /**
     * <p>
     * Checks if the resource requires authorization.
     * </p>
     *
     * @return
     */
    public boolean requiresAuthorization() {
        if (getConstraint().equals(ProtectedResourceConstraint.NOT_PROTECTED.name())) {
            return false;
        }

        return getConstraint().equals(ProtectedResourceConstraint.ALL.name())
                || getConstraint().equals(ProtectedResourceConstraint.AUTHORIZATION.name());
    }

    /**
     * <p>
     * Checks if the provided URI matches the pattern defined for this resource.
     * </p>
     *
     * @param uri
     * @return
     */
    public boolean matches(String uri) {
        if (getPattern().equals(ANY_RESOURCE_PATTERN)) {
            return true;
        }

        if (getPattern().equals(uri)) {
            return true;
        }

        if (getPattern().endsWith(ANY_RESOURCE_PATTERN)) {
            String formattedPattern = getPattern().replaceAll("/[*]", "/");

            if (uri.contains(formattedPattern)) {
                return true;
            }
        }

        if (pattern.equals("*")) {
            return true;
        } else {
            return (pattern.startsWith(ANY_RESOURCE_PATTERN) && uri.endsWith(pattern.substring(
                    ANY_RESOURCE_PATTERN.length() + 1, pattern.length())));
        }
    }

    public boolean isAllowed(UserContext subject) {
        boolean isRoleAllowed = false;

        if (this.roles != null && this.roles.length > 0) {
            for (String role : this.roles) {
                if (subject.hasRole(role)) {
                    isRoleAllowed = true;
                    break;
                }
            }
        } else {
            isRoleAllowed = true;
        }

        boolean isGroupAllowed = false;

        if (this.groups != null && this.groups.length > 0) {
            for (String group : this.groups) {
                if (subject.hasGroup(group)) {
                    isGroupAllowed = true;
                    break;
                }
            }
        } else {
            isGroupAllowed = true;
        }

        return isGroupAllowed && isRoleAllowed;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public void setConstraint(ProtectedResourceConstraint constraint) {
        this.constraint = constraint.name();
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
}
