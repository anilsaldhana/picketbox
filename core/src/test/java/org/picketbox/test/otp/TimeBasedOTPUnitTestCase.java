/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.picketbox.test.otp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.picketbox.core.util.TimeBasedOTP;
import org.picketbox.core.util.TimeBasedOTPUtil;

/**
 * Unit test the {@code TimeBasedOTP} utility
 *
 * @author anil saldhana
 * @since Sep 20, 2010
 */
public class TimeBasedOTPUnitTestCase {
    /**
     * | Time (sec) | UTC Time | Value of T (hex) | TOTP | Mode |
     * +------------+---------------+------------------+----------+--------+ | 59 | 1970-01-01 | 0000000000000001 | 94287082 |
     * SHA1 | | | 00:00:59 | | | | | 59 | 1970-01-01 | 0000000000000001 | 32247374 | SHA256 | | | 00:00:59 | | | | | 59 |
     * 1970-01-01 | 0000000000000001 | 69342147 | SHA512 | | | 00:00:59 | | | | | 1111111109 | 2005-03-18 | 00000000023523EC |
     * 07081804 | SHA1 | | | 01:58:29 | | | | | 1111111109 | 2005-03-18 | 00000000023523EC | 34756375 | SHA256 | | | 01:58:29 |
     * | | | | 1111111109 | 2005-03-18 | 00000000023523EC | 63049338 | SHA512 | | | 01:58:29 | | | | | 1111111111 | 2005-03-18 |
     * 00000000023523ED | 14050471 | SHA1 | | | 01:58:31 | | | | | 1111111111 | 2005-03-18 | 00000000023523ED | 74584430 |
     * SHA256 | | | 01:58:31 | | | | | 1111111111 | 2005-03-18 | 00000000023523ED | 54380122 | SHA512 | | | 01:58:31 | | | | |
     * 1234567890 | 2009-02-13 | 000000000273EF07 | 89005924 | SHA1 | | | 23:31:30 | | | | | 1234567890 | 2009-02-13 |
     * 000000000273EF07 | 42829826 | SHA256 | | | 23:31:30 | | | | | 1234567890 | 2009-02-13 | 000000000273EF07 | 76671578 |
     * SHA512 | | | 23:31:30 | | | | | 2000000000 | 2033-05-18 | 0000000003F940AA | 69279037 | SHA1 | | | 03:33:20 | | | | |
     * 2000000000 | 2033-05-18 | 0000000003F940AA | 78428693 | SHA256 | | | 03:33:20 | | | | | 2000000000 | 2033-05-18 |
     * 0000000003F940AA | 56464532 | SHA512 | | | 03:33:20 | | | |
     * +------------+---------------+------------------+----------+--------+
     */

    String seed = "mysecret";
    long T0 = 0;
    long X = 30;
    long testTime[] = { 59, 1111111109, 1111111111, 1234567890, 2000000000 };

    String steps = "0";

    String[] totp = new String[] { "37476548", "78869392", "78100410", "76693410", "21071274", "05513923", "61602741",
            "43570590", "51045921", "73362629", "93902831", "36270882", "97073471", "99012614", "21156782" };

    int NUMBER_OF_DIGITS = 8;

    int SLEEP_TIME = 2;

    @Test
    public void testTOTP() throws Exception {
        int totpIndex = -1;

        for (int i = 0; i < this.testTime.length; i++) {
            long T = (this.testTime[i] - this.T0) / this.X;
            this.steps = Long.toHexString(T).toUpperCase();

            // Just get a 16 digit string
            while (this.steps.length() < 16)
                this.steps = "0" + this.steps;

            assertEquals(this.totp[++totpIndex], TimeBasedOTP.generateTOTP(this.seed, this.steps, this.NUMBER_OF_DIGITS, "HmacSHA1"));
            assertEquals(this.totp[++totpIndex], TimeBasedOTP.generateTOTP(this.seed, this.steps, this.NUMBER_OF_DIGITS, "HmacSHA256"));
            assertEquals(this.totp[++totpIndex], TimeBasedOTP.generateTOTP(this.seed, this.steps, this.NUMBER_OF_DIGITS, "HmacSHA512"));
        }
    }

    @Test
    @Ignore
    public void testTOTPValidity() throws Exception {
        String totp = TimeBasedOTP.generateTOTP(this.seed, this.NUMBER_OF_DIGITS);

        System.out.println("We are going to sleep for " + this.SLEEP_TIME + " secs");
        Thread.sleep(this.SLEEP_TIME * 5000); // 10 secs

        assertTrue("TOTP validated", TimeBasedOTPUtil.validate(totp, this.seed.getBytes(), this.NUMBER_OF_DIGITS));
    }
    
}