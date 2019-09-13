package org.openmrs.module.callflows.api.service.it;

import org.hibernate.HibernateException;
import org.openmrs.api.context.Context;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.helper.CallHelper;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.util.CallAssert;
import org.openmrs.module.callflows.api.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Service Integration Tests
 *
 * @author bramak09
 */
public class CallServiceBundleIT extends BaseModuleContextSensitiveTest {

    @Autowired
    private CallService callService;

    @Autowired
    private CallDao callDao;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CallFlowDao callFlowDao;

    private CallFlow mainFlow;

    private Call inboundCall;

    private Call outboundCall;

    private Map<String, Object> params;

    private Map<String, String> providerData;

    private List<Config> configs;

    private Config voxeo;

    @Before
    public void setUp() throws IOException {
        // create a call flow
        mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setRaw(TestUtil.loadFile("main_flow.json"));
        mainFlow = callFlowDao.create(mainFlow);

        // link the call flow to a inbound
        inboundCall = CallHelper.createInboundCall();
        inboundCall.setStartFlow(mainFlow);
        inboundCall.setEndFlow(mainFlow);

        // and a outbound call
        outboundCall = CallHelper.createOutboundCall();
        outboundCall.setStartFlow(mainFlow);
        outboundCall.setEndFlow(mainFlow);

        // create two calls
        inboundCall = callDao.create(inboundCall);
        outboundCall = callDao.create(outboundCall);

        params = CallHelper.createParams();
        providerData = new HashMap<>();

        // and config
        configs = ConfigHelper.createConfigs();
        voxeo = configs.get(0);
        voxeo.setOutgoingCallUriTemplate("http://www.google.com");
        configService.updateConfigs(configs);
    }

    @After
    public void tearDown() {
        Context.clearSession();
        callDao.deleteAll();
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(callService);
    }

    @Test
    public void shouldCreateInboundCallWithActor() {

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.INCOMING,
                        Constants.ACTOR_ID, Constants.ACTOR_TYPE, Constants.EXTERNAL_ID, Constants.EXTERNAL_TYPE,
                        Constants.PLAYED_MESSAGES, null, params);

        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertIncomingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertExternal(newCall);
        CallAssert.assertPlayedMessages(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldCreateOutboundCallWithActor() {

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.OUTGOING,
                        Constants.ACTOR_ID, Constants.ACTOR_TYPE, null, null, null, Constants.REF_KEY, params);

        // Then
        CallAssert.assertBasicFields(newCall);
        CallAssert.assertOutgoingCall(newCall);
        CallAssert.assertActor(newCall);
        CallAssert.assertRefKey(newCall);
        CallAssert.assertTimestamps(newCall);
    }

    @Test
    public void shouldCreateInboundCallWithoutActor() {

        // When
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.INCOMING,
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
        Call newCall = callService
                .create(Constants.CONFIG_VOXEO, mainFlow, Constants.CALLFLOW_MAIN_ENTRY, CallDirection.OUTGOING,
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

    @Test
    public void shouldUpdateOnlyAllowedFieldsInCall() {
        // Given a existing outbound call without actors
        outboundCall.setActorId(null);
        outboundCall.setActorType(null);
        callDao.update(outboundCall);

        // And we updated *all* the properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        // When
        Call returnedCall = callService.update(updatedCall);

        // Then
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall,
                                                       outboundCall.getCallId());
        CallAssert.assertChangeToChangeableFields(returnedCall);
        CallAssert.assertActorUpdated(returnedCall);
        CallAssert.assertExternalUpdated(returnedCall);
        CallAssert.assertPlayedMessagesUpdated(returnedCall);
    }

    @Test
    public void shouldNotUpdateActorIfCallWasCreatedWithActorSet() {
        // Given a existing outbound call with actors
        outboundCall.setActorId(Constants.ACTOR_ID);
        outboundCall.setActorType(Constants.ACTOR_TYPE);
        callDao.update(outboundCall);

        // And we update all of it's properties
        Call updatedCall = CallHelper.updateAllPropertiesInOutboundCall(outboundCall);

        Context.flushSession();
        Context.clearSession();
        // When
        Call returnedCall = callService.update(updatedCall);

        // Then
        assertNotNull(returnedCall);
        CallAssert.assertNoChangeToNonChangeableFields(returnedCall,
                                                       outboundCall.getCallId());
        CallAssert.assertChangeToChangeableFields(returnedCall);
        CallAssert.assertActor(returnedCall);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfInvalidCallWasProvided() {

        // Given a invalid call ID
        outboundCall.setId(-1);

        // When
        Call updatedCall = callService.update(outboundCall);

        // Then we expect an expection
    }

    @Test
    public void shouldMakeCall() {
        // Given
        params.put("phone", "1234567890");
        // Just need a url that returns a 200 response, for lack of imagination we point to google :)
        voxeo.setOutgoingCallUriTemplate("http://www.google.com");
        configService.updateConfigs(configs);
        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNotNull(call);
        assertThat(call.getStatus(), equalTo(CallStatus.MOTECH_INITIATED));
    }

    @Test
    public void shouldNotMakeCallForNullPhone() {
        // Given
        params.put("phone", null);
        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);
    }

    @Test
    public void shouldNotMakeCallForEmptyPhone() {
        // Given
        params.put("phone", "");
        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);

    }

    @Test
    public void shouldNotMakeCallIfBadConfigWasProvidedInMakeCall() {
        // Given
        params.put("phone", "1234567890");
        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO + "Bad", Constants.CALLFLOW_MAIN, params);

        // Then
        assertNull(call);
    }

    @Test
    public void shouldNotMakeCallIfBadCallFlowWasProvidedInMakeCall() {
        // Given
        params.put("phone", "1234567890");
        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN + "Bad", params);

        // Then
        assertNull(call);
    }

    @Test
    public void shouldMakeCallFailedIfHttpStatusFromProviderIsNotAcceptable() {
        // Given
        params.put("phone", "1234567890");
        voxeo.setOutgoingCallUriTemplate("http://localhost/should-return-404");
        configService.updateConfigs(configs);

        // When
        Call call = callService.makeCall(Constants.CONFIG_VOXEO, Constants.CALLFLOW_MAIN, params);

        // Then
        assertNotNull(call);
        assertThat(call.getStatus(), equalTo(CallStatus.FAILED));
    }

}

