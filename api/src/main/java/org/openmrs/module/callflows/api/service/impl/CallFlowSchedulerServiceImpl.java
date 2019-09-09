package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.openmrs.module.callflows.api.service.CallFlowSchedulerService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("callflow.schedulerService")
public class CallFlowSchedulerServiceImpl extends BaseOpenmrsService implements CallFlowSchedulerService {

	private static final Log LOGGER = LogFactory.getLog(CallFlowSchedulerServiceImpl.class);

	@Autowired
	private SchedulerService schedulerService;

	@Override
	public void safeScheduleRunOnceJob(CallFlowEvent event, Date startTime, AbstractTask task) {
		String taskName = event.generateTaskName();
		shutdownTask(taskName);

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
			schedulerService.scheduleTask(taskDefinition);
		} catch(SchedulerException ex) {
			throw new CallFlowRuntimeException(ex);
		}
	}

	private void shutdownTask(String taskName) {
		try {
			TaskDefinition taskDefinition = schedulerService.getTaskByName(taskName);
			if (taskDefinition != null) {
				LOGGER.debug(String.format("Task %s was shutdown. Last execution time: %s", taskName,
						taskDefinition.getLastExecutionTime()));
				schedulerService.shutdownTask(taskDefinition);
				schedulerService.deleteTask(taskDefinition.getId());
			}
		} catch(SchedulerException ex) {
			LOGGER.error(ex);
		}
	}
}
