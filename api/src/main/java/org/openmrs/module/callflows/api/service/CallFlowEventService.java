/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.event.CallFlowEvent;

/**
 * Service to send Event Message
 */
public interface CallFlowEventService extends OpenmrsService {

    /**
     * Send the Event message
     *
     * @param event CallFlow Even
     */
    void sendEventMessage(CallFlowEvent event);
}
