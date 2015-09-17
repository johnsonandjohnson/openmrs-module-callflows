package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RendererContractBuilder rendererContractBuilder;

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
        configContract.setOutgoingCallUriTemplate(config.getOutgoingCallUriTemplate());

        Map<String, String> testUsersMap = new LinkedHashMap<>();
        testUsersMap.putAll(config.getTestUsersMap());
        configContract.setTestUsersMap(testUsersMap);

        Map<String, String> servicesMap = new LinkedHashMap<>();
        servicesMap.putAll(config.getServicesMap());
        configContract.setServicesMap(servicesMap);

        Map<String, RendererContract> rendererMap = new LinkedHashMap<>();
        for (Map.Entry<String, Renderer> rendererEntry : config.getRenderersMap().entrySet()) {
            rendererMap.put(rendererEntry.getKey(), rendererContractBuilder.createFrom(rendererEntry.getValue()));
        }
        configContract.setRenderersMap(rendererMap);

        return configContract;
    }
}

