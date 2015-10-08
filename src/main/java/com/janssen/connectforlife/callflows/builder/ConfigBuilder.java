package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;

import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Config Domain Builder
 *
 * @author bramak09
 */
@Component
public class ConfigBuilder {

    /**
     * Create a Config domain object from a ConfigContract object
     *
     * @param configContract the object to convert
     * @return a new Config object
     */
    public Config createFrom(ConfigContract configContract) {
        Config config = new Config();
        config.setName(configContract.getName());
        config.setOutgoingCallMethod(configContract.getOutgoingCallMethod());
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
}

