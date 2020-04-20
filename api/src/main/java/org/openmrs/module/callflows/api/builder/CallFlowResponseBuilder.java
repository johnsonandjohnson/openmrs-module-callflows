package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.springframework.stereotype.Component;

/**
 * Call Flow Response Builder
 *
 * @author bramak09
 */
@Component
public class CallFlowResponseBuilder {

    /**
     * Creates a new CallFlowResponse from a provided CallFlow
     *
     * @param callFlow that is provided
     * @return a CallFlowResponse
     */
    public CallFlowResponse createFrom(CallFlow callFlow) {
        return new CallFlowResponse(callFlow.getId(),
                callFlow.getName(),
                callFlow.getDescription(),
                callFlow.getStatus().name(),
                callFlow.getRaw());
    }
}
