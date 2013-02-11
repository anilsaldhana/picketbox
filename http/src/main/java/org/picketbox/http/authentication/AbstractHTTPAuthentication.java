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
package org.picketbox.http.authentication;

import static org.picketbox.core.PicketBoxMessages.MESSAGES;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.credential.UserCredential;
import org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.http.DefaultPicketBoxHTTPManager;
import org.picketbox.http.authentication.credential.HttpServletCredential;
import org.picketbox.http.config.HTTPAuthenticationConfiguration;

/**
 * Base class for all the HTTP authentication schemes
 *
 * @author anil saldhana
 * @since Jul 6, 2012
 */
public abstract class AbstractHTTPAuthentication extends AbstractAuthenticationMechanism {

    private RequestCache requestCache = new RequestCache();

    public static final String DEFAULT_REALM = "PicketBox Realm";

    /**
     * Injectable realm name
     */
    protected String realmName = DEFAULT_REALM;

    private static final String DEFAULT_PAGE_URL = "/";

    /**
     * The page used to redirect the user after a succesful authentication.
     */
    protected String defaultPage = DEFAULT_PAGE_URL;

    /**
     * The FORM login page. It should always start with a '/'
     */
    protected String formAuthPage = "/login.jsp";

    /**
     * The FORM error page. It should always start with a '/'
     */
    protected String formErrorPage = "/error.jsp";

    /**
     * The FORM login page. It should always start with a '/'
     */
    public void setFormAuthPage(String formAuthPage) {
        this.formAuthPage = formAuthPage;
    }

    /**
     * The FORM error page. It should always start with a '/'
     */
    public void setFormErrorPage(String formErrorPage) {
        this.formErrorPage = formErrorPage;
    }

    /**
     * The default page. It should always start with a '/'
     */
    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    public String getRealmName() {
        return this.realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        if (!(credential instanceof HttpServletCredential)) {
            throw PicketBoxMessages.MESSAGES.unexpectedCredentialType(credential, HttpServletCredential.class);
        }

        HttpServletCredential httpCredential = (HttpServletCredential) credential;

        HttpServletRequest request = httpCredential.getRequest();
        HttpServletResponse response = httpCredential.getResponse();

        UserContext subject = getPicketBoxManager().getUserContext(request);

        if (subject != null && subject.isAuthenticated()) {
            return subject.getPrincipal();
        }

        boolean jSecurityCheck = isAuthenticationRequest(request);

        if (!jSecurityCheck) {
            if (getPicketBoxManager().requiresAuthentication(request, response)) {
                this.requestCache.saveRequest(request);
                result.setStatus(AuthenticationStatus.CONTINUE);
                challengeClient(request, response);
            }

            return null;
        }

        Principal authenticatedPrincipal = performAuthentication(httpCredential);

        if (authenticatedPrincipal == null) {
            result.setStatus(AuthenticationStatus.INVALID_CREDENTIALS);
        }

        return authenticatedPrincipal;
    }

    /**
     * <p>Sub-classes should override this method to check if the specified {@link HttpServletRequest} requires authentication.</p>
     *
     * @param request
     * @return
     */
    protected abstract boolean isAuthenticationRequest(HttpServletRequest request);

    /**
     * <p>Performs the authentication workflow.</p>
     *
     * @param credential
     * @return
     * @throws AuthenticationException
     */
    private Principal performAuthentication(HttpServletCredential credential) throws AuthenticationException {
        Principal principal = doHTTPAuthentication(credential);

        HttpServletRequest request = credential.getRequest();
        HttpServletResponse response = credential.getResponse();

        if (principal == null) {
            sendErrorPage(request, response);
            return null;
        }

        if (principal != null) {
            // remove from the cache the saved request and store it in the session for further use.
            SavedRequest savedRequest = this.requestCache.removeAndStoreSavedRequestInSession(request);
            String requestedURI = null;

            if (savedRequest != null) {
                requestedURI = savedRequest.getRequestURI();
            }

            // if the user has explicit defined a default page url, use it to redirect the user after a successful
            // authentication.
            if (!getDefaultPage().equals(DEFAULT_PAGE_URL) || requestedURI == null) {
                requestedURI = request.getContextPath() + getDefaultPage();
            }

            sendRedirect(response, requestedURI);
        }

        return principal;
    }

    /**
     * <p>Sub-classes should override this method to provide the specific implementation for a given authentication mechanism.</p>
     *
     * @param credential
     * @return
     */
    protected abstract Principal doHTTPAuthentication(HttpServletCredential credential);

    /**
     * <p>Sub-classes should override this method to provide how a authentication challenge is sent to the client.</p>
     *
     * @param request
     * @param response
     * @throws AuthenticationException
     */
    protected abstract void challengeClient(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException;

    /**
     * <p>Sub-classes can override this method to provide how to send users to a error page.</p>
     *
     * @param request
     * @param response
     * @throws AuthenticationException
     */
    protected void sendErrorPage(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        sendRedirect(response, request.getContextPath() + getFormErrorPage());
    }

    protected void sendRedirect(HttpServletResponse response, String redirectUrl) throws AuthenticationException {
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw MESSAGES.failRedirectToDefaultPage(redirectUrl, e);
        }
    }

    protected void forwardLoginPage(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        RequestDispatcher rd = request.getServletContext().getRequestDispatcher(getFormAuthPage());
        if (rd == null)
            throw MESSAGES.unableToFindRequestDispatcher();

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism#getPicketBoxManager()
     */
    @Override
    protected DefaultPicketBoxHTTPManager getPicketBoxManager() {
        return (DefaultPicketBoxHTTPManager) super.getPicketBoxManager();
    }

    protected HTTPAuthenticationConfiguration getAuthenticationConfig() {
        return (HTTPAuthenticationConfiguration) getPicketBoxManager().getConfiguration().getAuthentication();
    }

    public String getDefaultPage() {
        return this.defaultPage;
    }

    public String getFormAuthPage() {
        return this.formAuthPage;
    }

    public String getFormErrorPage() {
        return this.formErrorPage;
    }
}