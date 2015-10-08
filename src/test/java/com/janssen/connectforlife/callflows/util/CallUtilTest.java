package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.event.Events;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.service.impl.CallServiceImpl;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.velocity.VelocityContext;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Call Helper Test Cases
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DateTime.class })
public class CallUtilTest extends BaseTest {

    private MotechEvent motechEvent;

    private CallFlow mainFlow;

    private Flow flow;

    private Call inboundCall;

    private Config voxeo;

    private Call outboundCall;

    private DateTime dateTime;

    private Map<String, Object> callParams;

    private Map<String, Object> eventParams = new HashMap<>();

    private Set<CallStatus> callStatusSet = new HashSet<>(Constants.ACTIVE_OUTBOUND_CALL_STATUSES);

    @InjectMocks
    private CallUtil callUtil = new CallUtil();

    @Mock
    private HttpServletRequest request;

    @Mock
    private CallDataService callDataService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Mock
    private Config config;

    @Mock
    private Call call;

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(CallUtilTest.class);
        PowerMockito.mockStatic(DateTime.class);
        dateTime = new DateTime();
        given(DateTime.now()).willReturn(dateTime);

        mainFlow = CallFlowHelper.createMainFlow();

        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        flow = FlowHelper.createFlow(raw);

        inboundCall = CallHelper.createInboundCall();
        outboundCall = CallHelper.createOutboundCall();
        voxeo = ConfigHelper.createConfigs().get(0);

        callParams = new HashMap<>();
        callParams.put(Constants.TEST_PARAM, Constants.TEST_VALUE);

        eventParams.put(Constants.PARAM_CONFIG, Constants.CONFIG_VOXEO);
        eventParams.put(Constants.PARAM_FLOW_NAME, Constants.CALLFLOW_MAIN);
        motechEvent = new MotechEvent(Events.CALLFLOWS_INITIATE_CALL, eventParams);
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
        given(request.getContextPath()).willReturn("/motech-platform-server/modules");

        // When
        String url = callUtil.buildContinuationUrl(request, inboundCall, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(url);
        assertThat(url, equalTo("http://localhost/motech-platform-server/modules/callflows/calls/" +
                                        inboundCall.getCallId() + ".vxml"));

    }

    @Test
    public void shouldReplaceParamsInString() {
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
        verifyZeroInteractions(callDataService);
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
        verify(callDataService, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldRetryIfCurrentActiveCallCountIsGreaterThanOutboundCallLimitAndCurrentRetriesIsLessThanRetryLimit()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDataService.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 1);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        motechEvent = new MotechEvent(Events.CALLFLOWS_OUTBOUND_CALL, eventParams);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDataService, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        assertThat(eventParams.get(Constants.PARAM_RETRY_ATTEMPTS).toString(), equalTo("2"));
        verify(schedulerService, times(1)).scheduleRunOnceJob(new RunOnceSchedulableJob(motechEvent, DateTime.now()
                                                                                                             .plusSeconds(
                                                                                                                     Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS)
                                                                                                             .toDate()));
    }

    @Test(expected = OperationNotSupportedException.class)
    public void shouldThrowOperationNotSupportedIfRetryCountIsGreaterThanMaxAllowedRetryAttemptsAndCallAllowedIsFalse()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDataService.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 6);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        given(config.getCallAllowed()).willReturn(false);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDataService, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
        verifyZeroInteractions(schedulerService);
    }

    @Test
    public void shouldAllowCallIfCurrentRetryLimitIsGreaterThanMaxAllowedRetryAttemptsAndCallAllowedIsTrue()
            throws OperationNotSupportedException {
        //Given
        given(config.getOutboundCallLimit()).willReturn(5);
        given(callDataService.countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet))
                .willReturn(10L);
        eventParams.put(Constants.PARAM_JOB_ID, outboundCall.getCallId());
        eventParams.put(Constants.PARAM_RETRY_ATTEMPTS, 6);
        given(config.getOutboundCallRetryAttempts()).willReturn(5);
        motechEvent = new MotechEvent(Events.CALLFLOWS_OUTBOUND_CALL, eventParams);
        given(config.getCallAllowed()).willReturn(true);

        //When
        callUtil.checkCallCanBePlaced(outboundCall, config, eventParams);

        //Then
        verify(config, times(2)).getOutboundCallLimit();
        verify(callDataService, times(1)).countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
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
        int[] arrayOfInt = { 1, 2, 3, 4 };
        long[] arrayOfLong = { 1L, 2L, 3L, 4L };
        float[] arrayOfFloat = { 1.1f, 2.2f, 3.3f };
        List<Integer> listOfInteger = Arrays.asList(1, 2, 3, 4);
        List<String> listOfString = Arrays.asList("One", "Two", "Three");
        String[] arrayOfString = { "One", "Two", "Three" };
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

    }

}
