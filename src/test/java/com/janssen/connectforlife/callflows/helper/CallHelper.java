package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Call Helper
 *
 * @author bramak09
 */
public final class CallHelper {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DATE_FORMAT);

    // private constructor
    private CallHelper() {
    }

    public static Map<String, Object> createParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.KEY_GREETING, Constants.VALUE_GREETING);
        params.put(Constants.KEY_PIN, Constants.VALUE_PIN);
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
        call.setStatus(CallStatus.MOTECH_INITIATED);
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
        call.setStartTime(formatter.parseDateTime(Constants.DATE_CURRENT));

        call.setEndFlow(mainFlow);
        call.setEndNode(Constants.CALLFLOW_MAIN_ENTRY);
        call.setEndTime(formatter.parseDateTime(Constants.DATE_CURRENT));

        call.setSteps(0L);
        call.setContext(createParams());
        call.setProviderData(new HashMap<String, String>());

        call.setActorId(Constants.ACTOR_ID);
        call.setActorType(Constants.ACTOR_TYPE);

        return call;
    }
}
