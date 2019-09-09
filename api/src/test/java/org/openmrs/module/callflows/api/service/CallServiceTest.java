package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.BaseTest;
import org.openmrs.module.callflows.api.Constants;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.event.Events;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.CallHelper;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.FlowHelper;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.service.impl.CallServiceImpl;
import org.openmrs.module.callflows.api.util.CallAssert;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.openmrs.module.callflows.api.util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Call Service Test
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CallServiceImpl.class, DateTime.class, UUID.class, DefaultHttpClient.class })
public class CallServiceTest extends BaseTest {

    @Mock
    private CallDao callDao;

    @InjectMocks
    private CallService callService = new CallServiceImpl();

    @Mock
    private FlowService flowService;

    @Mock
    private CallFlowService callFlowService;

    @Mock
    private ConfigService configService;

    @Spy
    @InjectMocks
    private CallUtil callUtil = new CallUtil();

    private Call inboundCall;

    private Call outboundCall;

    private CallFlow mainFlow;

    private Flow flow;

    private Map<String, Object> params;

    private Map<String, Object> errorParams;

    private Map<String, String> providerData;

    private Config voxeo;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DATE_FORMAT);

    @Mock
    private DefaultHttpClient client;

    @Mock
    private CloseableHttpResponse okResponse;

    @Mock
    private CloseableHttpResponse notFoundResponse;

    @Mock
    private CloseableHttpResponse failureResponse;

    @Mock
    private CloseableHttpResponse badResponse;

    @Mock
    private EventRelay eventRelay;

    private MotechEvent callFailedEvent;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(DateTime.class);
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.mockStatic(DefaultHttpClient.class);

        params = CallHelper.createParams();
        errorParams = new HashMap<>();
        providerData = new HashMap<>();

        mainFlow = CallFlowHelper.createMainFlow();
        String raw = TestUtil.loadFile("main_flow.json");
        flow = FlowHelper.createFlow(raw);

        inboundCall = CallHelper.createInboundCall();
        outboundCall = CallHelper.createOutboundCall();

        voxeo = ConfigHelper.createConfigs().get(0);

        given(callFlowService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);
        given(callFlowService.findByName(Constants.CALLFLOW_MAIN2)).willThrow(new IllegalArgumentException("Bad!"));

        given(configService.getConfig(Constants.CONFIG_VOXEO)).willReturn(voxeo);
        given(configService.getConfig(Constants.CONFIG_YO)).willThrow(new IllegalArgumentException("Bad!"));
        given(flowService.load(Constants.CALLFLOW_MAIN)).willReturn(flow);

        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_CURRENT));

        PowerMockito.whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(client);

        HttpEntity okEntity = mock(HttpEntity.class);
        given(okEntity.getContent()).willReturn(IOUtils.toInputStream("OK"));
        given(okResponse.getEntity()).willReturn(okEntity);

        StatusLine okStatusLine = mock(StatusLine.class);
        given(okResponse.getStatusLine()).willReturn(okStatusLine);
        given(okStatusLine.getStatusCode()).willReturn(200);

        HttpEntity errorEntity = mock(HttpEntity.class);
        given(errorEntity.getContent()).willReturn(IOUtils.toInputStream("ERROR"));
        given(notFoundResponse.getEntity()).willReturn(errorEntity);

        StatusLine errorStatusLine = mock(StatusLine.class);
        given(notFoundResponse.getStatusLine()).willReturn(errorStatusLine);
        given(errorStatusLine.getStatusCode()).willReturn(404);

        HttpEntity failureEntity = mock(HttpEntity.class);
        given(failureEntity.getContent()).willReturn(IOUtils.toInputStream("failure: unknown error"));
        given(failureResponse.getEntity()).willReturn(failureEntity);

        StatusLine failureStatusLine = mock(StatusLine.class);
        given(failureResponse.getStatusLine()).willReturn(failureStatusLine);
        given(failureStatusLine.getStatusCode()).willReturn(200);

        HttpEntity badEntity = mock(HttpEntity.class);
        given(badEntity.getContent()).willReturn(null);
        given(badResponse.getEntity()).willReturn(badEntity);

        StatusLine badStatusLine = mock(StatusLine.class);
        given(badResponse.getStatusLine()).willReturn(badStatusLine);
        given(badStatusLine.getStatusCode()).willReturn(200);

        // Given a request to make a call for a flow named MainFlow using voxeo config for a phone 1234567890
        given(UUID.randomUUID()).willReturn(Constants.OUTBOUND_CALL_ID);
        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        ArgumentCaptor<HttpUriRequest> request = ArgumentCaptor.forClass(HttpUriRequest.class);
        given(callDao.create(callArgumentCaptor.capture())).willReturn(outboundCall);

        errorParams.put("callId", "unknown");
        errorParams.put("status", CallStatus.FAILED.name());
        errorParams.put("params", params);
        errorParams.put("reason", "Empty Phone no while initiating a outbound call for flow MainFlow");
        callFailedEvent = new MotechEvent(Events.CALLFLOWS_CALL_STATUS, errorParams);
    }

    @Test
    public void shouldCreateInboundCallWithActorAndExternalData() {

        // Given
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.create(callFlowArgumentCaptor.capture())).willReturn(inboundCall);
        given(UUID.randomUUID()).willReturn(Constants.INBOUND_CALL_ID);

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.INCOMING,
                        Constants.ACTOR_ID, Constants.ACTOR_TYPE, Constants.EXTERNAL_ID, Constants.EXTERNAL_TYPE,
                        Constants.PLAYED_MESSAGES, null, params);

        // Then
        verify(callDao, times(1)).create(inboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(Constants.INBOUND_CALL_ID.toString()));
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertExternal(newCall);
        CallAssert.assertPlayedMessages(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithActor() {
        // Given
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.create(callFlowArgumentCaptor.capture())).willReturn(outboundCall);
        given(UUID.randomUUID()).willReturn(Constants.OUTBOUND_CALL_ID);

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.OUTGOING,
                        Constants.ACTOR_ID, Constants.ACTOR_TYPE, null, null, null, Constants.REF_KEY, params);

        // Then
        verify(callDao, times(1)).create(outboundCall);

        // And
        CallAssert.assertBasicFields(newCall);
        assertThat(newCall.getCallId(), equalTo(Constants.OUTBOUND_CALL_ID.toString()));
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertRefKey(newCall);
        CallAssert.assertMockedTimestamps(newCall);
    }

    @Test
    public void shouldCreateInboundCallWithoutActor() {
        // Given
        inboundCall.setActorId(null);
        inboundCall.setActorType(null);
        ArgumentCaptor<Call> callFlowArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.create(callFlowArgumentCaptor.capture())).willReturn(inboundCall);
        given(UUID.randomUUID()).willReturn(Constants.INBOUND_CALL_ID);

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.INCOMING, params);

        // Then
        verify(callDao, times(1)).create(inboundCall);

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
        given(callDao.create(callFlowArgumentCaptor.capture())).willReturn(outboundCall);
        given(UUID.randomUUID()).willReturn(Constants.OUTBOUND_CALL_ID);

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.OUTGOING, params);

        // Then
        verify(callDao, times(1)).create(outboundCall);

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
        given(callDao.findByCallId(Constants.INBOUND_CALL_ID.toString())).willReturn(inboundCall);

        // When
        Call call = callService.findByCallId(Constants.INBOUND_CALL_ID.toString());

        // Then
        verify(callDao, times(1)).findByCallId(Constants.INBOUND_CALL_ID.toString());
        assertNotNull(call);
        assertThat(call.getCallId(), equalTo(Constants.INBOUND_CALL_ID.toString()));
    }

    @Test
    public void shouldUpdateOnlyAllowedFieldsInCall() {

        // Given a outbound call without an actor yet
        outboundCall.setId(1L);
        outboundCall.setActorId(null);
        outboundCall.setActorType(null);
        outboundCall.setExternalId(Constants.EXTERNAL_ID);
        outboundCall.setExternalType(Constants.EXTERNAL_TYPE);
        outboundCall.setPlayedMessages(Constants.PLAYED_MESSAGES);

        DateTime oldEndTime = outboundCall.getEndTime();
        // And we update all properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.findById(1L)).willReturn(outboundCall);

        // Given for create we returned DATE_CURRENT, And for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        callService.update(updatedCall);

        // Then
        verify(callDao, times(1)).update(callArgumentCaptor.capture());

        // And let's see what gets sent to the database
        Call returnedCall = callArgumentCaptor.getValue();
        assertNotNull(returnedCall);

        // And we are ** not ** supposed to update the following properties
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall,
                                                       outboundCall.getCallId());

        // And we are supposed to update the following
        CallAssert.assertChangeToChangeableFields(returnedCall);

        // And we are supposed to update the actor
        CallAssert.assertActorUpdated(returnedCall);

        //And we are supposed to update the external id, external type and playedMessages
        CallAssert.assertExternalUpdated(returnedCall);

        //Updates played messages when there is any passed as part of the call entity to be updated
        CallAssert.assertPlayedMessagesUpdated(returnedCall);

        //Assert Start time is updated
        CallAssert.assertStartAndEndTimeAreUpdated(returnedCall);
    }

    @Test
    public void shouldNotUpdatePlayedMessagesFieldWhenItIsNotPassed() {

        // Given a outbound call without an actor yet
        outboundCall.setId(1L);
        outboundCall.setPlayedMessages(Constants.PLAYED_MESSAGES);

        // And we update all properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);
        //Set the played messages field to be empty
        updatedCall.setPlayedMessages("");

        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.findById(1L)).willReturn(outboundCall);

        // Given for create we returned DATE_CURRENT, And for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        callService.update(updatedCall);

        // Then
        verify(callDao, times(1)).update(callArgumentCaptor.capture());

        // And let's see what gets sent to the database
        Call returnedCall = callArgumentCaptor.getValue();
        assertNotNull(returnedCall);

        //Check the contents of playedMessages
        assertThat(returnedCall.getPlayedMessages(), equalTo(""));
    }

    @Test
    public void shouldNotUpdateActorIfCallWasCreatedWithActorSet() {
        // Given
        outboundCall.setId(1L);
        DateTime oldEndTime = outboundCall.getEndTime();

        // And we update all properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        given(callDao.findById(1L)).willReturn(outboundCall);

        // Given for create we returned DATE_CURRENT, for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        callService.update(updatedCall);

        // Then
        verify(callDao, times(1)).update(callArgumentCaptor.capture());

        // And let's see what gets sent to the database
        Call returnedCall = callArgumentCaptor.getValue();
        assertNotNull(returnedCall);

        // And we are ** not ** supposed to update the following properties
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall, outboundCall.getCallId());

        // AND we are ** not ** supposed to update the actor now
        CallAssert.assertActor(returnedCall);

        // And we are supposed to update the following
        CallAssert.assertChangeToChangeableFields(returnedCall);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfInvalidCallWasProvided() {

        // Given
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);
        updatedCall.setId(2L);
        given(callDao.findById(1L)).willReturn(outboundCall);
        // Given for create we returned DATE_CURRENT, for update we return DATE_NEXT_DAY
        given(DateTime.now()).willReturn(formatter.parseDateTime(Constants.DATE_NEXT_DAY));

        // When
        try {
            callService.update(updatedCall);
        } finally {
            verify(callDao, never()).update(any(Call.class));
        }
    }

    @Test
    public void shouldMakeCall() throws IOException, URISyntaxException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNotNull(call);
        // And callflow, config and flow must be loaded
        assertAllLoaded();
        // And we shouldn't have to update the call status, since we are creating it
        verify(callDao, never()).update(any(Call.class));
        verify(callUtil, times(1)).buildOutboundRequest("1234567890", outboundCall, voxeo, params);
        // And no failure , so no motech event to be sent
        assertNoEventSent();
    }

    @Test
    public void shouldNotMakeCallForNullPhone() throws IOException, URISyntaxException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        params.put("phone", null);

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);
        assertNoServiceInteractions();
        assertEventSent(null, "Empty Phone no while initiating a outbound call for flow MainFlow", "unknown");
    }

    @Test
    public void shouldNotMakeCallForEmptyPhone() throws IOException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        params.put("phone", "");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);
        assertNoServiceInteractions();
        assertEventSent("", "Empty Phone no while initiating a outbound call for flow MainFlow", "unknown");
    }

    @Test
    public void shouldSendCallFailedEventIfBadConfigWasProvidedInMakeCall() throws IOException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_YO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);
        verify(configService, times(1)).getConfig(Constants.CONFIG_YO);
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN);
        verifyZeroInteractions(callDao);
        verifyZeroInteractions(flowService);
        assertEventSent("1234567890", "Bad!", "unknown");
    }

    @Test
    public void shouldSendCallFailedEventIfBadCallFlowWasProvidedInMakeCall() throws IOException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN2, params);

        // Then
        assertNull(call);
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        verify(configService, never()).getConfig(anyString());
        verifyZeroInteractions(callDao);
        verifyZeroInteractions(flowService);
        assertEventSent("1234567890", "Bad!", "unknown");
    }

    @Test
    public void shouldSendCallFailedIfHttpResponseFromProviderHasFailure() throws IOException, URISyntaxException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(failureResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertAllLoaded();
        assertCallCreated();
        verify(callDao, times(1)).update(outboundCall);
        verify(callUtil, times(1)).buildOutboundRequest("1234567890", outboundCall, voxeo, params);
        // Since phone and config are fine, we have a valid call object which we'll use for error reporting
        assertEventSent(outboundCall);
    }

    @Test
    public void shouldUpdateCallStatusToFailedIfCallQueuingResultsInOperationsNotSupportedException()
            throws IOException, URISyntaxException, OperationNotSupportedException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(okResponse);
        doThrow(new OperationNotSupportedException()).when(callUtil).checkCallCanBePlaced(outboundCall, voxeo, params);
        params.put("phone", "1234567890");

        // When
        callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertAllLoaded();
        assertCallCreated();
        verify(callDao, times(1)).update(outboundCall);
        verify(callUtil, never()).buildOutboundRequest("1234567890", outboundCall, voxeo, params);
    }

    @Test
    public void shouldSendCallFailedIfHttpStatusFromProviderIsNotAcceptable() throws IOException, URISyntaxException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(notFoundResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertAllLoaded();
        assertCallCreated();
        verify(callDao, times(1)).update(outboundCall);
        verify(callUtil, times(1)).buildOutboundRequest("1234567890", outboundCall, voxeo, params);
        assertEventSent(outboundCall);
    }

    @Test
    public void shouldSendCallFailedIfResponseFromProviderIsNotReadable() throws IOException, URISyntaxException {
        // Given
        given(client.execute(any(HttpGet.class))).willReturn(badResponse);
        params.put("phone", "1234567890");

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertAllLoaded();
        verify(callDao, times(1)).create(outboundCall);
        verify(callDao, times(1)).update(outboundCall);
        verify(callUtil, times(1)).buildOutboundRequest("1234567890", outboundCall, voxeo, params);
        assertEventSent(outboundCall);
    }

    public void assertAllLoaded() {
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN);
        verify(configService, times(1)).getConfig(Constants.CONFIG_VOXEO);
        verify(flowService, times(1)).load(Constants.CALLFLOW_MAIN);
    }

    public void assertNoServiceInteractions() {
        verifyZeroInteractions(callFlowService);
        verifyZeroInteractions(configService);
        verifyZeroInteractions(callDao);
        verifyZeroInteractions(flowService);
    }

    public void assertCallCreated() {
        verify(callDao, times(1)).create(outboundCall);
    }

    public void assertCallNotCreated() {
        verify(callDao, never()).create(any(Call.class));
    }

    public void assertNoEventSent() {
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    public void assertEventSent(String phone, String reason, String callId) {
        errorParams.put("reason", reason);
        errorParams.put("callId", callId);
        verify(callUtil, times(1)).sendStatusEvent(CallStatus.FAILED, reason, params);
        verify(eventRelay, times(1)).sendEventMessage(callFailedEvent);
    }

    public void assertEventSent(Call call) {
        verify(callUtil, times(1)).sendStatusEvent(call);
    }
}
