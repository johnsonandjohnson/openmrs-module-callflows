package org.openmrs.module.callflows.api.util;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.CallHelper;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.FlowHelper;
import org.openmrs.module.callflows.api.service.CallFlowEventService;
import org.openmrs.module.callflows.api.service.CallFlowSchedulerService;
import org.openmrs.module.callflows.api.service.impl.CallServiceImpl;
import org.openmrs.module.callflows.api.task.CallFlowScheduledTask;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Call Helper Test Cases
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CallUtil.class, DateUtil.class, BufferedWriter.class, OutputStreamWriter.class,
        FileOutputStream.class, CsvMapWriter.class})
public class CallUtilTest extends BaseTest {

    private static final String AUTH_TOKEN = "2323232wewewe";

    private static final String NEW_AUTH_TOKEN = "734343efererere93";

    private CallFlowEvent callFlowEvent;

    private CallFlow mainFlow;

    private Flow flow;

    private Call inboundCall;

    private Config voxeo;

    private Config imiMobile;

    private Call outboundCall;

    private Date dateTime;

    private Map<String, Object> callParams;

    private Map<String, Object> eventParams = new HashMap<>();

    private Set<CallStatus> callStatusSet = new HashSet<>(Constants.ACTIVE_OUTBOUND_CALL_STATUSES);

    private BufferedWriter bufferedWriter;
    private FileOutputStream fileOutputStream;
    private OutputStreamWriter outputStreamWriter;
    private CsvMapWriter csvMapWriter;

    @InjectMocks
    private CallUtil callUtil = new CallUtil();

    @Mock
    private HttpServletRequest request;

    @Mock
    private CallDao callDao;

    @Mock
    @Autowired
    @Qualifier("callflow.schedulerService")
    private CallFlowSchedulerService schedulerService;

    @Mock
    @Autowired
    @Qualifier("callFlow.eventService")
    private CallFlowEventService callFlowEventService;

    @Mock
    private Config config;

    @Mock
    private Call call;

    @Mock
    private AuthUtil authUtil;

    @Captor
    private ArgumentCaptor<LinkedHashMap> csvArgumentCaptor;

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(CallUtilTest.class);
        PowerMockito.mockStatic(DateUtil.class);
        bufferedWriter = PowerMockito.mock(BufferedWriter.class);
        fileOutputStream = PowerMockito.mock(FileOutputStream.class);
        outputStreamWriter = PowerMockito.mock(OutputStreamWriter.class);
        csvMapWriter = PowerMockito.mock(CsvMapWriter.class);


        dateTime = new Date();
        given(DateUtil.now()).willReturn(dateTime);

        mainFlow = CallFlowHelper.createMainFlow();

        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        flow = FlowHelper.createFlow(raw);

        inboundCall = CallHelper.createInboundCall();
        outboundCall = CallHelper.createOutboundCall();
        voxeo = ConfigHelper.createConfigs().get(0);
        imiMobile = ConfigHelper.createConfigs().get(2);

        callParams = new HashMap<>();
        callParams.put(Constants.TEST_PARAM, Constants.TEST_VALUE);

        eventParams.put(Constants.PARAM_CONFIG, Constants.CONFIG_VOXEO);
        eventParams.put(Constants.PARAM_FLOW_NAME, Constants.CALLFLOW_MAIN);
        callFlowEvent = new CallFlowEvent(CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL, eventParams);
    }

    @Test
    public void shouldBuildFullNodePath() {
        // When
        String out = callUtil.buildFullNodePath(mainFlow, flow.getNodes().get(0));

        // Then
        assertNotNull(out);
        assertThat(out, equalTo("MainFlow.entry"));
    }

    @Test
    public void shouldBuildCallContinuationUrl() {
        // Given a call
        given(request.getScheme()).willReturn("http");
        given(request.getHeader("Host")).willReturn("localhost");
        given(request.getContextPath()).willReturn("/openmrs");

        // When
        String url = callUtil.buildContinuationUrl(request, inboundCall, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(url);
        assertThat(url, equalTo("http://localhost/openmrs/ws/callflows" +
                "/calls/" + inboundCall.getCallId() + ".vxml"));

    }

    @Test
    public void shouldReplaceParamsInString() throws UnsupportedEncodingException {
        // Given
        String url = "http://localhost/context-path?phone=[phone]&callId=[internal.callId]&noreplace=ok";
        Map<String, Object> params = new HashMap<>();
        params.put("phone", "1234567890");
        params.put("internal.callId", "myCallId");

        // When
        String result = callUtil.mergeUriAndRemoveParams(url, params);

        // Then
        assertThat(result, equalTo("http://localhost/context-path?phone=1234567890&callId=myCallId&noreplace=ok"));
    }

    @Test
    public void shouldBuildOutboundRequestUrlForGetRequest() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("GET"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));
    }

    @Test
    public void shouldBuildOutboundRequestUrlForPostRequest() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));

    }

    @Test
    public void shouldBuildOutboundRequestUrlForTestUserCorrectly() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.getTestUsersMap()
                .put("1234567890", "http://i-should-override-everything?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://i-should-override-everything?callId=" + inboundCall.getCallId() +
                        "&myParam=testValue")));

    }

    @Test
    public void shouldGenerateTokenForTheFirstTime()
            throws URISyntaxException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");
        voxeo.setHasAuthRequired(true);
        voxeo.setAuthToken(null);

        given(authUtil.generateToken()).willReturn(NEW_AUTH_TOKEN);

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));
        assertThat(voxeo.getAuthToken(), equalTo(NEW_AUTH_TOKEN));

        verify(authUtil, times(1)).generateToken();
    }

    @Test
    public void shouldGenerateTokenIfTheTokenIsExpired()
            throws URISyntaxException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");
        voxeo.setHasAuthRequired(true);
        voxeo.setAuthToken(AUTH_TOKEN);

        given(authUtil.isTokenValid(AUTH_TOKEN)).willReturn(false);
        given(authUtil.generateToken()).willReturn(NEW_AUTH_TOKEN);

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));
        assertThat(voxeo.getAuthToken(), equalTo(NEW_AUTH_TOKEN));

        verify(authUtil, times(1)).isTokenValid(anyString());
        verify(authUtil, times(1)).generateToken();
    }

    @Test
    public void shouldNotGenerateTokenForSubsequentRequests()
            throws URISyntaxException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");
        voxeo.setHasAuthRequired(true);
        voxeo.setAuthToken(AUTH_TOKEN);

        given(authUtil.isTokenValid(AUTH_TOKEN)).willReturn(true);

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));
        assertThat(voxeo.getAuthToken(), equalTo(AUTH_TOKEN));

        verify(authUtil, times(1)).isTokenValid(anyString());
        verify(authUtil, Mockito.never()).generateToken();
    }

    @Test
    public void shouldNotTriggerGenerateTokenIfAuthFlagIsDisabled()
            throws URISyntaxException, IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");
        voxeo.setHasAuthRequired(false);

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));

        verify(authUtil, Mockito.never()).isTokenValid(anyString());
        verify(authUtil, Mockito.never()).generateToken();
    }

    @Test
    public void shouldBuildOutboundRequestUrlForTestUserCorrectlyIMI() throws URISyntaxException {
        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, imiMobile, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(), equalTo(new URI(imiMobile.getOutgoingCallUriTemplate())));
        assertThat((uriRequest.getHeaders("Key")[0]).getValue(), equalTo("ccb2b7b2-3205-44ba-9e06-4844be3c298f"));
        assertThat((uriRequest.getHeaders("Content-Type")[0]).getValue(), equalTo("application/x-www-form-urlencoded"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfBadUrlWasProvidedInBuildOutboundRequest() throws URISyntaxException {

        // Given
        voxeo.setOutgoingCallUriTemplate("http://bad-url?% ");

        // When
        callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfBadUrlWasProvidedForTestUserInBuildOutboundRequest()
            throws URISyntaxException {

        // Given
        voxeo.setOutgoingCallUriTemplate("http://bad-url?% ");
        voxeo.getTestUsersMap()
                .put("1234567890", "http://i-should-override-everything?callId=[internal.callId]&myParam=[testParam]&%");
        voxeo.setOutgoingCallMethod("POST");

        // When
        callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

    }

    @Test
    public void shouldAllowCallAndDoNotCheckActiveCallsAndRetriesIfOutboundCallLimitIsNotSet()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(0);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(1)).getOutboundCallLimit();
        verifyZeroInteractions(callDao);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldCheckActiveCallsAndRetriesIfOutboundCallLimitIsSet() throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDao, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        verifyZeroInteractions(schedulerService);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void shouldRetryIfCurrentActiveCallCountIsGreaterThanOutboundCallLimitAndCurrentRetriesIsLessThanRetryLimit()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDao.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 1);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        callFlowEvent = new CallFlowEvent(CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL, eventParams);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDao, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        assertThat(eventParams.get(Constants.PARAM_RETRY_ATTEMPTS).toString(), equalTo("2"));
        verify(schedulerService, times(1)).scheduleRunOnceJob(callFlowEvent,
                DateUtil.plusSeconds(DateUtil.now(), Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS), new CallFlowScheduledTask());
    }

    @Test(expected = OperationNotSupportedException.class)
    public void shouldThrowOperationNotSupportedIfRetryCountIsGreaterThanMaxAllowedRetryAttemptsAndCallAllowedIsFalse()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDao.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 6);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        given(config.getCallAllowed()).willReturn(false);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDao, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldAllowCallIfCurrentRetryLimitIsGreaterThanMaxAllowedRetryAttemptsAndCallAllowedIsTrue()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDao.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 6);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        callFlowEvent = new CallFlowEvent(CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL, eventParams);
        given(config.getCallAllowed()).willReturn(true);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDao, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldMergeContextWithCallForAVarietyOfClassTypes() {
        // Given
        VelocityContext context = new VelocityContext();
        outboundCall.getContext().clear();
        int integerValue = 1;
        long longValue = 10L;
        Integer integerWrapperValue = new Integer(integerValue);
        Long longWrapperValue = new Long(longValue);
        int[] arrayOfInt = {1, 2, 3, 4};
        long[] arrayOfLong = {1L, 2L, 3L, 4L};
        float[] arrayOfFloat = {1.1f, 2.2f, 3.3f};
        List<Integer> listOfInteger = Arrays.asList(1, 2, 3, 4);
        List<String> listOfString = Arrays.asList("One", "Two", "Three");
        String[] arrayOfString = {"One", "Two", "Three"};
        Map<String, String> internalMap = new HashMap<String, String>();
        internalMap.put("test", "test");

        context.put("string", "am a string");
        context.put("integer", integerValue);
        context.put("integerWrapper", integerWrapperValue);
        context.put("long", longValue);
        context.put("longWrapper", longWrapperValue);
        context.put("arrayOfInt", arrayOfInt);
        context.put("arrayOfFloat", arrayOfFloat);
        context.put("arrayOfLong", arrayOfLong);
        context.put("arrayOfString", arrayOfString);
        context.put("listOfInteger", listOfInteger);
        context.put("listOfString", listOfString);
        context.put("internal", internalMap);
        context.put("complex", new Call());
        context.put("service", new CallServiceImpl());

        context.put("externalId", Constants.EXTERNAL_ID);
        context.put("externalType", Constants.EXTERNAL_TYPE);
        context.put("playedMessages", Constants.PLAYED_MESSAGES);

        context.put("refKey", Constants.REF_KEY);

        // When
        callUtil.mergeContextWithCall(context, outboundCall);

        // Then
        assertTrue(outboundCall.getContext().containsKey("string"));
        assertTrue(outboundCall.getContext().containsKey("integer"));
        assertTrue(outboundCall.getContext().containsKey("integerWrapper"));
        assertTrue(outboundCall.getContext().containsKey("long"));
        assertTrue(outboundCall.getContext().containsKey("longWrapper"));
        assertTrue(outboundCall.getContext().containsKey("arrayOfInt"));
        assertTrue(outboundCall.getContext().containsKey("arrayOfFloat"));
        assertTrue(outboundCall.getContext().containsKey("arrayOfLong"));
        assertTrue(outboundCall.getContext().containsKey("arrayOfString"));
        assertTrue(outboundCall.getContext().containsKey("internal"));
        // And we are not storing that one complex object
        assertFalse(outboundCall.getContext().containsKey("complex"));
        assertFalse(outboundCall.getContext().containsKey("service"));
        assertThat(outboundCall.getContext().size(), equalTo(context.getKeys().length - 2));

        assertTrue(outboundCall.getContext().containsKey("externalId"));
        assertTrue(outboundCall.getContext().containsKey("externalType"));
        assertTrue(outboundCall.getContext().containsKey("playedMessages"));

        assertTrue(outboundCall.getContext().containsKey("refKey"));
    }

    @Test
    public void shouldSendCallStatusEventForValidCall() {
        // Given a call with status and statusText as follows
        outboundCall.setStatus(CallStatus.BUSY);
        outboundCall.setStatusText(Constants.STATUS_TEXT);
        outboundCall.setContext(callParams);
        ArgumentCaptor<CallFlowEvent> callFlowEventArgumentCaptor = ArgumentCaptor.forClass(CallFlowEvent.class);

        // When
        callUtil.sendStatusEvent(outboundCall);

        // Then
        verify(callFlowEventService, times(1)).sendEventMessage(callFlowEventArgumentCaptor.capture());
        CallFlowEvent capturedEvent = callFlowEventArgumentCaptor.getValue();
        assertThat((String) capturedEvent.getParameters().get(Constants.PARAM_CALL_ID),
                equalTo(outboundCall.getCallId()));
        assertCallStatusEvent(capturedEvent);
    }

    @Test
    public void shouldNotSendCallStatusEventForInvalidCall() {
        // Given a null call
        Call badCall = null;

        // When
        callUtil.sendStatusEvent(badCall);

        // Then
        verifyZeroInteractions(callFlowEventService);
    }

    @Test
    public void shouldSendStatusEventForArgumentsPassed() {
        // Given, When
        callUtil.sendStatusEvent(CallStatus.BUSY, Constants.STATUS_TEXT, callParams);
        ArgumentCaptor<CallFlowEvent> callFlowEventArgumentCaptor = ArgumentCaptor.forClass(CallFlowEvent.class);

        // Then
        verify(callFlowEventService, times(1)).sendEventMessage(callFlowEventArgumentCaptor.capture());
        CallFlowEvent capturedEvent = callFlowEventArgumentCaptor.getValue();
        // Since we are sending this event without a call object being created, the call ID should be unknown
        assertThat((String) capturedEvent.getParameters().get(Constants.PARAM_CALL_ID), equalTo("unknown"));
        assertCallStatusEvent(capturedEvent);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void shouldSetCallRetryLimitToOneIfRetryAttemptIsNull() throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDao.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, null);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        callFlowEvent = new CallFlowEvent(CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL, eventParams);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDao, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        assertThat(eventParams.get(Constants.PARAM_RETRY_ATTEMPTS).toString(), equalTo("1"));
        verify(schedulerService, times(1)).scheduleRunOnceJob(callFlowEvent,
                DateUtil.plusSeconds(DateUtil.now(), Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS), new CallFlowScheduledTask());
    }

    @Test
    public void shouldNotThrowAnyExceptionWhileGeneratingCallReports() throws Exception {
        //Given
        List<Call> calls = new ArrayList<>(1);
        calls.add(setupCallData());
        final String[] headers = {"id", "actorId", "phone", "actorType", "callId", "direction", "creationDate",
                "callReference", "status", "statusText", "startTime", "endTime"};

        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();

        PowerMockito.whenNew(FileOutputStream.class).withArguments(anyString()).thenReturn(fileOutputStream);
        PowerMockito.whenNew(OutputStreamWriter.class).withArguments(fileOutputStream, Charset.forName(UTF_8))
                .thenReturn(outputStreamWriter);
        PowerMockito.whenNew(BufferedWriter.class).withArguments(outputStreamWriter).thenReturn(bufferedWriter);
        PowerMockito.whenNew(CsvMapWriter.class).withArguments(bufferedWriter, CsvPreference.STANDARD_PREFERENCE)
                .thenReturn(csvMapWriter);
        PowerMockito.whenNew(LinkedHashMap.class).withArguments(anyInt()).thenReturn(linkedHashMap);

        //When
        callUtil.generateReports("", calls);

        //verify
        verify(csvMapWriter, times(1)).write(csvArgumentCaptor.capture(), Matchers.<String>anyVararg());
        assertEquals(calls.get(0).getId(), csvArgumentCaptor.getValue().get("id"));
        assertEquals(calls.get(0).getCallId(), csvArgumentCaptor.getValue().get("callId"));
    }

    private Call setupCallData() {
        Call callData = new Call();
        callData.setId(1);
        callData.setActorId("10L");
        callData.setCallId("91882-92882-1882-9383ss-28292");
        callData.setDirection(CallDirection.OUTGOING);
        return callData;
    }

    private void assertCallStatusEvent(CallFlowEvent event) {
        assertThat(event.getSubject(), equalTo(CallFlowEventSubjectConstants.CALLFLOWS_CALL_STATUS));
        Map<String, Object> eventParameters = event.getParameters();
        assertThat(eventParameters.get(Constants.PARAM_STATUS), equalTo(CallStatus.BUSY.name()));
        assertThat(eventParameters.get(Constants.PARAM_REASON), equalTo(Constants.STATUS_TEXT));
        assertThat(eventParameters.get(Constants.PARAM_PARAMS), equalTo(callParams));
    }

}
