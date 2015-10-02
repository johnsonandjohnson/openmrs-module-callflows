package com.janssen.connectforlife.callflows;

import com.janssen.connectforlife.callflows.domain.types.CallStatus;

import org.springframework.http.MediaType;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Test Constants
 *
 * @author bramak09
 */
public final class Constants {

    // Utility class, hence private constructor
    private Constants() {

    }

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application",
                                                                        "json",
                                                                        Charset.forName("UTF-8"));

    public static final MediaType APPLICATION_VXML = new MediaType("application",
                                                                   "voicexml+xml",
                                                                   Charset.forName("UTF-8"));

    public static final MediaType PLAIN_TEXT = new MediaType("text", "plain", Charset.forName("UTF-8"));

    public static final String CALLFLOW_MAIN = "MainFlow";

    public static final String CALLFLOW_MAIN_ENTRY = "entry";

    public static final String CALLFLOW_MAIN2 = "MainFlow2";

    public static final String CALLFLOW_MAIN_DESCRIPTION = "This is the Main Flow";

    public static final String CALLFLOW_MAIN_RAW = "{}";

    public static final String CALLFLOW_BAD = "BadFlow.";

    public static final String CALLFLOW_BAD_DESCRIPTION = "This is a badly formatted callflow";

    public static final String CALLFLOW_BAD_RAW = "{}";

    public static final String CALLFLOW_MAIN_PREFIX = "Ma";

    public static final String CALLFLOW_INVALID_PREFIX = "Xu";

    public static final Collection<CallStatus> ACTIVE_OUTBOUND_CALL_STATUSES = Arrays
            .asList(CallStatus.INITIATED, CallStatus.IN_PROGRESS, CallStatus.MOTECH_INITIATED);
    public static final String UPDATED = "-updated";

    public static final String VELOCITY = "velocity";

    public static final String CONFIG_VOXEO = "voxeo";

    public static final String CONFIG_YO = "yo";

    public static final String INVALID = "invalid";

    public static final String CONFIG_VOXEO_OUT_TEMPLATE = "http://some-api-server/?flow=[flow]&callid=[callid]";

    public static final String CONFIG_YO_OUT_TEMPLATE = "http://some-other-api-server/?flow=[flow]&callid=[callid]";

    public static final String CONFIG_VOXEO_METHOD = "GET";

    public static final String CONFIG_YO_METHOD = "POST";

    public static final String CONFIG_VOXEO_USER = "1111111111";

    public static final String CONFIG_YO_USER = "2222222222";

    public static final String CONFIG_VOXEO_USER_URL = "http://some-api-server/?phone=1111111111";

    public static final String CONFIG_YO_USER_URL = "http://some-other-api-server/?phone=2222222222";

    public static final int CONFIG_VOXEO_OUTBOUND_CALL_LIMIT = 5;

    public static final int CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS = 5;

    public static final int CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS = 60;

    public static final boolean CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL = false;

    public static final int CONFIG_YO_OUTBOUND_CALL_LIMIT = 7;

    public static final int CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS = 7;

    public static final int CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS = 30;

    public static final boolean CONFIG_YO_CAN_PLACE_OUTBOUND_CALL = false;

    public static final String CONFIG_SRVC_CALL = "callSrvc";

    public static final String CONFIG_SRVC_CALL_CLASS = "com.janssen.connectforlife.callflows.service.CallService";

    public static final String CONFIG_RENDERER_VXML = "vxml";

    public static final String CONFIG_RENDERER_VXML_MIME = "application/voicexml+xml";

    public static final String CONFIG_RENDERER_VXML_TPL = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><vxml version=\"2.1\"><form><block><prompt>Hello World</prompt></block></form></vxml>";

    public static final String CONFIG_RENDERER_TXT = "txt";

    public static final String CONFIG_RENDERER_TXT_MIME = "text/plain";

    public static final String CONFIG_RENDERER_TXT_TPL = "Text Renderer. Doesn't do much, unless am changed.";

    public static final String CONFIG_RENDERER_JSON = "json";

    public static final String ACTOR_ID = "1223";

    public static final String ACTOR_TYPE = "Patient";

    public static final String KEY_GREETING = "greeting";

    public static final String KEY_PIN = "pid";

    public static final String VALUE_GREETING = "Hello Martian!";

    public static final Integer VALUE_PIN = 1234;

    public static final String DATE_CURRENT = "09/16/2015";

    public static final String DATE_NEXT_DAY = "09/17/2015";

    public static final String DATE_FORMAT = "MM/dd/yyyy";

    public static final UUID INBOUND_CALL_ID = UUID.fromString("6acafc03-3818-4e36-b40a-8881fe5ac71b");

    public static final UUID OUTBOUND_CALL_ID = UUID.fromString("5c8f6f83-567c-4586-a3b6-368397d5aba8");

    public static final String PARAM_NEXT_URL = "nextURL";

    public static final String PARAM_INTERNAL = "internal";

    public static final String PARAM_FLOW_NAME = "flowName";

    public static final String PARAM_CONFIG = "config";

    public static final String PARAM_JOB_ID = "JobID";

    public static final String NEXT_URL_JSON = "http://localhost/motech-platform-server/modules/callflows/calls/6acafc03-3818-4e36-b40a-8881fe5ac71b.json";

    public static final String NEXT_URL_VXML = "http://localhost/motech-platform-server/modules/callflows/calls/6acafc03-3818-4e36-b40a-8881fe5ac71b.vxml";

    public static final String ERROR_SCRIPT = "error:SCRIPT:error in script";

    public static final String ERROR_SYSTEM = "error:SYSTEM:system error";

    public static final String ERROR_YO = "yo config cannot be loaded";

    public static final String ERROR_MAIN_FLOW2 = "MainFlow2 flow cannot be loaded";

    public static final String ERROR_CALLFLOW = "error:BAD_INPUT:" + ERROR_MAIN_FLOW2;

    public static final String ERROR_CONFIG = "error:BAD_INPUT:" + ERROR_YO;

    public static final String XML_OK_RESPONSE = "<?xml version=\"1.0\"?><response>OK</response>";

    public static final String XML_ERROR_RESPONSE = "<?xml version=\"1.0\"?><response>ERROR</response>";

    public static final String TEST_PARAM = "testParam";

    public static final String TEST_VALUE = "testValue";

    public static final String PARAM_RETRY_ATTEMPTS = "retryAttempts";

}
