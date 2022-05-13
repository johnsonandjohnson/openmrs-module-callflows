/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Config Domain Builder
 *
 * @author bramak09
 */
public final class ConfigBuilder {

    /**
     * Create a Config domain object from a ConfigContract object
     *
     * @param configContract the object to convert
     * @return a new Config object
     */
    public static Config createFrom(ConfigContract configContract) {
        Config config = new Config();
        config.setName(configContract.getName());
        config.setOutgoingCallMethod(configContract.getOutgoingCallMethod());
        config.setHasAuthRequired(configContract.getHasAuthRequired());
        config.setOutgoingCallPostParams(configContract.getOutgoingCallPostParams());

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.putAll(configContract.getOutgoingCallPostHeadersMap());
        config.setOutgoingCallPostHeadersMap(headersMap);

        config.setOutgoingCallUriTemplate(configContract.getOutgoingCallUriTemplate());
        config.setOutboundCallLimit(configContract.getOutboundCallLimit());
        config.setOutboundCallRetryAttempts(configContract.getOutboundCallRetryAttempts());
        config.setOutboundCallRetrySeconds(configContract.getOutboundCallRetrySeconds());
        config.setCallAllowed(configContract.isCallAllowed());

        Map<String, String> testUsersMap = new LinkedHashMap<>();
        testUsersMap.putAll(configContract.getTestUsersMap());
        config.setTestUsersMap(testUsersMap);

        Map<String, String> servicesMap = new LinkedHashMap<>();
        servicesMap.putAll(configContract.getServicesMap());
        config.setServicesMap(servicesMap);

        return config;
    }

    private ConfigBuilder() {
    }
}
