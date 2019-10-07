package org.openmrs.module.callflows.api.helper;

import org.openmrs.module.callflows.api.domain.flow.Flow;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;

/**
 * Flow Helper to manage Flow objects
 *
 * @author bramak09
 */
public final class FlowHelper {

    // private constructor
    private FlowHelper() {

    }

    public static Flow createFlow(String raw) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(raw, Flow.class);
    }
}

