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

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.credential.OTPCredential;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.TimeBasedOTP;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;
import org.picketlink.idm.IdentityManager;
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
        
        String firstOTP = generateOTP(identityManager);
        
        authenticatingUser.setCredential(new OTPCredential("admin", "admin", firstOTP));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        assertRoles(authenticatedUser);
        assertGroups(authenticatedUser);
        
        picketBoxManager.logout(authenticatedUser);
        
        Thread.sleep(30000);
        
        String secondOTP = generateOTP(identityManager);
        
        assertFalse(firstOTP.equals(secondOTP));
        
        authenticatingUser.setCredential(new OTPCredential("admin", "admin", secondOTP));
        
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
    public void testInvalidOTPAuthentication() throws Exception {
        PicketBoxManager picketBoxManager = createManager();
        IdentityManager identityManager = picketBoxManager.getIdentityManager();
        
        UserContext authenticatingUser = new UserContext();
        
        String firstOTP = generateOTP(identityManager);
        
        authenticatingUser.setCredential(new OTPCredential("admin", "admin", firstOTP));

        // let's authenticate the user
        UserContext authenticatedUser = picketBoxManager.authenticate(authenticatingUser);

        assertNotNull(authenticatedUser);
        assertTrue(authenticatedUser.isAuthenticated());
        
        picketBoxManager.logout(authenticatedUser);
        
        Thread.sleep(30000);
        
        authenticatingUser.setCredential(new OTPCredential("admin", "admin", firstOTP));
        
        authenticatedUser = picketBoxManager.authenticate(authenticatingUser);
        
        assertNotNull(authenticatedUser);
        assertFalse(authenticatedUser.isAuthenticated());
    }

    private String generateOTP(IdentityManager identityManager) throws GeneralSecurityException {
        String serialNumber;
        User idmuser = identityManager.getUser("admin");
        serialNumber = idmuser.getAttribute("serial");
        
        if(serialNumber == null){
            //Generate serial number
            serialNumber = UUID.randomUUID().toString();
            serialNumber = serialNumber.replace('-', 'c');
            
            //Just pick the first 10 characters
            serialNumber = serialNumber.substring(0, 10);
            
            serialNumber = toHexString(serialNumber.getBytes());
            idmuser.setAttribute("serial", serialNumber);
        }
        
        return TimeBasedOTP.generateTOTP(serialNumber, 6);
    }
    
    private String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }
}