package org.openmrs.module.callflows.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.springframework.core.io.ByteArrayResource;

public final class SettingsManagerUtil {

	public static SettingsManagerService getSettingsManagerService() {
		return Context.getRegisteredComponent("cf.settings.manager", SettingsManagerService.class);
	}

	public static void loadDefaultConfigurationFromResources(String filename) {
		String defaultConfiguration = ResourceUtil.readResourceFile(filename);
		ByteArrayResource resource = new ByteArrayResource(defaultConfiguration.getBytes());
		getSettingsManagerService().saveRawConfig(filename, resource);
	}

	public static boolean configurationNotExist(String filename) {
		return !getSettingsManagerService().configurationExist(filename);
	}

	public static void loadDefaultIfNotExists(String filename) {
		if (configurationNotExist(filename)){
			loadDefaultConfigurationFromResources(filename);
		}
	}

	private SettingsManagerUtil() { }
}
