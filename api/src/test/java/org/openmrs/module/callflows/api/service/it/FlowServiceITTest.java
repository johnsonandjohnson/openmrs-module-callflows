package org.openmrs.module.callflows.api.service.it;

import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.FlowStep;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.FlowHelper;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.evaluation.EvaluationCommand;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.TestUtil;

import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Flow Service Integration Tests
 *
 * @author bramak09
 */
public class FlowServiceITTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private FlowService flowService;

    @Autowired
    private CallFlowDao callFlowDao;

    @Autowired
    private EvaluationCommand evaluationCommand;

    private CallFlow mainFlow;

    private Flow loadedFlow;

    private Node activeHandlerNode;

    private Node inactiveHandlerNode;

    private Node inactiveNode;

    private Node entryHandlerNode;

    private VelocityContext context;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        mainFlow = CallFlowHelper.createMainFlow();
        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        callFlowDao.create(mainFlow);

        loadedFlow = FlowHelper.createFlow(raw);
        entryHandlerNode = loadedFlow.getNodes().get(1);
        activeHandlerNode = loadedFlow.getNodes().get(3);
        inactiveNode = loadedFlow.getNodes().get(4);
        inactiveHandlerNode = loadedFlow.getNodes().get(5);

        objectMapper = new ObjectMapper();
        context = new VelocityContext();
        Context.logout();
        Module module = new Module("callflows");
        module.setModuleId("callflows");
        Method m = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        m.setAccessible(true); //if security settings allow this
        Object o = m.invoke(null, module); //use null if the method is static
        evaluationCommand.setDaemonToken((DaemonToken) o);
    }

    @After
    public void tearDown() {
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(flowService);
    }

    @Test
    public void shouldLoadFlowWithValidName() throws IOException {
        // Given a flow named MainFlow

        // When
        Flow flow = flowService.load(Constants.CALLFLOW_MAIN);

        // Then
        assertNotNull(flow);
        assertThat(flow.getName(), equalTo(Constants.CALLFLOW_MAIN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfGivenInvalidNameDuringLoadFlow() {
        // Given a flow named MainFlow

        // When we look for main2 instead of main
        Flow flow = flowService.load(Constants.CALLFLOW_MAIN2);

        // Then expect a exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfFlowIsBadlyFormattedDuringLoadFlow() {
        // Given a incomplete flow
        mainFlow.setRaw("");
        callFlowDao.update(mainFlow);

        // When we look for this flow
        Flow flow = flowService.load(Constants.CALLFLOW_MAIN);

        // Then we expect a exception
    }

    // Parse
    // =====

    @Test
    public void shouldParseValidFlow() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.entry|", null);

        // Then
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentForFlowNotFoundDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow2.|", null);

        // Then exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentForStepNotFoundDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.non-existent|", null);

        // Then exception
    }

    @Test
    public void shouldReturnFirstNodeOfFlowIfNodeWasNullDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.|", null);

        // Then
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test
    public void shouldSubstitutePassedFlowIfFlowInformationIsNotPresentDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|entry|", loadedFlow);

        // Then
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test
    public void shouldEvalContinuouslyAndTerminateIfBadJumpIsEncountered() throws IOException {

        // Given entry-handler is set to go to active-handler
        entryHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|active-handler|");
        // And active-handler to inactive-handler
        activeHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|inactive-handler|");
        // And inactive-handler to not a known place
        inactiveHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("am_in_a_bad_place");
        mainFlow.setRaw(objectMapper.writeValueAsString(loadedFlow));
        callFlowDao.update(mainFlow);

        // When
        FlowPosition position = flowService.evalNode(loadedFlow, entryHandlerNode, context);

        // Then
        assertNotNull(position);
        assertTrue(position.isTerminated());
        // Then since we couldn't jump, our end node is in the last correct node
        assertThat(position.getEnd(), equalTo(inactiveHandlerNode));
    }

    @Test
    public void shouldEvalContinuouslyAndNotTerminateIfWeAreAbleToGoToAUserNode() throws IOException {

        // Given entry-handler is set to go to active-handler
        entryHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|active-handler|");
        // And active-handler to inactive-handler
        activeHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|inactive-handler|");
        // And inactive-handler to inactive
        inactiveHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|inactive|");
        mainFlow.setRaw(objectMapper.writeValueAsString(loadedFlow));
        callFlowDao.update(mainFlow);

        // When
        FlowPosition position = flowService.evalNode(loadedFlow, entryHandlerNode, context);

        // Then
        assertNotNull(position);
        assertFalse(position.isTerminated());
        assertThat(position.getEnd(), equalTo(inactiveNode));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateWhenLongRunningLoopsAreDetected() throws IOException {
        // Given
        entryHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|active-handler|");
        activeHandlerNode.getTemplates().get(Constants.VELOCITY).setContent("|entry-handler|");
        mainFlow.setRaw(objectMapper.writeValueAsString(loadedFlow));
        callFlowDao.update(mainFlow);

        // When we try to run from entry-handler
        flowService.evalNode(loadedFlow, entryHandlerNode, context);
    }

}

