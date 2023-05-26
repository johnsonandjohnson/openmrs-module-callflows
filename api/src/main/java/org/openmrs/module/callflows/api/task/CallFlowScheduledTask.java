/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Map;

/**
 * CallFlow Scheduled Task
 */
public class CallFlowScheduledTask extends AbstractTask {

    /**
     * Method to Execute the task.
     */
    @Override
    public void execute() {
        Map<String, Object> properties = CallFlowEvent.convertFromTaskDefinitionProperties(getTaskDefinition().getProperties());
        String config = properties.get(Constants.PARAM_CONFIG).toString();
        String flowName = properties.get(Constants.PARAM_FLOW_NAME).toString();
        Map<String, Object> params = (Map<String, Object>) properties.get(Constants.PARAM_PARAMS);
        Context.getRegisteredComponent("callflows.callService", CallService.class).makeCall(config, flowName, params);
    }
}
