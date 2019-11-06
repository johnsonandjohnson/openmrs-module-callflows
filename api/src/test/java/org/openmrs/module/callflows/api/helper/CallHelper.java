package org.openmrs.module.callflows.api.helper;

import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Call Helper
 *
 * @author bramak09
 */
public final class CallHelper {

    // private constructor
    private CallHelper() {
    }

    public static Map<String, Object> createParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.KEY_GREETING, Constants.VALUE_GREETING);
        params.put(Constants.KEY_PIN, Constants.VALUE_PIN);
        return params;
    }

    public static Map<String, Object> createUpdatedParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.KEY_GREETING, Constants.VALUE_GREETING + Constants.UPDATED);
        params.put(Constants.KEY_PIN, Constants.VALUE_PIN + Constants.UPDATED);
        return params;
    }

    public static Call createInboundCall() {
        Call call = createCall();
        call.setStatus(CallStatus.INITIATED);
        call.setDirection(CallDirection.INCOMING);
        call.setCallId(Constants.INBOUND_CALL_ID.toString());
        return call;
    }

    public static Call createOutboundCall() {
        Call call = createCall();
        call.setStatus(CallStatus.OPENMRS_INITIATED);
        call.setDirection(CallDirection.OUTGOING);
        call.setCallId(Constants.OUTBOUND_CALL_ID.toString());
        return call;
    }

    private static Call createCall() {
        Call call = new Call();
        call.setConfig(Constants.CONFIG_VOXEO);

        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        call.setStartFlow(mainFlow);
        call.setStartNode(Constants.CALLFLOW_MAIN_ENTRY);

        call.setEndFlow(mainFlow);
        call.setEndNode(Constants.CALLFLOW_MAIN_ENTRY);

        call.setSteps(0L);
        call.setContext(createParams());
        call.setProviderData(new HashMap<String, String>());

        call.setActorId(Constants.ACTOR_ID);
        call.setActorType(Constants.ACTOR_TYPE);

        call.setExternalId(Constants.EXTERNAL_ID);
        call.setExternalType(Constants.EXTERNAL_TYPE);
        call.setPlayedMessages(Constants.PLAYED_MESSAGES);

        call.setRefKey(Constants.REF_KEY);
        return call;
    }

    public static Call updateAllPropertiesInOutboundCall(Call existingCall) {
        Call call = new Call();
        call.setId(existingCall.getId() == null ? 1 : existingCall.getId());
        call.setConfig(Constants.CONFIG_VOXEO + Constants.UPDATED);
        call.setCallId(existingCall.getCallId() + Constants.UPDATED);

        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setName(Constants.CALLFLOW_MAIN2);
        call.setStartFlow(mainFlow);
        call.setStartNode(Constants.CALLFLOW_MAIN_ENTRY + Constants.UPDATED);

        call.setEndFlow(mainFlow);
        call.setEndNode(Constants.CALLFLOW_MAIN_ENTRY + Constants.UPDATED);

        call.setSteps(1L);

        call.setContext(createUpdatedParams());

        call.setProviderData(new HashMap<String, String>());

        call.setProviderTime(Constants.DATE_CURRENT);
        call.setProviderCallId(Constants.UPDATED);

        call.setActorId(Constants.ACTOR_ID + Constants.UPDATED);
        call.setActorType(Constants.ACTOR_TYPE + Constants.UPDATED);

        call.setExternalId(Constants.EXTERNAL_ID + Constants.UPDATED);
        call.setExternalType(Constants.EXTERNAL_TYPE + Constants.UPDATED);

        call.setPlayedMessages(Constants.PLAYED_MESSAGES + Constants.UPDATED);

        call.setStatus(CallStatus.IN_PROGRESS);
        call.setDirection(CallDirection.INCOMING);

        return call;
    }
}
