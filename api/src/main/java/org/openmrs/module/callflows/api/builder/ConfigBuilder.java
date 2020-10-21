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
