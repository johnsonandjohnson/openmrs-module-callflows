package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration Helper
 *
 * @author bramak09
 */
public final class ConfigHelper {

    public static final String CONFIG_FILE_PATH = "/com.janssen.connectforlife.callflows/raw/callflows-configs.json";

    public static final String CONFIG_FILE_NAME = "callflows-configs.json";

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
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setRenderersMap(GenericHelper.buildRenderersMap());
        voxeo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER,
                                                              Constants.CONFIG_VOXEO_USER_URL));

        Config yo = new Config();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setRenderersMap(GenericHelper.buildRenderersMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        configs.add(voxeo);
        configs.add(yo);
        return configs;
    }

}
