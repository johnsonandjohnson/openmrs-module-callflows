/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

/**
 * Service to manage settings and configurations.
 */
public interface SettingsManagerService extends OpenmrsService {

    /**
     * Saves the raw configurations.
     *
     * @param configFileName Configuration file name
     * @param resource Byte Array resource
     */
    void saveRawConfig(String configFileName, ByteArrayResource resource);

    /**
     * Gets the Raw configuration.
     *
     * @param configFileName Configuration file name
     * @throws CallFlowRuntimeException if there is an error loading a file
     */
    InputStream getRawConfig(String configFileName);

    /**
     * Checks if Configuration exist.
     *
     * @param configurationFileName Configuration file name
     */
    boolean configurationExist(String configurationFileName);

}
