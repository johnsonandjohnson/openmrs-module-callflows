package org.openmrs.module.callflows.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.util.CallFlowEventSubjectConstants;

import java.util.Map;

/**
 * Initiate Call Event Listener
 *
 * @author bramak09
 */
public class CallFlowInitiateCallEventListener extends AbstractCallFlowEventListener {

    private static final Log LOGGER = LogFactory.getLog(CallFlowInitiateCallEventListener.class);

    @Override
    public String getSubject() {
        return CallFlowEventSubjectConstants.CALLFLOWS_INITIATE_CALL;
    }

    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.info(String.format("Handling outbound call event %s: %s", getSubject(), properties.toString()));
        String config = properties.get(Constants.PARAM_CONFIG).toString();
        String flowName = properties.get(Constants.PARAM_FLOW_NAME).toString();
        Map<String, Object> params = (Map<String, Object>) properties.get(Constants.PARAM_PARAMS);
        getComponent("callflows.callService", CallService.class).makeCall(config, flowName, params);
    }
}
