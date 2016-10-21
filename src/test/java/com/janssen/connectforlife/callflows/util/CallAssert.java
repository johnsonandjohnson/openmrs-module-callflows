package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * A handy collection of asserts for testing calls
 *
 * @author bramak09
 */
public final class CallAssert {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DATE_FORMAT);

    private static Map<String, String> providerData = new HashMap<>();

    // private
    private CallAssert() {

    }

    public static void assertBasicFields(Call newCall) {

        assertNotNull(newCall);

        // We just test for not null in order to re-use this method in both mocked and integration tests
        // The mocked methods test for the correct ID
        assertNotNull(newCall.getCallId());

        assertThat(newCall.getConfig(), equalTo(Constants.CONFIG_VOXEO));

        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        assertThat(newCall.getStartFlow(), equalTo(mainFlow));
        assertThat(newCall.getStartNode(), equalTo(Constants.CALLFLOW_MAIN_ENTRY));

        assertThat(newCall.getEndFlow(), equalTo(newCall.getStartFlow()));
        assertThat(newCall.getEndNode(), equalTo(newCall.getStartNode()));

        Map<String, Object> params = CallHelper.createParams();
        assertThat(newCall.getContext(), equalTo(params));

        assertNull(newCall.getProviderCallId());
        assertNull(newCall.getProviderTime());
        assertThat(newCall.getProviderData(), equalTo(providerData));

        assertThat(newCall.getSteps(), equalTo(0L));
    }

    public static void assertNoChangeToNonChangeableFields(Call call, String oldCallId, DateTime oldStartTime) {
        assertThat(call.getCallId(), equalTo(oldCallId));
        assertThat(call.getConfig(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(call.getDirection(), equalTo(CallDirection.OUTGOING));

        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        assertThat(call.getStartFlow(), equalTo(mainFlow));

        assertThat(call.getStartNode(), equalTo(Constants.CALLFLOW_MAIN_ENTRY));
        assertThat(call.getStartTime(), equalTo(oldStartTime));
    }

    public static void assertChangeToChangeableFields(Call call, DateTime oldEndTime) {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setName(Constants.CALLFLOW_MAIN2);
        assertThat(call.getEndFlow(), equalTo(mainFlow));
        assertThat(call.getEndNode(), equalTo(Constants.CALLFLOW_MAIN_ENTRY + Constants.UPDATED));
        assertThat(call.getEndTime(), not(equalTo(oldEndTime)));

        assertThat(call.getSteps(), equalTo(1L));

        assertThat(call.getProviderCallId(), equalTo(Constants.UPDATED));
        assertNotNull(call.getProviderTime());
        assertThat(call.getContext(), equalTo(CallHelper.createUpdatedParams()));

        assertThat(call.getStatus(), equalTo(CallStatus.IN_PROGRESS));
    }

    public static void assertActor(Call call) {
        assertThat(call.getActorId(), equalTo(Constants.ACTOR_ID));
        assertThat(call.getActorType(), equalTo(Constants.ACTOR_TYPE));
    }

    public static void assertActorUpdated(Call call) {
        assertThat(call.getActorId(), equalTo(Constants.ACTOR_ID + Constants.UPDATED));
        assertThat(call.getActorType(), equalTo(Constants.ACTOR_TYPE + Constants.UPDATED));
    }

    public static void assertExternal(Call call) {
        assertThat(call.getExternalId(), equalTo(Constants.EXTERNAL_ID));
        assertThat(call.getExternalType(), equalTo(Constants.EXTERNAL_TYPE));
    }

    public static void assertExternalUpdated(Call call) {
        assertThat(call.getExternalId(), equalTo(Constants.EXTERNAL_ID + Constants.UPDATED));
        assertThat(call.getExternalType(), equalTo(Constants.EXTERNAL_TYPE + Constants.UPDATED));
    }

    public static void assertPlayedMessages(Call call) {
        assertThat(call.getPlayedMessages(), equalTo(Constants.PLAYED_MESSAGES));
    }

    public static void assertPlayedMessagesUpdated(Call call) {
        assertThat(call.getPlayedMessages(), equalTo(Constants.PLAYED_MESSAGES + Constants.UPDATED));
    }

    public static void assertNullActor(Call call) {
        assertNull(call.getActorId());
        assertNull(call.getActorType());
    }

    public static void assertIncomingCall(Call call) {
        assertThat(call.getStatus(), equalTo(CallStatus.INITIATED));
        assertThat(call.getDirection(), equalTo(CallDirection.INCOMING));
    }

    public static void assertOutgoingCall(Call call) {
        assertThat(call.getStatus(), equalTo(CallStatus.MOTECH_INITIATED));
        assertThat(call.getDirection(), equalTo(CallDirection.OUTGOING));
    }

    public static void assertMockedTimestamps(Call call) {
        assertThat(call.getStartTime(), equalTo(formatter.parseDateTime(Constants.DATE_CURRENT)));
        assertThat(call.getEndTime(), equalTo(call.getStartTime()));
    }

    public static void assertTimestamps(Call call) {
        assertNotNull(call.getStartTime());
        assertThat(call.getEndTime(), equalTo(call.getStartTime()));
    }
}
