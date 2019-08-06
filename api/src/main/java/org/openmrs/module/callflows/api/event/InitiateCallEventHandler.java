package org.openmrs.module.callflows.api.event;

import org.openmrs.module.callflows.api.service.CallService;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Initiate Call Event Handler
 *
 * @author bramak09
 */
@Component
public class InitiateCallEventHandler {

    private static final String PARAM_FLOW_NAME = "flowName";

    private static final String PARAM_CONFIG = "config";

    private static final Logger LOGGER = LoggerFactory.getLogger(InitiateCallEventHandler.class);

    @Autowired
    private CallService callService;

    @MotechListener(subjects = { Events.CALLFLOWS_INITIATE_CALL })
    public void handleOutboundCallEvent(MotechEvent event) {
        LOGGER.info("Handling outbound call event {}: {}", event.getSubject(), event.getParameters().toString());
        Map<String, Object> eventParams = event.getParameters();
        String config = eventParams.get(PARAM_CONFIG).toString();
        String flowName = eventParams.get(PARAM_FLOW_NAME).toString();
        Map<String, Object> params = (Map<String, Object>) eventParams.get("params");
        callService.makeCall(config, flowName, params);
    }
}
