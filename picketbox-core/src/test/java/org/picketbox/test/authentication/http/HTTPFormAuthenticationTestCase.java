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
package org.picketbox.test.authentication.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPFormAuthentication;
import org.picketbox.authentication.impl.PropertiesFileBasedAuthenticationManager;
import org.picketbox.test.http.TestServletContext;
import org.picketbox.test.http.TestServletContext.TestRequestDispatcher;
import org.picketbox.test.http.TestServletRequest;
import org.picketbox.test.http.TestServletResponse;

/**
 * Unit test the {@link HTTPFormAuthentication} class
 *
 * @author anil saldhana
 * @since July 9, 2012
 */
public class HTTPFormAuthenticationTestCase {

    private HTTPFormAuthentication httpForm = null;

    private TestServletContext sc = new TestServletContext(new HashMap<String, String>());

    @Before
    public void setup() throws Exception {
        httpForm = new HTTPFormAuthentication();

        httpForm.setAuthManager(new PropertiesFileBasedAuthenticationManager());

        httpForm.setServletContext(sc);
    }

    @Test
    public void testHttpForm() throws Exception {
        TestServletRequest req = new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        TestServletResponse resp = new TestServletResponse(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        });

        req.setMethod("GET");

        // Original URI
        String orig = "http://msite/someurl";

        req.setRequestURI(orig);

        // Call the server to get the digest challenge
        boolean result = httpForm.authenticate(req, resp);
        assertFalse(result);

        // We will test that the request dispatcher is set on the form login page
        TestRequestDispatcher rd = sc.getLast();
        assertEquals(rd.getRequest(), req);

        assertEquals("/login.jsp", rd.getRequestUri());

        // Now assume we have the login page. Lets post
        TestServletRequest newReq = new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
        newReq.setRequestURI("http://msite" + PicketBoxConstants.HTTP_FORM_J_SECURITY_CHECK);
        newReq.setParameter(PicketBoxConstants.HTTP_FORM_J_USERNAME, "Aladdin");
        newReq.setParameter(PicketBoxConstants.HTTP_FORM_J_PASSWORD, "Open Sesame");

        result = httpForm.authenticate(newReq, resp);
        assertTrue(result);

        // After authentication, we should be redirected to the original url
        assertTrue(resp.getSendRedirectedURI().equals(orig));
    }
}