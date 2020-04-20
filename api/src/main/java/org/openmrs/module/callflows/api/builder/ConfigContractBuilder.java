package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Config Contract Builder
 *
 * @author bramak09
 */
@Component
public class ConfigContractBuilder {

    /**
     * Create a ConfigContract for use from Config domain
     *
     * @param config the domain
     * @return a config contract
     */
    public ConfigContract createFrom(Config config) {
        ConfigContract configContract = new ConfigContract();
        configContract.setName(config.getName());
        configContract.setOutgoingCallMethod(config.getOutgoingCallMethod());
        configContract.setHasAuthRequired(config.getHasAuthRequired());
        configContract.setOutgoingCallPostParams(config.getOutgoingCallPostParams());

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.putAll(config.getOutgoingCallPostHeadersMap());
        configContract.setOutgoingCallPostHeadersMap(headersMap);

        configContract.setOutgoingCallUriTemplate(config.getOutgoingCallUriTemplate());
        configContract.setOutboundCallLimit(config.getOutboundCallLimit());
        configContract.setOutboundCallRetryAttempts(config.getOutboundCallRetryAttempts());
        configContract.setOutboundCallRetrySeconds(config.getOutboundCallRetrySeconds());
        configContract.setCallAllowed(config.getCallAllowed());

        Map<String, String> testUsersMap = new LinkedHashMap<>();
        testUsersMap.putAll(config.getTestUsersMap());
        configContract.setTestUsersMap(testUsersMap);

        Map<String, String> servicesMap = new LinkedHashMap<>();
        servicesMap.putAll(config.getServicesMap());
        configContract.setServicesMap(servicesMap);

        return configContract;
    }
}
