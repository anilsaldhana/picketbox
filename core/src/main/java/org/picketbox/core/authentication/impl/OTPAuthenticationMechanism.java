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

package org.picketbox.core.authentication.impl;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.OTPCredential;
import org.picketbox.core.authentication.credential.UserCredential;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.TimeBasedOTP;
import org.picketbox.core.util.TimeBasedOTPUtil;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.User;

/**
 * An authentication mechanism for OTP SignIn
 *
 * @author Anil Saldhana
 * @author Pedro Silva
 */
public class OTPAuthenticationMechanism extends AbstractAuthenticationMechanism {

    private String algorithm = TimeBasedOTP.HMAC_SHA1;

    // This is the number of digits in the totp
    private int NUMBER_OF_DIGITS = 6;

    public OTPAuthenticationMechanism() {
    }

    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        ArrayList<AuthenticationInfo> info = new ArrayList<AuthenticationInfo>();

        info.add(new AuthenticationInfo("OTP Authentication", "Provides OTP authentication.", OTPCredential.class));

        return info;
    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        OTPCredential otpCredential = (OTPCredential) credential;
        UsernamePasswordCredentials passwordCredential = (UsernamePasswordCredentials) otpCredential.getCredential();

        String username = otpCredential.getUserName();
        String otp = otpCredential.getOtp();

        Principal principal = null;

        User user = getIdentityManager().getUser(username);

        if (user != null) {
            getIdentityManager().validateCredentials(passwordCredential);

            boolean validation = passwordCredential.getStatus().equals(Status.VALID);

            if (validation) {
                // Validate OTP
                Attribute<Serializable> secretAttribute = user.getAttribute("serial");
                String seed = null;

                if (secretAttribute != null) {
                    seed = secretAttribute.getValue().toString();
                }

                if (seed != null) {
                    try {
                        if (this.algorithm.equals(TimeBasedOTP.HMAC_SHA1)) {
                            validation = TimeBasedOTPUtil.validate(otp, seed.getBytes(), this.NUMBER_OF_DIGITS);
                        } else if (this.algorithm.equals(TimeBasedOTP.HMAC_SHA256)) {
                            validation = TimeBasedOTPUtil.validate256(otp, seed.getBytes(), this.NUMBER_OF_DIGITS);
                        } else if (this.algorithm.equals(TimeBasedOTP.HMAC_SHA512)) {
                            validation = TimeBasedOTPUtil.validate512(otp, seed.getBytes(), this.NUMBER_OF_DIGITS);
                        }
                    } catch (GeneralSecurityException e) {
                        throw new AuthenticationException(e);
                    }
                } else {
                    validation = false;
                    authenticationFailed(result);
                    result.addMessage("User does not have a seed. OTP tokens could not be derived.");
                }
            }

            if (validation) {
                principal = new PicketBoxPrincipal(username);
            } else {
                invalidCredentials(result);
            }
        }

        return principal;
    }
}