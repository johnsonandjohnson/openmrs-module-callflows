package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.service.impl.CallServiceImpl;
import com.janssen.connectforlife.callflows.util.CallAssert;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Call Service Test
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CallServiceImpl.class, DateTime.class, UUID.class })
public class CallServiceTest extends BaseTest {

    @Mock
    private CallDataService callDataService;

    @InjectMocks
    private CallService callService = new CallServiceImpl();

    private Call inboundCall;

    private Call outboundCall;

    private CallFlow mainFlow;

    private Map<String, Object> params;

    private Map<String, String> providerData;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DATE_FORMAT);

    @Before
    public void setUp() {
        PowerMockito.mockStatic(DateTime.class);
        PowerMockito.mockStatic(UUID.class);

        params = CallHelper.createParams();
        providerData = new HashMap<>();

        mainFlow = CallFlowHelper.createMainFlow();

        inboundCall = CallHelper.createInboundCall();
        outboundCall = CallHelper.createOutboundCall();

        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_CURRENT));
    }

    @Test
    public void shouldCreateInboundCallWithActor() {

        // Given
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.create(callFlowArgumentCaptor.capture())).willReturn(inboundCall);
        given(UUID.randomUUID()).willReturn(Constants.INBOUND_CALL_ID);

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.INCOMING,
                                          Constants.ACTOR_ID,
                                          Constants.ACTOR_TYPE,
                                          params);

        // Then
        verify(callDataService, times(1)).create(inboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(Constants.INBOUND_CALL_ID.toString()));
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithActor() {
        // Given
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.create(callFlowArgumentCaptor.capture())).willReturn(outboundCall);
        given(UUID.randomUUID()).willReturn(Constants.OUTBOUND_CALL_ID);

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.OUTGOING,
                                          Constants.ACTOR_ID,
                                          Constants.ACTOR_TYPE,
                                          params);

        // Then
        verify(callDataService, times(1)).create(outboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(Constants.OUTBOUND_CALL_ID.toString()));
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldCreateInboundCallWithoutActor() {
        // Given
        inboundCall.setActorId(null);
        inboundCall.setActorType(null);
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.create(callFlowArgumentCaptor.capture())).willReturn(inboundCall);
        given(UUID.randomUUID()).willReturn(Constants.INBOUND_CALL_ID);

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.INCOMING,
                                          params);

        // Then
        verify(callDataService, times(1)).create(inboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(inboundCall.getCallId()));
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertNullActor(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithoutActor() {
        // Given
        outboundCall.setActorId(null);
        outboundCall.setActorType(null);
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.create(callFlowArgumentCaptor.capture())).willReturn(outboundCall);
        given(UUID.randomUUID()).willReturn(Constants.OUTBOUND_CALL_ID);

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.OUTGOING,
                                          params);

        // Then
        verify(callDataService, times(1)).create(outboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(Constants.OUTBOUND_CALL_ID.toString()));
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertNullActor(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldFindCallByCallID() {
        // Given
        given(callDataService.findByCallId(Constants.INBOUND_CALL_ID.toString())).willReturn(inboundCall);

        // When
        Call call = callService.findByCallId(Constants.INBOUND_CALL_ID.toString());

        // Then
        verify(callDataService, times(1)).findByCallId(Constants.INBOUND_CALL_ID.toString());
        assertNotNull(call);
        assertThat(call.getCallId(), equalTo(Constants.INBOUND_CALL_ID.toString()));
    }

    @Test
    public void shouldUpdateOnlyAllowedFieldsInCall() {

        // Given a outbound call without an actor yet
        outboundCall.setId(1L);
        outboundCall.setActorId(null);
        outboundCall.setActorType(null);
        DateTime oldEndTime = outboundCall.getEndTime();
        // And we update all properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.findById(1L)).willReturn(outboundCall);

        // Given for create we returned DATE_CURRENT, And for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        callService.update(updatedCall);

        // Then
        verify(callDataService, times(1)).update(callArgumentCaptor.capture());

        // And let's see what gets sent to the database
        Call returnedCall = callArgumentCaptor.getValue();
        assertNotNull(returnedCall);

        // And we are ** not ** supposed to update the following properties
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall,
                                                       outboundCall.getCallId(),
                                                       outboundCall.getStartTime());

        // And we are supposed to update the following
        CallAssert.assertChangeToChangeableFields(returnedCall, oldEndTime);

        // And we are supposed to update the actor
        CallAssert.assertActorUpdated(returnedCall);
    }

    @Test
    public void shouldNotUpdateActorIfCallWasCreatedWithActorSet() {
        // Given
        outboundCall.setId(1L);
        DateTime oldEndTime = outboundCall.getEndTime();

        // And we update all properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDataService.findById(1L)).willReturn(outboundCall);

        // Given for create we returned DATE_CURRENT, for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        callService.update(updatedCall);

        // Then
        verify(callDataService, times(1)).update(callArgumentCaptor.capture());

        // And let's see what gets sent to the database
        Call returnedCall = callArgumentCaptor.getValue();
        assertNotNull(returnedCall);

        // And we are ** not ** supposed to update the following properties
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall,
                                                       outboundCall.getCallId(),
                                                       outboundCall.getStartTime());

        // AND we are ** not ** supposed to update the actor now
        CallAssert.assertActor(returnedCall);

        // And we are supposed to update the following
        CallAssert.assertChangeToChangeableFields(returnedCall, oldEndTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfInvalidCallWasProvided() {

        // Given
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);
        updatedCall.setId(2L);
        given(callDataService.findById(1L)).willReturn(outboundCall);
        // Given for create we returned DATE_CURRENT, for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        try {
            callService.update(updatedCall);
        } finally {
            verify(callDataService, never()).update(any(Call.class));
        }
    }

}