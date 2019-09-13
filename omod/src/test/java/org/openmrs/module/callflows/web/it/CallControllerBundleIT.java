package org.openmrs.module.callflows.web.it;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
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
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.TestUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Call Controller Integration Tests
 *
 * @author bramak09
 */
@WebAppConfiguration
public class CallControllerBundleIT extends BaseModuleWebContextSensitiveTest {

	private static final String NOT_EXISTING_SERVICE_BEAN_NAME = "not.existing.Service";

	@Autowired
	private ConfigService configService;

	@Autowired
	private CallDao callDao;

	@Autowired
	private CallFlowService callFlowService;

	@Autowired
	private CallService callService;

	@Autowired
	private FlowService flowService;

	@Autowired
	private CallFlowDao callFlowDao;

	private List<Config> configs;

	private List<Renderer> renderers;

	private CallFlow mainFlow;

	private Call outboundCall;

	private Map<String, Object> params;

	private Flow flow;

	private UserNode userNode;

	private Template vxmlTemplate;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() throws IOException, CallFlowAlreadyExistsException {
		// Save only voxeo in the DB and not yo
		configs = ConfigHelper.createConfigs();
		configs.remove(1);
		configService.updateConfigs(configs);

		renderers = RendererHelper.createRenderers();
		renderers.remove(1);
		configService.updateRenderers(renderers);

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

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void tearDown() {
		configService.updateConfigs(new ArrayList());
		callDao.deleteAll();
		callFlowDao.deleteAll();
	}

	@Test
	public void shouldHandleIncoming() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.vxml"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_VXML));
	}

	@Test
	public void shouldHandleIncomingWithJsonExtension() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.json"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldHandleIncomingWithCallIdParamAndUseExistingOutboundCall() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.vxml")
				.param("internal.callid", outboundCall.getCallId()))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_VXML));
	}

	@Test
	public void shouldHandleIncomingWithJsonExtensionAndCallIdParamAndUseExistingOutboundCall() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.json")
				.param("internal.callid", outboundCall.getCallId()))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncoming() throws Exception {
		// Given a velocity directive that's missing a #end
		vxmlTemplate.setContent("#if ($missing) no end hee hee hee");
		mainFlow.setRaw(json(flow));
		callFlowService.update(mainFlow);

		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.vxml"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.PLAIN_TEXT));
	}

	@Test
	public void shouldReturnInternalServerErrorIfScriptErrorInHandleIncomingWithJsonExtension() throws Exception {
		// Given a velocity directive that's missing a #end in the text element used for json
		((TextElement) userNode.getBlocks().get(0).getElements().get(0)).setTxt("#if ($x) we won't close!");
		mainFlow.setRaw(json(flow));
		callFlowService.update(mainFlow);

		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.json"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncoming() throws Exception {
		// Given a bad service input by a call flow designer
		givenABadServiceExists();

		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.vxml"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.PLAIN_TEXT));
	}

	@Test
	public void shouldReturnInternalServerErrorIfServicesCouldNotBeLoadedInHandleIncomingWithJsonExtension()
			throws Exception {
		// Given
		givenABadServiceExists();

		mockMvc.perform(get("/callflows/in/voxeo/flows/MainFlow.json"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCall() throws Exception {
		mockMvc.perform(get("/callflows/in/yo-i-dont-exist/flows/MainFlow.vxml"))
				.andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
				.andExpect(content().contentType(Constants.PLAIN_TEXT));
	}

	@Test
	public void shouldReturnBadRequestIfBadConfigIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {
		mockMvc.perform(get("/callflows/in/yo-i-dont-exist/flows/MainFlow.json"))
				.andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCall() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/DontExistHereOrAnyWhereElse.vxml"))
				.andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
				.andExpect(content().contentType(Constants.PLAIN_TEXT));
	}

	@Test
	public void shouldReturnBadRequestIfBadFlowNameIsUsedInHandleIncomingCallWithJsonExtension() throws Exception {
		mockMvc.perform(get("/callflows/in/voxeo/flows/DontExistHereOrAnyWhereElse.json"))
				.andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	/* Call Continuation */
	/* ================= */

	@Test
	public void shouldHandleCallContinuation() throws Exception {
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".vxml"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_VXML));
	}

	@Test
	public void shouldHandleCallContinuationWithJsonExtension() throws Exception {
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".json"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldHandleCallContinuationWhenJumpToIsSpecified() throws Exception {

		CallFlow testFlow = CallFlowHelper.createTestFlow();
		testFlow.setRaw(TestUtil.loadFile("test_flow.json"));
		callFlowService.create(testFlow);
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".json")
				.param("jumpTo", "TestFlow"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldTerminateCallInHandleCallContinuationIfNotAbleToGetToAUserNode() throws Exception {
		// Given
		givenABadJumpFromASystemNodeThatLeadsToNowhere();
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".vxml"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_VXML));
	}

	@Test
	public void shouldTerminateCallInHandleCallContinuationWithJsonExtensionIfNotAbleToGetToAUserNode() throws Exception {
		// Given
		givenABadJumpFromASystemNodeThatLeadsToNowhere();
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".json"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuation() throws Exception {
		// Given
		givenACyclicLoopExists();
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".vxml"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.PLAIN_TEXT));
	}

	@Test
	public void shouldReturnInternalServerErrorForCyclicLoopDetectionInHandleCallContinuationWithJsonExtension()
			throws Exception {
		// Given
		givenACyclicLoopExists();
		mockMvc.perform(get("/callflows/calls/" + outboundCall.getCallId() + ".json"))
				.andExpect(status().is(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
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
		badServices.put("badSrvc", NOT_EXISTING_SERVICE_BEAN_NAME);
		configs.get(0).setServicesMap(badServices);
		configService.updateConfigs(configs);
	}

	private String json(Object obj) throws IOException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}
