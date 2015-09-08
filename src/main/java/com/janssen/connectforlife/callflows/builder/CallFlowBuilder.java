package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.contract.CallFlowCreationRequest;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallFlowStatus;

import org.springframework.stereotype.Component;

/**
 * CallFlow builder
 *
 * @author bramak09
 */
@Component
public class CallFlowBuilder {

    /**
     * Creates a new Call Flow from a Call flow creation request
     *
     * @param callFlowCreationRequest containing attributes used during call flow creation
     * @return a new CallFlow object
     */
    public CallFlow createFrom(CallFlowCreationRequest callFlowCreationRequest) {
        return new CallFlow(callFlowCreationRequest.getName(),
                            callFlowCreationRequest.getDescription(),
                            CallFlowStatus.valueOf(callFlowCreationRequest.getStatus()),
                            callFlowCreationRequest.getRaw());
    }
}
