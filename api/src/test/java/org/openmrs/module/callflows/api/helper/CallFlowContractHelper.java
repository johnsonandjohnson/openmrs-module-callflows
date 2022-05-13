/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.helper;

import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;

/**
 * Call Flow Response Helper
 *
 * @author bramak09
 */
public final class CallFlowContractHelper {

    // utility class, hence private constructor
    private CallFlowContractHelper() {

    }

    public static CallFlowResponse createMainFlowResponse() {
        return new CallFlowResponse(1,
                Constants.CALLFLOW_MAIN,
                Constants.CALLFLOW_MAIN_DESCRIPTION,
                CallFlowStatus.DRAFT.name(),
                Constants.CALLFLOW_MAIN_RAW);
    }

    public static CallFlowRequest createMainFlowRequest() {
        CallFlowRequest request = new CallFlowRequest();
        request.setName(Constants.CALLFLOW_MAIN);
        request.setDescription(Constants.CALLFLOW_MAIN_DESCRIPTION);
        request.setStatus(CallFlowStatus.DRAFT.name());
        request.setRaw(Constants.CALLFLOW_MAIN_RAW);
        return request;
    }

    public static CallFlowRequest createBadFlowRequest() {
        CallFlowRequest request = new CallFlowRequest();
        request.setName(Constants.CALLFLOW_BAD);
        request.setDescription(Constants.CALLFLOW_BAD_DESCRIPTION);
        request.setStatus(CallFlowStatus.DRAFT.name());
        request.setRaw(Constants.CALLFLOW_BAD_RAW);
        return request;
    }

    public static CallFlowRequest createBadFlowRequestWithoutNodes() {
        CallFlowRequest request = new CallFlowRequest();
        request.setName(Constants.CALLFLOW_BAD);
        request.setDescription(Constants.CALLFLOW_BAD_DESCRIPTION);
        request.setStatus(CallFlowStatus.DRAFT.name());
        request.setRaw(Constants.CALLFLOW_BAD_RAW_NO_NODES);
        return request;
    }

    public static CallFlowResponse createFlow1Response() {
        return new CallFlowResponse(1,
                Constants.CALLFLOW_MAIN,
                Constants.CALLFLOW_MAIN_DESCRIPTION,
                CallFlowStatus.DRAFT.name(),
                Constants.CALLFLOW_MAIN_RAW);
    }

    public static CallFlowResponse createFlow2Response() {
        return new CallFlowResponse(2,
                Constants.CALLFLOW_MAIN2,
                Constants.CALLFLOW_MAIN_DESCRIPTION,
                CallFlowStatus.DRAFT.name(),
                Constants.CALLFLOW_MAIN_RAW);
    }
}
