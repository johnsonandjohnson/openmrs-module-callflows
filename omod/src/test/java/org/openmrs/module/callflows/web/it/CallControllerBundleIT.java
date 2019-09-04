package org.openmrs.module.callflows.web.it;

import org.openmrs.module.callflows.api.Constants;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Template;
import org.openmrs.module.callflows.api.domain.flow.TextElement;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.RendererHelper;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.service.SettingsService;
import org.openmrs.module.callflows.api.util.TestUtil;

import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Call Controller Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallControllerBundleIT extends RESTControllerPaxIT {

    @Inject
    private SettingsService settingsService;

    @Inject
    private CallDao callDao;

    @Inject
    private CallFlowService callFlowService;

    @Inject
    private CallService callService;

    @Inject
    private FlowService flowService;

    @Inject
    private CallFlowDao callFlowDao;

    private List<Config> configs;

    private List<Renderer> renderers;

    private CallFlow mainFlow;

    private Call outboundCall;

    private Map<String, Object> params;

    private Flow flow;

    private UserNode userNode;

    private Template vxmlTemplate;

    @Before
    public void setUp() throws IOException, CallFlowAlreadyExistsException {

        // Save only voxeo in the DB and not yo
        configs = ConfigHelper.createConfigs();
        configs.remove(1);
        settingsService.updateConfigs(configs);

        renderers = RendererHelper.createRenderers();
        renderers.remove(1);
        settingsService.updateRenderers(renderers);

        // create a callflow
        mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setRaw(TestUtil.loadFile("main_flow.json"));
        callFlowService.create(mainFlow);

        // create a outbound call
        params = new HashMap<>();
        outboundCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.OUTGOING,
                                          params);

        // load flow
        flow = flowService.load(Constants.CALLFLOW_MAIN);
        userNode = (UserNode) flow.getNodes().get(0);
        vxmlTemplate = userNode.getTemplates().get(Constants.CONFIG_RENDERER_VXML);

    }

    @After
    public void tearDown() {
        super.tearDown();
        settingsService.updateConfigs(new ArrayList());
        callDao.deleteAll();
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldHandleIncoming() throws Exception {

        // When we make a inbound call request with Voice XML
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_VXML);
    }

    @Test
    public void shouldHandleIncomingWithJsonExtension() throws Exception {

        // When we make a inbound call request with JSON
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldHandleIncomingWithCallIdParamAndUseExistingOutboundCall() throws Exception {

        // When we make a inbound request to handle a already created outbound call
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.vxml",
                                          "internal.callid",
                                          outboundCall.getCallId());
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_VXML);
    }

    @Test
    public void shouldHandleIncomingWithJsonExtensionAndCallIdParamAndUseExistingOutboundCall() throws Exception {

        // When we make a inbound JSON request to handle a already created outbound call
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.json",
                                          "internal.callid",
                                          outboundCall.getCallId());
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncoming() throws Exception {
        // Given a velocity directive that's missing a #end
        vxmlTemplate.setContent("#if ($missing) no end hee hee hee");
        mainFlow.setRaw(json(flow));
        callFlowService.update(mainFlow);

        // When we make a inbound call request with JSON
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.PLAIN_TEXT);
    }

    @Test
    public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncomingWithJsonExtension() throws Exception {
        // Given a velocity directive that's missing a #end in the text element used for json
        ((TextElement) userNode.getBlocks().get(0).getElements().get(0)).setTxt("#if ($x) we won't close!");
        mainFlow.setRaw(json(flow));
        callFlowService.update(mainFlow);

        // When we make a inbound call request with JSON
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncoming() throws Exception {
        // Given a bad service input by a call flow designer
        givenABadServiceExists();

        // When we make a inbound call request with VXML
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.PLAIN_TEXT);
    }

    @Test
    public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncomingWithJsonExtension()
            throws Exception {
        // Given
        givenABadServiceExists();

        // When we make a inbound call request with JSON
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/MainFlow.json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCall() throws Exception {

        // When we make a inbound call request with VXML and a bad non existent config
        HttpGet httpGet = buildGetRequest("/callflows/in/yo-i-dont-exist/flows/MainFlow.vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_BAD_REQUEST, Constants.PLAIN_TEXT);
    }

    @Test
    public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {

        // When we make a inbound call request with JSON and a bad non existent config
        HttpGet httpGet = buildGetRequest("/callflows/in/yo-i-dont-exist/flows/MainFlow.json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_BAD_REQUEST, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCall() throws Exception {

        // When we make a inbound call request with VXML and a bad non existent callflow
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/DontExistHereOrAnyWhereElse.vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_BAD_REQUEST, Constants.PLAIN_TEXT);
    }

    @Test
    public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {

        // When we make a inbound call request with JSON and a bad non existent callflow
        HttpGet httpGet = buildGetRequest("/callflows/in/voxeo/flows/DontExistHereOrAnyWhereElse.json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_BAD_REQUEST, Constants.APPLICATION_JSON_UTF8);
    }

    /* Call Continuation */
    /* ================= */

    @Test
    public void shouldHandleCallContinuation() throws Exception {
        // When we make a call continuation request for a created call
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_VXML);
    }

    @Test
    public void shouldHandleCallContinuationWithJsonExtension() throws Exception {
        // When we make a call continuation request for a created call with json extension
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldHandleCallContinuationWhenJumpToIsSpecified() throws Exception {

        CallFlow testFlow = CallFlowHelper.createTestFlow();
        testFlow.setRaw(TestUtil.loadFile("test_flow.json"));
        callFlowService.create(testFlow);

        // When we make a call continuation request for a created call with JumpTo specified
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".json", "jumpTo",
                                          "TestFlow");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldTerminateCallInHandleCallContinuationIfNotAbleToGetToAUserNode() throws Exception {
        // Given
        givenABadJumpFromASystemNodeThatLeadsToNowhere();

        // When we make a inbound call continuation request for a created call
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_VXML);
    }

    @Test
    public void shouldTerminateCallInHandleCallContinuationWithJsonExtensionIfNotAbleToGetToAUserNode()
            throws Exception {
        // Given
        givenABadJumpFromASystemNodeThatLeadsToNowhere();

        // When we make a inbound call continuation request for a created call
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_OK, Constants.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuation() throws Exception {
        // Given
        givenACyclicLoopExists();

        // When we make a inbound call continuation request for a created call
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".vxml");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.PLAIN_TEXT);
    }

    @Test
    public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuationWithJsonExtension()
            throws Exception {
        // Given
        givenACyclicLoopExists();

        // When we make a inbound call continuation request for a created call
        HttpGet httpGet = buildGetRequest("/callflows/calls/" + outboundCall.getCallId() + ".json");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertResponseToBe(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.APPLICATION_JSON_UTF8);
    }

    private void givenACyclicLoopExists() throws IOException, CallFlowAlreadyExistsException {
        flow.getNodes().get(1).getTemplates().get(Constants.VELOCITY).setContent("|active-handler|");
        flow.getNodes().get(3).getTemplates().get(Constants.VELOCITY).setContent("|entry-handler|");
        mainFlow.setRaw(json(flow));
        callFlowService.update(mainFlow);
    }

    private void givenABadJumpFromASystemNodeThatLeadsToNowhere() throws IOException, CallFlowAlreadyExistsException {
        flow.getNodes().get(1).getTemplates().get(Constants.VELOCITY).setContent("|active-handler|");
        flow.getNodes().get(3).getTemplates().get(Constants.VELOCITY).setContent("|inactive-handler|");
        flow.getNodes().get(5).getTemplates().get(Constants.VELOCITY).setContent("no where in particular");
        mainFlow.setRaw(json(flow));
        callFlowService.update(mainFlow);
    }

    private void givenABadServiceExists() {
        Map<String, String> badServices = new HashMap<>();
        badServices.put("badSrvc", "com.underground.missing.but.useful.if.found.Service");
        configs.get(0).setServicesMap(badServices);
        settingsService.updateConfigs(configs);
    }

}
