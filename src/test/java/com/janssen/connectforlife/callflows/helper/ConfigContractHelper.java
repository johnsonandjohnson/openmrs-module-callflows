package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.ConfigContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to create config contracts
 *
 * @author bramak09
 */
public final class ConfigContractHelper {

    // private constructor
    private ConfigContractHelper() {
    }

    public static List<ConfigContract> createConfigContracts() {

        // configuration
        List<ConfigContract> contracts = new ArrayList<>();

        ConfigContract voxeo = new ConfigContract();
        voxeo.setName(Constants.CONFIG_VOXEO);
        voxeo.setOutgoingCallUriTemplate(Constants.CONFIG_VOXEO_OUT_TEMPLATE);
        voxeo.setOutgoingCallMethod(Constants.CONFIG_VOXEO_METHOD);
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setRenderersMap(GenericHelper.buildRenderersContractMap());
        voxeo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER,
                                                              Constants.CONFIG_VOXEO_USER_URL));

        ConfigContract yo = new ConfigContract();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setRenderersMap(GenericHelper.buildRenderersContractMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        contracts.add(voxeo);
        contracts.add(yo);
        return contracts;
    }


}
