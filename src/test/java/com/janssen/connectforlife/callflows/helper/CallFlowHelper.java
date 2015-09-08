package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallFlowStatus;

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
}
