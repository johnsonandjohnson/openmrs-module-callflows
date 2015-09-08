package com.janssen.connectforlife.callflows.service.it;

import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallFlowStatus;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.CallFlowService;

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

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Flow Service Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallFlowServiceBundleIT extends BasePaxIT {

    private CallFlow mainFlow;

    private CallFlow badFlow;

    @Inject
    private CallFlowService callFlowService;

    @Inject
    private CallFlowDataService callFlowDataService;

    @Before
    public void setUp() {
        mainFlow = CallFlowHelper.createMainFlow();
        badFlow = CallFlowHelper.createBadFlow();
    }

    @After
    public void tearDown() {
        callFlowDataService.deleteAll();
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(callFlowService);
    }

    @Test
    public void shouldReturnNewlyCreatedCallFlow() throws CallFlowAlreadyExistsException {
        // When
        CallFlow callFlow = callFlowService.create(mainFlow);

        // Then
        assertNotNull(callFlow);
        assertNotNull(callFlow.getId());
        assertThat(callFlow.getName(), equalTo(mainFlow.getName()));
        assertThat(callFlow.getDescription(), equalTo(mainFlow.getDescription()));
        assertThat(callFlow.getStatus(), equalTo(CallFlowStatus.DRAFT));
        assertThat(callFlow.getRaw(), equalTo(mainFlow.getRaw()));
    }

    @Test(expected = CallFlowAlreadyExistsException.class)
    public void shouldThrowCallFlowAlreadyExistsIfDuplicateCallFlowIsCreated() throws CallFlowAlreadyExistsException {
        // Given
        CallFlow callFlow = callFlowService.create(mainFlow);
        // When a flow with the same name is created again
        callFlowService.create(mainFlow);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfCallFlowWithNonAlphanumericCharactersIsCreated() throws CallFlowAlreadyExistsException {
        // Given, When And Then
        CallFlow callFlow = callFlowService.create(badFlow);
    }
}
