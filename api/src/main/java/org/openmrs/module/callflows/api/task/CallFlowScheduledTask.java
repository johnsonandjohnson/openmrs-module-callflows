package org.openmrs.module.callflows.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Map;

public class CallFlowScheduledTask extends AbstractTask {

	@Override
	public void execute() {
		Map<String, Object> properties = CallFlowEvent.convertProperties(getTaskDefinition().getProperties());
		String config = properties.get(Constants.PARAM_CONFIG).toString();
		String flowName = properties.get(Constants.PARAM_FLOW_NAME).toString();
		Map<String, Object> params = (Map<String, Object>) properties.get(Constants.PARAM_PARAMS);
		Context.getRegisteredComponent("callService", CallService.class).makeCall(config, flowName, params);
	}
}
