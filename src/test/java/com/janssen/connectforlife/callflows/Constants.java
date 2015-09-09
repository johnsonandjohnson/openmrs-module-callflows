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

    public static final String CALLFLOW_MAIN_DESCRIPTION = "This is the Main Flow";

    public static final String CALLFLOW_MAIN_RAW = "{}";

    public static final String CALLFLOW_BAD = "BadFlow.";

    public static final String CALLFLOW_BAD_DESCRIPTION = "This is a badly formatted callflow";

    public static final String CALLFLOW_BAD_RAW = "{}";

    public static final String UPDATED = "-updated";

}
