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
        // OSGI Services
        Map<String, String> servicesMap = new HashMap<>();
        servicesMap.put(Constants.CONFIG_SRVC_CALL, Constants.CONFIG_SRVC_CALL_CLASS);
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

