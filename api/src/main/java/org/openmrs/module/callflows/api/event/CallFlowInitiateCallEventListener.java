/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.util.CallFlowEventSubjectConstants;

import java.util.Map;

/**
 * Initiate Call Event Listener
 *
 * @author bramak09
 */
public class CallFlowInitiateCallEventListener extends AbstractCallFlowEventListener {

    private static final Log LOGGER = LogFactory.getLog(CallFlowInitiateCallEventListener.class);

    /**
     * Get the Subject
     *
     * @return is a subject string
     */
    @Override
    public String getSubject() {
        return CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL;
    }

    /**
     * Handle the event
     *
     * @param properties is a map
     */
    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.info(String.format("Handling outbound call event %s: %s", getSubject(), properties.toString()));
        String config = properties.get(Constants.PARAM_CONFIG).toString();
        String flowName = properties.get(Constants.PARAM_FLOW_NAME).toString();
        Map<String, Object> params = (Map<String, Object>) properties.get(Constants.PARAM_PARAMS);
        getComponent("callflows.callService", CallService.class).makeCall(config, flowName, params);
    }
}
