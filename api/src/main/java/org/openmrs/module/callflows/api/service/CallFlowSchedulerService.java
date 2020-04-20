package org.openmrs.module.callflows.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;

public interface CallFlowSchedulerService extends OpenmrsService {

    void scheduleRunOnceJob(CallFlowEvent event, Date startTime, AbstractTask task);
}
