package com.janssen.connectforlife.callflows.service.it;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.FlowStep;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.FlowService;
import com.janssen.connectforlife.callflows.util.TestUtil;

import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

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

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Flow Service Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class FlowServiceBundleIT extends BasePaxIT {

    @Inject
    private FlowService flowService;

    @Inject
    private CallFlowDataService callFlowDataService;

    private CallFlow mainFlow;

    private Flow expectedFlow;

    @Before
    public void setUp() throws IOException {
        mainFlow = CallFlowHelper.createMainFlow();
        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        callFlowDataService.create(mainFlow);

        expectedFlow = FlowHelper.createFlow(raw);
    }

    @After
    public void tearDown() {
        callFlowDataService.deleteAll();
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
        callFlowDataService.update(mainFlow);

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
        FlowStep flowStep = flowService.parse("|entry|", expectedFlow);

        // Then
        assertNotNull(flowStep);
        assertThat(flowStep.getFlow().getName(), equalTo("MainFlow"));
        assertThat(flowStep.getStep(), equalTo("entry"));
    }
}


