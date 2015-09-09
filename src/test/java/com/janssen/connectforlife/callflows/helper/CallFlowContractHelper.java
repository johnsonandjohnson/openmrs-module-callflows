package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.CallFlowRequest;
import com.janssen.connectforlife.callflows.contract.CallFlowResponse;
import com.janssen.connectforlife.callflows.domain.types.CallFlowStatus;

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
        return new CallFlowResponse(1L,
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


}
