package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.FlowPosition;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.flow.Node;
import com.janssen.connectforlife.callflows.domain.flow.TextElement;
import com.janssen.connectforlife.callflows.domain.flow.UserNode;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;
import com.janssen.connectforlife.callflows.service.CallFlowService;
import com.janssen.connectforlife.callflows.service.CallService;
import com.janssen.connectforlife.callflows.service.ConfigService;
import com.janssen.connectforlife.callflows.service.FlowService;
import com.janssen.connectforlife.callflows.util.CallUtil;
import com.janssen.connectforlife.callflows.util.FlowUtil;
import com.janssen.connectforlife.callflows.util.TestUtil;

import org.motechproject.mds.util.ServiceUtil;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.DefaultRequestBuilder;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Call Controller Unit Test Cases
 * We use @Spy for a couple of the util classes as these are mostly stand-alone utils without additional dependencies
 * CallUtil has a dependency on HttpServletRequest, but that's anyway mocked by MockMvc
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceUtil.class })
public class CallControllerTest extends BaseTest {

    private static final String LOCALHOST = "localhost";

    private static final String CONTEXT_PATH = "/motech-platform-server/modules";

    private MockMvc mockMvc;

    @InjectMocks
    private CallController callController = new CallController();

    @Mock
    private ConfigService configService;

    @Mock
    private CallFlowService callFlowService;

    @Mock
    private CallService callService;

    @Mock
    private FlowService flowService;

    @Mock
    private BundleContext bundleContext;

    @Spy
    @InjectMocks
    private FlowUtil flowUtil = new FlowUtil();

    @Spy
    @InjectMocks
    private CallUtil callUtil = new CallUtil();

    @Mock
    private HttpServletRequest request;

    private ArgumentCaptor<VelocityContext> velocityContextCaptor;

    private ArgumentCaptor<Call> callCaptor;

    private Config voxeo;

    private CallFlow mainFlow;

    private Flow flow;

    private FlowPosition flowPosition;

    private Node entryHandlerNode;

    private Node inactiveNode;

    private Long steps;

    private Call inboundCall;

    private String nextURLFormat = "http://localhost/motech-platform-server/modules/callflows/calls/%s.%s";

    private String inboundNextURLVxml;

    private String inboundNextURLJson;

    private Call outboundCall;

    private String outboundNextURLVxml;

    private String outboundNextURLJson;

    private Map<String, Object> context;

    private Map<String, String> servicesMap;

    private static final String CALL_SERVICE_CLASS = "com.janssen.connectforlife.callflows.service.CallService";

    @Before
    public void setUp() throws IOException {
        // initialize
        PowerMockito.mockStatic(ServiceUtil.class);
        mockMvc = MockMvcBuilders.standaloneSetup(callController).build();
        callController.initialize();

        // Load Flows and node strings from JSON Files
        String raw = TestUtil.loadFile("main_flow.json");

        // Contexts
        context = new HashMap<>();

        // Config
        voxeo = ConfigHelper.createConfigs().get(0);
        servicesMap = new HashMap<>();
        // We'll use a service that we can use for integration testing also
        servicesMap.put("callService", CALL_SERVICE_CLASS);
        voxeo.setServicesMap(servicesMap);
        given(configService.getConfig(Constants.CONFIG_VOXEO)).willReturn(voxeo);
        given(configService.getConfig(Constants.CONFIG_YO)).willThrow(new IllegalArgumentException(Constants.ERROR_YO));

        // Call Flow Service
        mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setRaw(raw);
        mainFlow.setId(1L);

        given(callFlowService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);
        given(callFlowService.findByName(Constants.CALLFLOW_MAIN2)).willThrow(new IllegalArgumentException(Constants.ERROR_MAIN_FLOW2));

        // bundleContext
        given(ServiceUtil.getServiceForInterfaceName(bundleContext, CALL_SERVICE_CLASS)).willReturn("callService");

        // Flow Service
        flow = FlowHelper.createFlow(raw);
        entryHandlerNode = flow.getNodes().get(1);
        inactiveNode = flow.getNodes().get(4);
        given(flowService.load(Constants.CALLFLOW_MAIN)).willReturn(flow);
        flowPosition = new FlowPosition();
        flowPosition.setStart(entryHandlerNode)
                    .setEnd(inactiveNode)
                    .setTerminated(false)
                    .setOutput("|active|")
                    .setStartFlow(flow)
                    .setEndFlow(flow);

        given(flowService.evalNode(eq(flow),
                                   eq(entryHandlerNode),
                                   any(VelocityContext.class))).willReturn(flowPosition);

        // Call Service
        inboundCall = CallHelper.createInboundCall();
        inboundCall.setStatus(CallStatus.IN_PROGRESS);
        steps = inboundCall.getSteps();

        outboundCall = CallHelper.createOutboundCall();

        given(callService.create(Constants.CONFIG_VOXEO,
                                 mainFlow,
                                 Constants.CALLFLOW_MAIN_ENTRY,
                                 CallDirection.INCOMING,
                                 null)).willReturn(inboundCall);

        given(callService.findByCallId(inboundCall.getCallId())).willReturn(inboundCall);
        given(callService.findByCallId(outboundCall.getCallId())).willReturn(outboundCall);

        // Given
        velocityContextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        callCaptor = ArgumentCaptor.forClass(Call.class);

        inboundNextURLVxml = String.format(nextURLFormat,
                                           Constants.INBOUND_CALL_ID.toString(),
                                           Constants.CONFIG_RENDERER_VXML);
        inboundNextURLJson = String.format(nextURLFormat,
                                           Constants.INBOUND_CALL_ID.toString(),
                                           Constants.CONFIG_RENDERER_JSON);
        outboundNextURLVxml = String.format(nextURLFormat,
                                            Constants.OUTBOUND_CALL_ID.toString(),
                                            Constants.CONFIG_RENDERER_VXML);
        outboundNextURLJson = String.format(nextURLFormat,
                                            Constants.OUTBOUND_CALL_ID.toString(),
                                            Constants.CONFIG_RENDERER_JSON);
    }

    @Test
    public void shouldHandleIncoming() throws Exception {

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.vxml"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_VXML))
               .andExpect((content().string(sameAsFile("main_flow_entry.vxml"))));

        // Then config and flow must have loaded correctly
        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        // And a call created
        assertCallCreated(Constants.CONFIG_VOXEO, CallDirection.INCOMING);
        // And since we are creating a call, there's no need to find a call
        assertCallNeverSearched();
        // And after node evaluation, we should persist the context with the call object
        verify(callUtil, times(1)).mergeContextWithCall(velocityContextCaptor.capture(), callCaptor.capture());
        // And we also expect a update to the call, so that the context is saved
        verify(callService, times(1)).update(inboundCall);
        // And let's look at what got persisted with the call data is what we wanted to do
        assertContext(velocityContextCaptor.getValue(), inboundCall.getCallId(), inboundNextURLVxml);
    }

    @Test
    public void shouldHandleIncomingWithJsonExtension() throws Exception {

        // When we make a inbound call with json extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.json"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("main_flow_entry_with_body.json")));

        // Then config and flow must have loaded correctly
        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        // And a call created
        assertCallCreated(Constants.CONFIG_VOXEO, CallDirection.INCOMING);
        // And since we are creating a call, there's no need to find a call
        assertCallNeverSearched();
        // And after node evaluation, we should persist the context with the call object
        verify(callUtil, times(1)).mergeContextWithCall(velocityContextCaptor.capture(), callCaptor.capture());
        // And we also expect a update to the call, so that the context is saved
        verify(callService, times(1)).update(inboundCall);
        // And let's look at what got persisted with the call data is what we wanted to do
        assertContext(velocityContextCaptor.getValue(), inboundCall.getCallId(), inboundNextURLJson);
    }

    @Test
    public void shouldHandleIncomingWithCallIdParamAndUseExistingOutboundCall() throws Exception {

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.vxml?internal.callId=" + outboundCall.getCallId()))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_VXML))
               .andExpect(content().string(sameAsFile("main_flow_entry_outbound.vxml")));

        // Then config and flow must have loaded correctly
        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        // And since this is coming as part of a outbound call, we *don't* create a call
        assertCallNotCreated();
        // And since we are trying to retrieve a outbound call, we *do* need to find it
        verify(callService, times(1)).findByCallId(Constants.OUTBOUND_CALL_ID.toString());
        // And because we are using a existing call, we also have to load the context once
        verify(callUtil, times(1)).mergeCallWithContext(any(Call.class), any(VelocityContext.class));
        // And after node evaluation, we should persist the context with the call object
        verify(callUtil, times(1)).mergeContextWithCall(velocityContextCaptor.capture(), callCaptor.capture());
        // And we also expect a update to the call, so that the context is saved
        verify(callService, times(1)).update(outboundCall);
        // And let's look at what got persisted with the call data is what we wanted to do
        assertContext(velocityContextCaptor.getValue(), outboundCall.getCallId(), outboundNextURLVxml);
    }

    @Test
    public void shouldHandleIncomingWithJsonExtensionAndCallIdParamAndUseExistingOutboundCall() throws Exception {

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.json?internal.callId=" + outboundCall.getCallId()))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("main_flow_entry_outbound_with_body.json")));

        // Then config and flow must have loaded correctly
        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        // And since this is coming as part of a outbound call, we don't create a call
        assertCallNotCreated();
        // And since we are trying to retrieve a outbound call, we need to find it
        verify(callService, times(1)).findByCallId(Constants.OUTBOUND_CALL_ID.toString());
        // And because we are using a existing call, we also have to load the context once
        verify(callUtil, times(1)).mergeCallWithContext(any(Call.class), any(VelocityContext.class));
        // And after node evaluation, we should persist the context with the call object
        verify(callUtil, times(1)).mergeContextWithCall(velocityContextCaptor.capture(), callCaptor.capture());
        // And we also expect a update to the call, so that the context is saved
        verify(callService, times(1)).update(outboundCall);
        // And let's look at what got persisted with the call data is what we wanted to do
        assertContext(velocityContextCaptor.getValue(), outboundCall.getCallId(), outboundNextURLJson);
    }

    @Test
    public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncoming() throws Exception {
        // Given a bad script where we don't close the #if directive of velocity with a #end
        flow.getNodes()
            .get(0)
            .getTemplates()
            .get(Constants.CONFIG_RENDERER_VXML)
            .setContent("#if ($x) we won't close!");

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.vxml"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.PLAIN_TEXT))
               .andExpect(content().string(Constants.ERROR_SCRIPT));

        //TODO: Can possibly replace this with a standard error response in VXML itself by changing config?

        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        assertCallNeverSearched();
        assertCallFailureUpdated();

    }

    @Test
    public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncomingWithJsonExtension() throws Exception {
        // Given a bad script where we don't close the #if directive of velocity with a #end
        UserNode userNode = (UserNode) flow.getNodes().get(0);
        ((TextElement) userNode.getBlocks().get(0).getElements().get(0)).setTxt("#if ($x) we won't close!");

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.json"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(containsString("Encountered \\\"<EOF>\\\" at MainFlow.entry[line 1, column 24]")));
        // We check enough of the content to have confidence in the velocity error, but not the complete text as the string
        // is too deeply nested and on windows and unix escapes with different line endings

        assertConfigAndFlowLoaded(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN);
        assertCallNeverSearched();
        assertCallFailureUpdated();
    }

    @Test
    public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncoming() throws Exception {
        // Given a badly designed callflow where someone gave a service that could simply not be loaded even if we made a trip to mars
        Map<String, String> srvcMap = new HashMap<>();
        srvcMap.put("NOT_FOUND_SERVICE", "outer.space.mars.orbiter.GoodForNothingService");
        voxeo.setServicesMap(srvcMap);

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.vxml"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.PLAIN_TEXT))
               .andExpect(content().string(Constants.ERROR_SYSTEM));

        //TODO: Can possibly replace this with a standard error response in VXML itself by changing config and register a error template?

        // Then we should have tried to load the config cause the OSGI services are part of the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_VOXEO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, never()).findByName(anyString());
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    @Test
    public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncomingWithJsonExtension()
            throws Exception {
        // Given a badly designed callflow where someone gave a service that could simply not be loaded even if we made a trip to mars
        Map<String, String> srvcMap = new HashMap<>();
        srvcMap.put("NOT_FOUND_SERVICE", "outer.space.mars.orbiter.GoodForNothingService");
        voxeo.setServicesMap(srvcMap);

        // When we make a inbound call with vxml extension
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow.json"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("error_bad_services.json")));

        // Then we should have tried to load the config cause the OSGI services are part of the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_VOXEO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, never()).findByName(anyString());
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    @Test
    public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCall() throws Exception {

        // When we make a inbound call with a configuration (yo) that's not defined in the system yet
        mockMvc.perform(customGet("/in/yo/flows/MainFlow.vxml"))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.PLAIN_TEXT))
               .andExpect(content().string(Constants.ERROR_CONFIG));
        // Then we should have tried to load the config cause the OSGI services are part of the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_YO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, never()).findByName(anyString());
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    @Test
    public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {

        // When we make a inbound call with a configuration (yo) that's not defined in the system yet
        mockMvc.perform(customGet("/in/yo/flows/MainFlow.json"))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("error_bad_config.json")));
        // Then we should have tried to load the config cause the OSGI services are part of the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_YO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, never()).findByName(anyString());
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    @Test
    public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCall() throws Exception {

        // When we make a inbound call with a flow that's not defined in the system yet
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow2.vxml"))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.PLAIN_TEXT))
               .andExpect(content().string(Constants.ERROR_CALLFLOW));
        // Then we should have tried to load the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_VOXEO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    @Test
    public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {

        // When we make a inbound call with a flow that's not defined in the system yet
        mockMvc.perform(customGet("/in/voxeo/flows/MainFlow2.json"))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("error_bad_callflow.json")));
        // Then we should have tried to load the config
        verify(configService, times(1)).getConfig(Constants.CONFIG_VOXEO);
        // And we should NOT have loaded the callflow
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        // And nothing happened with calls
        assertNoActionOnCall();
    }

    /* Handle Call Continuation */
    /* ======================== */

    @Test
    public void shouldHandleCallContinuation() throws Exception {

        // When we make a call continuation request for a existing inbound call with vxml
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".vxml"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_VXML))
               .andExpect(content().string(sameAsFile("main_flow_inactive.vxml")));

        // Then we should have tried to load the call first and then config and then flow
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And we must have updated the call eventually by incrementing the steps and status to as set
        assertCallUpdatedWithIncrementedSteps(CallStatus.IN_PROGRESS);
    }

    @Test
    public void shouldHandleCallContinuationWithJsonExtension() throws Exception {
        // When we make a call continuation request for a existing inbound call with json
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".json"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("main_flow_inactive_with_body.json")));

        // Then we should have tried to load the call first and then config and then flow
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And we must have updated the call eventually by incrementing the steps and status to as set
        assertCallUpdatedWithIncrementedSteps(CallStatus.IN_PROGRESS);
    }

    @Test
    public void shouldTerminateCallInHandleCallContinuationIfNotAbleToGetToAUserNode() throws Exception {
        // Given
        flowPosition.setTerminated(true);

        // When we make a call continuation request for a existing inbound call with vxml
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".vxml"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_VXML))
               .andExpect(content().string("|active|"));

        // Then call, config and flow must be loaded
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And we must have updated the call eventually by incrementing the steps and status to as set
        assertCallUpdatedWithIncrementedSteps(CallStatus.COMPLETED);
    }

    @Test
    public void shouldTerminateCallInHandleCallContinuationWithJsonExtensionIfNotAbleToGetToAUserNode()
            throws Exception {
        // Given
        flowPosition.setTerminated(true);

        // When we make a call continuation request for a existing inbound call with json
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".json"))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("main_flow_inactive_terminated_with_body.json")));

        // Then
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And we must have updated the call eventually by incrementing the steps and status to as set
        assertCallUpdatedWithIncrementedSteps(CallStatus.COMPLETED);
    }

    @Test
    public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuation() throws Exception {
        // Given a cyclic loop that will throw a illegal state exception when evaluating a node
        given(flowService.evalNode(eq(flow),
                                   eq(entryHandlerNode),
                                   any(VelocityContext.class))).willThrow(new IllegalStateException("Cyclic Loop!"));

        // When we make a call continuation request for a existing inbound call with json
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".vxml"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.PLAIN_TEXT))
               .andExpect(content().string("error:SYSTEM:system error"));
        // Then
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And call must be updated with failure
        assertCallFailureUpdated();
    }

    @Test
    public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuationWithJsonExtension()
            throws Exception {
        // Given a cyclic loop that will throw a illegal state exception when evaluating a node
        given(flowService.evalNode(eq(flow),
                                   eq(entryHandlerNode),
                                   any(VelocityContext.class))).willThrow(new IllegalStateException("Cyclic Loop!"));

        // When we make a call continuation request for a existing inbound call with json
        mockMvc.perform(customGet("/calls/" + inboundCall.getCallId() + ".json"))
               .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(sameAsFile("error_cyclic_loop.json")));
        // Then
        assertCallConfigFlowLoaded(inboundCall, Constants.CONFIG_VOXEO, mainFlow.getName());
        // And we evaluated the next node
        verify(flowService, times(1)).evalNode(flow, entryHandlerNode, velocityContextCaptor.getValue());
        // And call must be updated with failure
        assertCallFailureUpdated();
    }

    private void assertContext(VelocityContext context, String callId, String nextURL) {
        assertNotNull(context);
        assertTrue(context.containsKey("internal"));
        Map<String, Object> internalContext = (Map<String, Object>) context.get("internal");
        assertThat(internalContext.size(), equalTo(2));
        assertThat((String) internalContext.get("callId"), equalTo(callId));
        assertThat((String) internalContext.get("nextURL"), equalTo(nextURL));

    }

    private void assertCallConfigFlowLoaded(Call call, String config, String callflow) {
        // These must be loaded
        verify(callService, times(1)).findByCallId(call.getCallId());
        verify(configService, times(1)).getConfig(config);
        verify(flowService, times(1)).load(callflow);
        // And we called the merge to load previously persisted data
        verify(callUtil, times(1)).mergeCallWithContext(eq(inboundCall), velocityContextCaptor.capture());
    }

    private void assertConfigAndFlowLoaded(String config, String callflow) {
        // Then we have to find the configuration
        verify(configService, times(1)).getConfig(config);
        // And we need to load the flow
        verify(callFlowService, times(1)).findByName(callflow);
    }

    private void assertCallCreated(String config, CallDirection direction) {
        verify(callService, times(1)).create(config, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, direction, null);
    }

    private void assertCallNotCreated() {
        verify(callService, never()).create(anyString(),
                                            any(CallFlow.class),
                                            anyString(),
                                            any(CallDirection.class),
                                            any(Map.class));
    }

    private void assertCallFailureUpdated() {
        ArgumentCaptor<Call> callArgumentCaptor = ArgumentCaptor.forClass(Call.class);
        verify(callService, times(1)).update(callArgumentCaptor.capture());
        Call failedCall = callArgumentCaptor.getValue();
        assertThat(failedCall.getStatus(), equalTo(CallStatus.FAILED));
    }

    private void assertCallUpdatedWithIncrementedSteps(CallStatus status) {
        // Before updating the call we expect a merge back operation
        verify(callUtil, times(1)).mergeContextWithCall(velocityContextCaptor.getValue(), inboundCall);
        verify(callService, times(1)).update(callCaptor.capture());
        // And we expect the steps to be incremented in that
        Call updatedCall = callCaptor.getValue();
        assertThat(updatedCall.getSteps(), equalTo(steps + 1));
        assertThat(updatedCall.getStatus(), equalTo(status));
    }

    private void assertNoActionOnCall() {
        verifyZeroInteractions(callService);
    }

    private void assertCallNeverSearched() {
        verify(callService, never()).findByCallId(anyString());
        // And of course there's no need to merge the call with the context since we are creating it
        verify(callUtil, never()).mergeCallWithContext(any(Call.class), any(VelocityContext.class));
    }

    private String sameAsFile(String file) throws IOException {
        return TestUtil.loadFile(file);
    }

    private DefaultRequestBuilder customGet(String urlTemplate, Object... urlVariables) {
        return get(CONTEXT_PATH + urlTemplate, urlVariables).header("Host", LOCALHOST).contextPath(CONTEXT_PATH);
    }
}
