/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.helper;

import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic Helper to build oft-used objects across various other helpers
 *
 * @author bramak09
 */
public final class GenericHelper {

    public static final String SETTINGS_FILE_NAME = "callflows-settings.json";

    // private constructor
    private GenericHelper() {
    }

    public static Map<String, String> buildServicesMap() {
        Map<String, String> servicesMap = new HashMap<>();
        servicesMap.put(Constants.CONFIG_SRVC_CALL, Constants.CONFIG_SRVC_CALL_BEAN_NAME);
        servicesMap.put(Constants.CONFIG_PERSON_SERV, Constants.CONFIG_PERSON_SERV_BEAN_NAME);
        return servicesMap;
    }

    public static Map<String, String> buildTestUsersMap(String phone, String url) {
        // Test Users map
        Map<String, String> testUsersMap = new HashMap<>();
        testUsersMap.put(phone, url);
        return testUsersMap;
    }

    public static Settings createSettings() {
        Settings settings = new Settings();
        settings.setConfigs(ConfigHelper.createConfigs());
        settings.setRenderers(RendererHelper.createRenderers());
        return settings;
    }

}

