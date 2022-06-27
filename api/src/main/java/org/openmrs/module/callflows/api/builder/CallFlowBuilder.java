/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;

/**
 * CallFlow builder
 *
 * @author bramak09
 */
public final class CallFlowBuilder {

    /**
     * Creates a new Call Flow from a Call flow creation request
     *
     * @param callFlowRequest containing attributes used during call flow creation
     * @return a new CallFlow object
     */
    public static CallFlow createFrom(CallFlowRequest callFlowRequest) {
        return new CallFlow(callFlowRequest.getName(),
                callFlowRequest.getDescription(),
                CallFlowStatus.valueOf(callFlowRequest.getStatus()),
                callFlowRequest.getRaw());
    }

    private CallFlowBuilder() {
    }
}
