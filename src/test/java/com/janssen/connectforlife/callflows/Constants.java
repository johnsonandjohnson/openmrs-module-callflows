package com.janssen.connectforlife.callflows;

import org.springframework.http.MediaType;
import java.nio.charset.Charset;

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

    public static final String CALLFLOW_MAIN = "MainFlow";

    public static final String CALLFLOW_MAIN2 = "MainFlow2";

    public static final String CALLFLOW_MAIN_DESCRIPTION = "This is the Main Flow";

    public static final String CALLFLOW_MAIN_RAW = "{}";

    public static final String CALLFLOW_BAD = "BadFlow.";

    public static final String CALLFLOW_BAD_DESCRIPTION = "This is a badly formatted callflow";

    public static final String CALLFLOW_BAD_RAW = "{}";

    public static final String CALLFLOW_MAIN_PREFIX = "Ma";

    public static final String CALLFLOW_INVALID_PREFIX = "Xu";

    public static final String UPDATED = "-updated";

    public static final String CONFIG_VOXEO = "voxeo";

    public static final String CONFIG_YO = "yo";

    public static final String CONFIG_INVALID = "invalid";

    public static final String CONFIG_VOXEO_OUT_TEMPLATE = "http://some-api-server/?flow=[flow]&callid=[callid]";

    public static final String CONFIG_YO_OUT_TEMPLATE = "http://some-other-api-server/?flow=[flow]&callid=[callid]";

    public static final String CONFIG_VOXEO_METHOD = "GET";

    public static final String CONFIG_YO_METHOD = "POST";

    public static final String CONFIG_VOXEO_USER = "1111111111";

    public static final String CONFIG_YO_USER = "2222222222";

    public static final String CONFIG_VOXEO_USER_URL = "http://some-api-server/?phone=1111111111";

    public static final String CONFIG_YO_USER_URL = "http://some-other-api-server/?phone=2222222222";

    public static final String CONFIG_SRVC_PATIENT = "patientSrvc";

    public static final String CONFIG_SRVC_PATIENT_CLASS = "com.janssen.connectforlife.patient.service.PatientService";

    public static final String CONFIG_SRVC_HEALTHTIP = "healthTipSrvc";

    public static final String CONFIG_SRVC_HEALTHTIP_CLASS = "com.janssen.connectforlife.patient.service.HealthTipService";

    public static final String CONFIG_RENDERER_VXML = "vxml";

    public static final String CONFIG_RENDERER_VXML_MIME = "application/voicexml+xml";

    public static final String CONFIG_RENDERER_VXML_TPL = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><vxml version=\"2.1\"><form><block><prompt>Hello World</prompt></block></form></vxml>";

    public static final String CONFIG_RENDERER_TXT = "txt";

    public static final String CONFIG_RENDERER_TXT_MIME = "text/plain";

    public static final String CONFIG_RENDERER_TXT_TPL = "Text Renderer. Doesn't do much, unless am changed.";

}
