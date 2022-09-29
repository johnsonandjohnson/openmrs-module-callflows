/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.openmrs.module.callflows.api.util.CallFlowConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation for SettingsManagerService
 */
public class SettingsManagerServiceImpl extends BaseOpenmrsService implements SettingsManagerService {

    /**
     * Saves the raw configurations.
     *
     * @param configFileName Configuration file name
     * @param resource Byte Array resource
     */
    @Override
    public void saveRawConfig(String configFileName, ByteArrayResource resource) {
        File destinationFile = getDestinationFile(configFileName);
        try (InputStream is = resource.getInputStream(); FileOutputStream fos = new FileOutputStream(destinationFile)) {
            IOUtils.copy(is, fos);
        } catch (IOException e) {
            throw new CallFlowRuntimeException("Error saving file " + configFileName, e);
        }
    }

    /**
     * Gets the Raw configuration.
     *
     * @param configFileName Configuration file name
     * @throws CallFlowRuntimeException if there is an error loading a file
     */
    @Override
    public InputStream getRawConfig(String configFileName) {
        InputStream is = null;
        try {
            File configurationFile = getDestinationFile(configFileName);
            if (configurationFile.exists()) {
                is = new FileInputStream(configurationFile);
            }
        } catch (IOException e) {
            throw new CallFlowRuntimeException("Error loading file " + configFileName, e);
        }
        return is;
    }

    /**
     * Checks if Configuration exist.
     *
     * @param configurationFileName Configuration file name
     */
    @Override
    public boolean configurationExist(String configurationFileName) {
        return getDestinationFile(configurationFileName).exists();
    }

    private File getDestinationFile(String filename) {
        File configFileFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(CallFlowConstants.CONFIG_DIR);
        return new File(configFileFolder, filename);
    }
}
