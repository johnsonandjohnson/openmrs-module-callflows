package org.openmrs.module.callflows.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.event.CallFlowEvent;

public interface CallFlowEventService extends OpenmrsService {

	void sendEventMessage(CallFlowEvent event);
}
