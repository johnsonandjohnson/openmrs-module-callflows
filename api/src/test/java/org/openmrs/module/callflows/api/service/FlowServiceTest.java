package org.openmrs.module.callflows.api.service;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.FlowStep;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.FlowHelper;
import org.openmrs.module.callflows.api.service.impl.FlowServiceImpl;
import org.openmrs.module.callflows.api.util.FlowUtil;
import org.openmrs.module.callflows.api.util.TestUtil;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Flow Service Tests
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowServiceTest extends BaseTest {

    @InjectMocks
    private FlowService flowService = new FlowServiceImpl();

    @Mock
    private FlowUtil flowUtil;

    @Mock
    private CallFlowService callFlowService;

    private CallFlow mainFlow;

    private Flow expectedFlow;

    private Node entryNode;

    private Node entryHandlerNode;

    private Node activeNode;

    private Node activeHandlerNode;

    private Node inactiveNode;

    private Node inactiveHandlerNode;

    private VelocityContext context;

    @Before
    public void setUp() throws IOException {
        mainFlow = CallFlowHelper.createMainFlow();
        String raw = TestUtil.loadFile("main_flow.json");
        expectedFlow = FlowHelper.createFlow(raw);
        mainFlow.setRaw(raw);

        // Nodes
        entryNode = expectedFlow.getNodes().get(0);
        entryHandlerNode = expectedFlow.getNodes().get(1);
        activeNode = expectedFlow.getNodes().get(2);
        activeHandlerNode = expectedFlow.getNodes().get(3);
        inactiveNode = expectedFlow.getNodes().get(4);
        inactiveHandlerNode = expectedFlow.getNodes().get(5);

        // Given a MainFlow
        given(callFlowService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        // And the following parse behavior
        given(flowUtil.parse("|MainFlow.|", null)).willReturn(new String[]{"MainFlow", null});
        given(flowUtil.parse("|MainFlow.entry|", null)).willReturn(new String[]{"MainFlow", "entry"});
        given(flowUtil.parse("|entry|", expectedFlow.getName())).willReturn(new String[]{"MainFlow", "entry"});
        given(flowUtil.parse("|MainFlow.non-existent|", null)).willReturn(new String[]{"MainFlow", "non-existent"});
        given(flowUtil.parse("|MainFlow2.|", null)).willReturn(new String[]{"MainFlow2", null});
        given(flowUtil.parse("finished", expectedFlow.getName())).willThrow(new IllegalArgumentException("Bad format"));

        given(flowUtil.parse("|active|", expectedFlow.getName())).willReturn(new String[]{"MainFlow", "active"});
        given(flowUtil.parse("|active-handler|", expectedFlow.getName())).willReturn(new String[]{"MainFlow",
                "active-handler"});
        given(flowUtil.parse("|inactive|", expectedFlow.getName())).willReturn(new String[]{"MainFlow", "inactive"});
        given(flowUtil.parse("|inactive-handler|", expectedFlow.getName())).willReturn(new String[]{"MainFlow",
                "inactive-handler"});
        given(flowUtil.parse("|entry-handler|", expectedFlow.getName())).willReturn(new String[]{"MainFlow",
                "entry-handler"});

        // And one way to find a node
        given(flowUtil.getNodeByStep(expectedFlow, "entry")).willReturn(entryNode);
        given(flowUtil.getNodeByStep(expectedFlow, "entry-handler")).willReturn(entryHandlerNode);
        given(flowUtil.getNodeByStep(expectedFlow, "active")).willReturn(activeNode);
        given(flowUtil.getNodeByStep(expectedFlow, "active-handler")).willReturn(activeHandlerNode);
        given(flowUtil.getNodeByStep(expectedFlow, "inactive")).willReturn(inactiveNode);
        given(flowUtil.getNodeByStep(expectedFlow, "inactive-handler")).willReturn(inactiveHandlerNode);

        // Introducing a infinite loop
        context = new VelocityContext();
    }

    @Test
    public void shouldLoadFlowWithValidName() throws IOException {
        // Given a call flow by name MainFlow

        // When
        Flow flow = flowService.load(Constants.CALLFLOW_MAIN);

        // Then
        verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN);
        assertNotNull(flow);
        assertThat(flow.getName(), equalTo(Constants.CALLFLOW_MAIN));
    }

    @Test
    public void shouldThrowIllegalArgumentIfGivenInvalidNameDuringLoadFlow() {
        expectException(IllegalArgumentException.class);
        // Given a call flow by name MainFlow

        try {
            // When we look for main2 instead of main
            Flow flow = flowService.load(Constants.CALLFLOW_MAIN2);
        } finally {
            // Then
            verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfFlowIsBadlyFormattedDuringLoadFlow() {
        expectException(IllegalArgumentException.class);
        // Given a incomplete flow
        mainFlow.setRaw("");
        given(callFlowService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        try {
            // When we look for this incomplete flow
            Flow flow = flowService.load(Constants.CALLFLOW_MAIN);
        } finally {
            // Then
            verify(callFlowService, times(1)).findByName(Constants.CALLFLOW_MAIN);
        }
    }

    // Parse
    // =====

    @Test
    public void shouldParseValidFlow() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.entry|", null);

        // Then
        verify(flowUtil, times(1)).parse("|MainFlow.entry|", null);
        verify(callFlowService, times(1)).findByName("MainFlow");
        verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "entry");
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test
    public void shouldThrowIllegalArgumentForFlowNotFoundDuringParse() {
        expectException(IllegalArgumentException.class);

        try {
            // When
            FlowStep flowStep = flowService.parse("|MainFlow2.|", null);
        } finally {
            // Then
            verify(flowUtil, times(1)).parse("|MainFlow2.|", null);
            verify(callFlowService, times(1)).findByName("MainFlow2");
            verify(flowUtil, never()).getNodeByStep(any(Flow.class), anyString());
        }
    }

    @Test
    public void shouldThrowIllegalArgumentForStepNotFoundDuringParse() {
        expectException(IllegalArgumentException.class);

        try {
            // When
            FlowStep flowStep = flowService.parse("|MainFlow.non-existent|", null);
        } finally {
            // Then
            verify(flowUtil, times(1)).parse("|MainFlow.non-existent|", null);
            verify(callFlowService, times(1)).findByName("MainFlow");
            verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "non-existent");
        }

    }

    @Test
    public void shouldReturnFirstNodeOfFlowIfNodeWasNullDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.|", null);

        // Then
        verify(flowUtil, times(1)).parse("|MainFlow.|", null);
        verify(callFlowService, times(1)).findByName("MainFlow");
        verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "entry");
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test
    public void shouldSubstitutePassedFlowIfFlowInformationIsNotPresentDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|entry|", expectedFlow);

        // Then
        verify(flowUtil, times(1)).parse("|entry|", "MainFlow");
        verify(callFlowService, times(1)).findByName("MainFlow");
        verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "entry");
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

    @Test
    public void shouldEvalContinuouslyAndTerminateIfBadJumpIsEncountered() throws IOException {
        // Given that we introduce a waterfall that leads to a bad jump
        given(flowUtil.evalNode(expectedFlow, entryHandlerNode, context, "velocity")).willReturn("|active-handler|");
        given(flowUtil.evalNode(expectedFlow, activeHandlerNode, context, "velocity")).willReturn("|inactive-handler|");
        // Not surrounded by pipes, so bad jump
        given(flowUtil.evalNode(expectedFlow, inactiveHandlerNode, context, "velocity")).willReturn("finished");

        // When
        FlowPosition position = flowService.evalNode(expectedFlow, entryHandlerNode, context);

        // Then
        assertTrue(position.isTerminated());
        assertThat(position.getStartFlow().getName(), equalTo(Constants.CALLFLOW_MAIN));
        assertThat(position.getEndFlow().getName(), equalTo(Constants.CALLFLOW_MAIN));
        assertThat(position.getStart().getStep(), equalTo("entry-handler"));
        assertThat(position.getEnd().getStep(), equalTo("inactive-handler"));
        assertThat(position.getOutput(), equalTo("finished"));
    }

    @Test
    public void shouldEvalContinuouslyAndNotTerminateIfWeAreAbleToGoToAUserNode() throws IOException {
        // Given that we introduce a waterfall that jumps to a good user node at the end
        given(flowUtil.evalNode(expectedFlow, entryHandlerNode, context, "velocity")).willReturn("|active-handler|");
        given(flowUtil.evalNode(expectedFlow, activeHandlerNode, context, "velocity")).willReturn("|inactive-handler|");
        // now we jump to inactive again - a valid user name
        given(flowUtil.evalNode(expectedFlow, inactiveHandlerNode, context, "velocity")).willReturn("|inactive|");

        // When
        FlowPosition position = flowService.evalNode(expectedFlow, entryHandlerNode, context);

        // Then
        assertFalse(position.isTerminated());
        assertThat(position.getStartFlow().getName(), equalTo(Constants.CALLFLOW_MAIN));
        assertThat(position.getEndFlow().getName(), equalTo(Constants.CALLFLOW_MAIN));
        assertThat(position.getStart().getStep(), equalTo("entry-handler"));
        assertThat(position.getEnd().getStep(), equalTo("inactive"));
        assertThat(position.getOutput(), equalTo("|inactive|"));
    }

    @Test
    public void shouldThrowIllegalStateWhenLongRunningLoopsAreDetected() throws IOException {
        expectException(IllegalStateException.class);

        // Given that we introduce a infinite loop between two nodes
        given(flowUtil.evalNode(expectedFlow, entryHandlerNode, context, "velocity")).willReturn("|active-handler|");
        given(flowUtil.evalNode(expectedFlow, activeHandlerNode, context, "velocity")).willReturn("|entry-handler|");

        // When
        flowService.evalNode(expectedFlow, entryHandlerNode, context);
    }

}


