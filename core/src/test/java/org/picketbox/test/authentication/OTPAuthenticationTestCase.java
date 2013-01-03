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

package org.picketbox.test.authentication;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.security.GeneralSecurityException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.credential.OTPCredential;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.TimeBasedOTP;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.User;

/**
 * <p>
 * Tests the different ways to authenticate users using a {@link UserCredential} instance..
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class OTPAuthenticationTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests if the authentication performs successfully when provided a valid {@link OTPCredential}.
     * </p>
     * @throws Exception
     *
     * @throws AuthenticationException
     */
    @Test
    public void testSuccessfulAuthentication() throws Exception {
        PicketBoxManager picketBoxManager = createManager();
        IdentityManager identityManager = picketBoxManager.getIdentityManager();

        UserContext authenticatingUser = new UserContext();

        String token = generateOTP(identityManager);
        String userName = "admin";
        String password = "admin";

        UserCredential credential = new OTPCredential(userName, password, token);

        authenticatingUser.setCredential(credential);

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);

        picketBoxManager.logout(authenticatedUser);

        Thread.sleep(30000);

        String secondOTP = generateOTP(identityManager);

        assertFalse(token.equals(secondOTP));

        authenticatingUser.setCredential(new OTPCredential(userName, userName, secondOTP));

        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);
    }

    /**
     * <p>
     * Tests if the authentication fail when using the same token twice.
     * </p>
     * @throws Exception
     *
     * @throws AuthenticationException
     */
    @Test
    @Ignore
    public void testInvalidOTPAuthentication() throws Exception {
        PicketBoxManager picketBoxManager = createManager();
        IdentityManager identityManager = picketBoxManager.getIdentityManager();

        UserContext authenticatingUser = new UserContext();

        String firstOTP = generateOTP(identityManager);

        authenticatingUser.setCredential(new OTPCredential("admin", "admin", firstOTP));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        Assert.assertTrue(authenticatedUser.isAuthenticated());

        picketBoxManager.logout(authenticatedUser);

        Thread.sleep(30000);

        authenticatingUser.setCredential(new OTPCredential("admin", "admin", firstOTP));

        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Tests if the authentication fail when using a null token.
     * </p>
     * @throws Exception
     *
     * @throws AuthenticationException
     */
    @Test
    public void testNullToken() throws Exception {
        PicketBoxManager picketBoxManager = createManager();
        IdentityManager identityManager = picketBoxManager.getIdentityManager();

        UserContext authenticatingUser = new UserContext();

        // only sets the seed as an user attribute.
        generateOTP(identityManager);

        authenticatingUser.setCredential(new OTPCredential("admin", "admin", null));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    /**
     * <p>
     * Tests if the authentication fail when authenticating an user without a seed.
     * </p>
     * @throws Exception
     *
     * @throws AuthenticationException
     */
    @Test
    public void testUserAuthenticationWithoutSeed() throws Exception {
        PicketBoxManager picketBoxManager = createManager();

        UserContext authenticatingUser = new UserContext();

        authenticatingUser.setCredential(new OTPCredential("admin", "admin", null));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
        assertFalse(authenticatedUser.getAuthenticationResult().getMessages().isEmpty());
    }

    private String generateOTP(IdentityManager identityManager) throws GeneralSecurityException {
        Attribute<String> serialNumber;
        User idmuser = identityManager.getUser("admin");
        serialNumber = idmuser.getAttribute("serial");

        if(serialNumber == null){
            String serial = null;
            
            //Generate serial number
            serial = UUID.randomUUID().toString();
            serial = serial.replace('-', 'c');

            //Just pick the first 10 characters
            serial = serial.substring(0, 10);

            serial = toHexString(serial.getBytes());
            
            serialNumber = new Attribute<String>("serial", serial);
            
            idmuser.setAttribute(serialNumber);
            
            identityManager.update(idmuser);
        }

        return TimeBasedOTP.generateTOTP(serialNumber.getValue(), 6);
    }

    private String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }
}