package org.openmrs.module.callflows.api.event;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CallFlowEventTest {

    private static final String TEST_SUBJECT = "test_subject";

    private static final String FLOW_NAME = "flowName";

    private static final String PHONE = "543-654-531";

    private static final String CONFIG = "config";

    private static final String ACTOR_ID = "actor_id";

    private static final String ACTOR_TYPE = "actor_type";

    private static final String EXTERNAL_ID = "external_id";

    private static final String EXTERNAL_TYPE = "external_type";

    private static final String PLAYED_MESSAGES = "played messages";

    private static final String REF_KEY = "refKey";

    private static final int RETRY_ATTEMPTS = 2;

    private static final String PARAMS_AS_STRING = "externalType=external_type,actorType=actor_type,actorId=actor_id,"
            + "playedMessages=played messages,phone=543-654-531,externalId=external_id,refKey=refKey,retryAttempts=2";

    private Map<String, Object> propertiesObject;
    private Map<String, String> propertiesString;

    @Before
    public void setUp() {
        propertiesObject = new HashMap<String, Object>() {{
            put(Constants.PARAM_CONFIG, CONFIG);
            put(Constants.PARAM_FLOW_NAME, FLOW_NAME);
            put(Constants.PARAM_PARAMS, new HashMap<String, Object>() {{
                put(Constants.PARAM_PHONE, PHONE);
                put(Constants.PARAM_ACTOR_ID, ACTOR_ID);
                put(Constants.PARAM_ACTOR_TYPE, ACTOR_TYPE);
                put(Constants.PARAM_EXTERNAL_ID, EXTERNAL_ID);
                put(Constants.PARAM_EXTERNAL_TYPE, EXTERNAL_TYPE);
                put(Constants.PARAM_PLAYED_MESSAGES, PLAYED_MESSAGES);
                put(Constants.PARAM_REF_KEY, REF_KEY);
                put(Constants.PARAM_RETRY_ATTEMPTS, RETRY_ATTEMPTS);
            }});
        }};

        propertiesString = new HashMap<String, String>() {{
            put(Constants.PARAM_CONFIG, CONFIG);
            put(Constants.PARAM_FLOW_NAME, FLOW_NAME);
            put(Constants.PARAM_PARAMS, PARAMS_AS_STRING);
        }};
    }

    @Test
    public void convertPropertiesToMapStringString() {
        CallFlowEvent event = new CallFlowEvent(TEST_SUBJECT, propertiesObject);
        Map<String, String> actual = event.convertProperties();

        assertThat(actual.size(), equalTo(actual.size()));
        assertThat(actual.get(Constants.PARAM_PARAMS), equalTo(PARAMS_AS_STRING));
        assertThat(actual.get(Constants.PARAM_CONFIG), equalTo(CONFIG));
        assertThat(actual.get(Constants.PARAM_FLOW_NAME), equalTo(FLOW_NAME));
    }

    @Test
    public void convertPropertiesToMapStringObject() {
        Map<String, Object> actual = CallFlowEvent.convertProperties(propertiesString);

        assertThat(actual.size(), equalTo(actual.size()));
        assertThat(actual.get(Constants.PARAM_CONFIG), equalTo(CONFIG));
        assertThat(actual.get(Constants.PARAM_FLOW_NAME), equalTo(FLOW_NAME));
        Map<String, Object> params = (Map<String, Object>) actual.get(Constants.PARAM_PARAMS);
        assertThat(params.get(Constants.PARAM_PHONE), equalTo(PHONE));
        assertThat(params.get(Constants.PARAM_ACTOR_ID), equalTo(ACTOR_ID));
        assertThat(params.get(Constants.PARAM_ACTOR_TYPE), equalTo(ACTOR_TYPE));
        assertThat(params.get(Constants.PARAM_EXTERNAL_ID), equalTo(EXTERNAL_ID));
        assertThat(params.get(Constants.PARAM_EXTERNAL_TYPE), equalTo(EXTERNAL_TYPE));
        assertThat(params.get(Constants.PARAM_PLAYED_MESSAGES), equalTo(PLAYED_MESSAGES));
        assertThat(params.get(Constants.PARAM_REF_KEY), equalTo(REF_KEY));
        assertThat(params.get(Constants.PARAM_RETRY_ATTEMPTS), equalTo(RETRY_ATTEMPTS));
    }
}
