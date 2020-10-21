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
