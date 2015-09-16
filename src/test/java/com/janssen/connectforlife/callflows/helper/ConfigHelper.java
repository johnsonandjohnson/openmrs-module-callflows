package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        voxeo.setServicesMap(buildServicesMap());
        voxeo.setRenderersMap(buildRenderersMap());
        voxeo.setTestUsersMap(buildTestUsersMap(Constants.CONFIG_VOXEO_USER, Constants.CONFIG_VOXEO_USER_URL));

        Config yo = new Config();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setServicesMap(buildServicesMap());
        yo.setRenderersMap(buildRenderersMap());
        yo.setTestUsersMap(buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        configs.add(voxeo);
        configs.add(yo);
        return configs;
    }

    private static Map<String, String> buildServicesMap() {
        // OSGI Services
        Map<String, String> servicesMap = new HashMap<>();
        servicesMap.put(Constants.CONFIG_SRVC_PATIENT, Constants.CONFIG_SRVC_PATIENT_CLASS);
        servicesMap.put(Constants.CONFIG_SRVC_HEALTHTIP, Constants.CONFIG_SRVC_HEALTHTIP_CLASS);
        return servicesMap;
    }

    private static Map<String, Renderer> buildRenderersMap() {
        // Renderers
        Renderer vxmlRenderer = new Renderer();
        vxmlRenderer.setName(Constants.CONFIG_RENDERER_VXML);
        vxmlRenderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        vxmlRenderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);

        Renderer textRenderer = new Renderer();
        textRenderer.setName(Constants.CONFIG_RENDERER_TXT);
        textRenderer.setMimeType(Constants.CONFIG_RENDERER_TXT_MIME);
        textRenderer.setTemplate(Constants.CONFIG_RENDERER_TXT_TPL);

        // Renderer Map
        Map<String, Renderer> rendererMap = new HashMap<>();
        rendererMap.put(Constants.CONFIG_RENDERER_VXML, vxmlRenderer);
        rendererMap.put(Constants.CONFIG_RENDERER_TXT, textRenderer);
        return rendererMap;
    }

    private static Map<String, String> buildTestUsersMap(String phone, String url) {
        // Test Users map
        Map<String, String> testUsersMap = new HashMap<>();
        testUsersMap.put(phone, url);
        return testUsersMap;
    }

}
