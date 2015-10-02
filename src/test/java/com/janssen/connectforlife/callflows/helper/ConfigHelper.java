package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration Helper
 *
 * @author bramak09
 */
public final class ConfigHelper {

    // private, hence can't be constructed
    private ConfigHelper() {
    }

    public static List<Config> createConfigs() {

        // configuration
        List<Config> configs = new ArrayList<>();

        Config voxeo = new Config();
        voxeo.setName(Constants.CONFIG_VOXEO);
        voxeo.setOutgoingCallUriTemplate(Constants.CONFIG_VOXEO_OUT_TEMPLATE);
        voxeo.setOutgoingCallMethod(Constants.CONFIG_VOXEO_METHOD);
        voxeo.setOutboundCallLimit(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT);
        voxeo.setOutboundCallRetryAttempts(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        voxeo.setOutboundCallRetrySeconds(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS);
        voxeo.setCallAllowed(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL);
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setTestUsersMap(
                GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER, Constants.CONFIG_VOXEO_USER_URL));

        Config yo = new Config();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setOutboundCallLimit(Constants.CONFIG_YO_OUTBOUND_CALL_LIMIT);
        yo.setOutboundCallRetryAttempts(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        yo.setOutboundCallRetrySeconds(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS);
        yo.setCallAllowed(Constants.CONFIG_YO_CAN_PLACE_OUTBOUND_CALL);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        configs.add(voxeo);
        configs.add(yo);
        return configs;
    }

    public static List<ConfigContract> createConfigContracts() {

        // configuration
        List<ConfigContract> contracts = new ArrayList<>();

        ConfigContract voxeo = new ConfigContract();
        voxeo.setName(Constants.CONFIG_VOXEO);
        voxeo.setOutgoingCallUriTemplate(Constants.CONFIG_VOXEO_OUT_TEMPLATE);
        voxeo.setOutgoingCallMethod(Constants.CONFIG_VOXEO_METHOD);
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER,
                                                              Constants.CONFIG_VOXEO_USER_URL));

        ConfigContract yo = new ConfigContract();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        contracts.add(voxeo);
        contracts.add(yo);
        return contracts;
    }


}
