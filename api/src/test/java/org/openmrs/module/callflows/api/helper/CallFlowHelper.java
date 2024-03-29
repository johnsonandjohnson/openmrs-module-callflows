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
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;

/**
 * Helper class to create various call flows
 *
 * @author bramak09
 */
public final class CallFlowHelper {

    // utility class, hence private constructor
    private CallFlowHelper() {
    }

    public static CallFlow createMainFlow() {
        CallFlow callFlow = new CallFlow(Constants.CALLFLOW_MAIN,
                Constants.CALLFLOW_MAIN_DESCRIPTION,
                CallFlowStatus.DRAFT,
                Constants.CALLFLOW_MAIN_RAW);
        return callFlow;
    }

    public static CallFlow createBadFlow() {
        CallFlow callFlow = new CallFlow(Constants.CALLFLOW_BAD,
                Constants.CALLFLOW_BAD_DESCRIPTION,
                CallFlowStatus.DRAFT,
                Constants.CALLFLOW_BAD_RAW);
        return callFlow;

    }

    public static CallFlow createTestFlow() {
        CallFlow callFlow = new CallFlow("TestFlow",
                "This is the Test Flow",
                CallFlowStatus.DRAFT,
                "{}");
        return callFlow;
    }
}
