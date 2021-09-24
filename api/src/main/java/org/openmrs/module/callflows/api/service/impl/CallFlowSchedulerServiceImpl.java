package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.openmrs.module.callflows.api.service.CallFlowSchedulerService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;

public class CallFlowSchedulerServiceImpl extends BaseOpenmrsService implements CallFlowSchedulerService {

    private static final Log LOGGER = LogFactory.getLog(CallFlowSchedulerServiceImpl.class);

    @Override
    public void scheduleRunOnceJob(CallFlowEvent event, Date startTime, AbstractTask task) {
        String taskName = event.generateTaskName();

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setName(taskName);
        taskDefinition.setTaskClass(task.getClass().getName());
        taskDefinition.setStartTime(startTime);
        taskDefinition.setStartOnStartup(true);
        taskDefinition.setProperties(event.convertProperties());
        taskDefinition.setRepeatInterval(0L);

        try {
            LOGGER.debug(String.format("Task %s (%s) scheduled to run once at %s", taskName, task.getClass().getName(),
                    startTime));
            Context.getSchedulerService().scheduleTask(taskDefinition);
        } catch (SchedulerException ex) {
            throw new CallFlowRuntimeException(ex);
        }
    }
}
