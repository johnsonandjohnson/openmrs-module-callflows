package com.janssen.connectforlife.callflows.service.it;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.CallService;
import com.janssen.connectforlife.callflows.util.CallAssert;

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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Service Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallServiceBundleIT extends BasePaxIT {

    @Inject
    private CallService callService;

    @Inject
    private CallDataService callDataService;

    @Inject
    private CallFlowDataService callFlowDataService;

    private CallFlow mainFlow;

    private Call inboundCall;

    private Call outboundCall;

    private Map<String, Object> params;

    private Map<String, String> providerData;

    @Before
    public void setUp() {
        // create a call flow
        mainFlow = callFlowDataService.create(CallFlowHelper.createMainFlow());

        // link the call flow to a inbound
        inboundCall = CallHelper.createInboundCall();
        inboundCall.setStartFlow(mainFlow);
        inboundCall.setEndFlow(mainFlow);

        // and a outbound call
        outboundCall = CallHelper.createOutboundCall();
        outboundCall.setStartFlow(mainFlow);
        outboundCall.setEndFlow(mainFlow);

        // create two calls
        inboundCall = callDataService.create(inboundCall);
        outboundCall = callDataService.create(outboundCall);

        params = CallHelper.createParams();
        providerData = new HashMap<>();
    }

    @After
    public void tearDown() {
        callDataService.deleteAll();
        callFlowDataService.deleteAll();
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(callService);
    }

    @Test
    public void shouldCreateInboundCallWithActor() {

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.INCOMING,
                                          Constants.ACTOR_ID,
                                          Constants.ACTOR_TYPE,
                                          params);

        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithActor() {

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.OUTGOING,
                                          Constants.ACTOR_ID,
                                          Constants.ACTOR_TYPE,
                                          params);

        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldCreateInboundCallWithoutActor() {

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.INCOMING,
                                          params);

        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertNullActor(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithoutActor() {

        // When
        Call newCall = callService.create(Constants.CONFIG_VOXEO,
                                          mainFlow,
                                          Constants.CALLFLOW_MAIN_ENTRY,
                                          CallDirection.OUTGOING,
                                          params);
        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertNullActor(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldFindCallByValidCallId() {

        // When we search for a inbound call that we created earlier
        Call call = callService.findByCallId(inboundCall.getCallId());

        // Then
        assertNotNull(call);
        assertThat(call.getCallId(), equalTo(inboundCall.getCallId()));
    }
}

