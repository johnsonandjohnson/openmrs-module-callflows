package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.FlowStep;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.impl.FlowServiceImpl;
import com.janssen.connectforlife.callflows.util.FlowUtil;
import com.janssen.connectforlife.callflows.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
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
    private CallFlowDataService callFlowDataService;

    private CallFlow mainFlow;

    private Flow expectedFlow;

    @Before
    public void setUp() throws IOException {
        mainFlow = CallFlowHelper.createMainFlow();
        String raw = TestUtil.loadFile("main_flow.json");
        expectedFlow = FlowHelper.createFlow(raw);
        mainFlow.setRaw(raw);

        // Given a MainFlow
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);
        // And the following parse behavior
        given(flowUtil.parse("|MainFlow.|", null)).willReturn(new String[]{ "MainFlow", null });
        given(flowUtil.parse("|MainFlow.entry|", null)).willReturn(new String[]{ "MainFlow", "entry" });
        given(flowUtil.parse("|entry|", expectedFlow.getName())).willReturn(new String[]{ "MainFlow", "entry" });
        given(flowUtil.parse("|MainFlow.non-existent|", null)).willReturn(new String[]{ "MainFlow", "non-existent" });
        given(flowUtil.parse("|MainFlow2.|", null)).willReturn(new String[]{ "MainFlow2", null });
        // And one way to find a node
        given(flowUtil.getNodeByStep(expectedFlow, "entry")).willReturn(expectedFlow.getNodes().get(0));
    }

    @Test
    public void shouldLoadFlowWithValidName() throws IOException {
        // Given a call flow by name MainFlow

        // When
        Flow flow = flowService.load(Constants.CALLFLOW_MAIN);

        // Then
        verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN);
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
            verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfFlowIsBadlyFormattedDuringLoadFlow() {
        expectException(IllegalArgumentException.class);
        // Given a incomplete flow
        mainFlow.setRaw("");
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        try {
            // When we look for this incomplete flow
            Flow flow = flowService.load(Constants.CALLFLOW_MAIN);
        } finally {
            // Then
            verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN);
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
        verify(callFlowDataService, times(1)).findByName("MainFlow");
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
            verify(callFlowDataService, times(1)).findByName("MainFlow2");
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
            verify(callFlowDataService, times(1)).findByName("MainFlow");
            verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "non-existent");
        }

    }

    @Test
    public void shouldReturnFirstNodeOfFlowIfNodeWasNullDuringParse() {
        // When
        FlowStep flowStep = flowService.parse("|MainFlow.|", null);

        // Then
        verify(flowUtil, times(1)).parse("|MainFlow.|", null);
        verify(callFlowDataService, times(1)).findByName("MainFlow");
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
        verify(callFlowDataService, times(1)).findByName("MainFlow");
        verify(flowUtil, times(1)).getNodeByStep(expectedFlow, "entry");
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }

}


