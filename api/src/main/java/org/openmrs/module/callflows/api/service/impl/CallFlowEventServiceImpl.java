package org.openmrs.module.callflows.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.CallFlowEventService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service("callFlow.eventService")
public class CallFlowEventServiceImpl extends BaseOpenmrsService implements CallFlowEventService {

    @Override
    public void sendEventMessage(CallFlowEvent event) {
        Event.fireEvent(event.getSubject(), convertParamsToEventMessage(event.getParameters()));
    }

    private EventMessage convertParamsToEventMessage(Map<String, Object> params) {
        EventMessage eventMessage = new EventMessage();

        for (String key : params.keySet()) {
            eventMessage.put(key, (Serializable) params.get(key));
        }

        return eventMessage;
    }
}
