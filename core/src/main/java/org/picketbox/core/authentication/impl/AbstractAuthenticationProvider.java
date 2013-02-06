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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.credential.UserCredential;

/**
 * <p>
 * Base class for {@link AuthenticationProvider} implementations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    private final Map<Class<? extends UserCredential>, List<AuthenticationMechanism>> mechanisms = new HashMap<Class<? extends UserCredential>, List<AuthenticationMechanism>>();
    private final PicketBoxManager picketboxManager;

    public AbstractAuthenticationProvider(PicketBoxManager picketBoxManager) {
        this.picketboxManager = picketBoxManager;
        initMechanisms(this.picketboxManager.getConfiguration().getAuthentication().getMechanisms());
    }

    private void initMechanisms(List<AuthenticationMechanism> providedMechanisms) {
        for (AuthenticationMechanism authenticationMechanism : providedMechanisms) {

            if (authenticationMechanism instanceof AbstractAuthenticationMechanism) {
                ((AbstractAuthenticationMechanism) authenticationMechanism).setPicketBoxManager(this.picketboxManager);
            }

            List<AuthenticationInfo> mechanismInfos = authenticationMechanism.getAuthenticationInfo();

            for (AuthenticationInfo info : mechanismInfos) {
                Class<? extends UserCredential> supportedCredential = info.getSupportedCredentials();

                List<AuthenticationMechanism> supportedMechanisms = this.mechanisms.get(supportedCredential);

                if (supportedMechanisms == null) {
                    supportedMechanisms = new ArrayList<AuthenticationMechanism>();
                    this.mechanisms.put(supportedCredential, supportedMechanisms);
                }

                supportedMechanisms.add(authenticationMechanism);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#getSupportedMechanisms()
     */
    @Override
    public String[] getSupportedMechanisms() {
        List<AuthenticationMechanism> supportedMechanisms = getAllMechanisms();

        String[] mechanisms = new String[supportedMechanisms.size()];

        int i = 0;

        for (AuthenticationMechanism entry : supportedMechanisms) {
            mechanisms[i++] = entry.getClass().getName();
        }

        return mechanisms;
    }

    @Override
    public List<AuthenticationMechanism> getMechanisms(UserCredential credential) {
        return this.mechanisms.get(credential.getClass());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#supports(java.lang.String)
     */
    @Override
    public boolean supports(String mechanismName) {
        for (String supportedMechanismName : getSupportedMechanisms()) {
            if (supportedMechanismName.equals(mechanismName)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#getMechanism(java.lang.String)
     */
    @Override
    public AuthenticationMechanism getMechanism(String mechanismName) {
        for (AuthenticationMechanism currentMechanism : getAllMechanisms()) {
            if (currentMechanism.getClass().getName().equals(mechanismName)) {
                return currentMechanism;
            }
        }

        return null;
    }

    private List<AuthenticationMechanism> getAllMechanisms() {
        Set<Entry<Class<? extends UserCredential>, List<AuthenticationMechanism>>> entrySet = this.mechanisms.entrySet();

        List<AuthenticationMechanism> supportedMechanisms = new ArrayList<AuthenticationMechanism>();

        for (Entry<Class<? extends UserCredential>, List<AuthenticationMechanism>> entry : entrySet) {
            supportedMechanisms.addAll(entry.getValue());
        }

        return supportedMechanisms;
    }

}