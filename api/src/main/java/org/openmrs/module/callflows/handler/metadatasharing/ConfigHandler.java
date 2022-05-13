/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.handler.metadatasharing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.service.ConfigService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The ConfigHandler Class is a Handler which exposes {@link Config}s (Providers) to be exported and imported by Metadata
 * Sharing module.
 * <p>
 * Bean configured in moduleApplicationContext.xml
 * </p>
 *
 * @implNote The Metadata Sharing handlers us an ID an UUID to read the objects to import and export.
 * Since Config has no ID or UUID and there is no validation for UUID, these values are generated as follows:
 * the ID is equal to hashCode of the name; the UUID is equal to the name.
 */
@OpenmrsProfile(modules = {"metadatasharing:1.*"})
public class ConfigHandler extends BaseSettingsItemHandler<Config> {

    public ConfigHandler() {
        super("CallFlow Provider", Config.class, Config::getName,
                () -> Context.getService(ConfigService.class).allConfigs());
    }

    @Override
    public Map<String, Object> getProperties(Config provider) {
        return Collections.emptyMap();
    }

    @Override
    public Config saveItem(Config provider) throws DAOException {
        final ConfigService configService = Context.getService(ConfigService.class);
        final List<Config> allProviders = configService.allConfigs();

        final Config savedItem;

        if (configService.hasConfig(provider.getName())) {
            // We trust this is the same object as in allRenderers
            final Config currentState = configService.getConfig(provider.getName());
            currentState.setAuthToken(provider.getAuthToken());
            currentState.setCallAllowed(provider.getCallAllowed());
            currentState.setHasAuthRequired(provider.getHasAuthRequired());
            currentState.setOutboundCallLimit(provider.getOutboundCallLimit());
            currentState.setOutboundCallRetryAttempts(provider.getOutboundCallRetryAttempts());
            currentState.setOutboundCallRetrySeconds(provider.getOutboundCallRetrySeconds());
            currentState.setOutgoingCallMethod(provider.getOutgoingCallMethod());
            currentState.setOutgoingCallPostHeadersMap(provider.getOutgoingCallPostHeadersMap());
            currentState.setOutgoingCallPostParams(provider.getOutgoingCallPostParams());
            currentState.setOutgoingCallUriTemplate(provider.getOutgoingCallUriTemplate());
            currentState.setServicesMap(provider.getServicesMap());
            currentState.setTestUsersMap(provider.getTestUsersMap());
            savedItem = currentState;
        } else {
            allProviders.add(provider);
            savedItem = provider;
        }

        // Writes to file
        configService.updateConfigs(allProviders);
        return savedItem;
    }
}
