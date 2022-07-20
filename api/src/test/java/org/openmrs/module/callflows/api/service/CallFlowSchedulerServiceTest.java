package org.openmrs.module.callflows.api.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.impl.CallFlowSchedulerServiceImpl;
import org.openmrs.module.callflows.api.task.CallFlowScheduledTask;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseContextMockTest;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CallFlowSchedulerServiceTest extends BaseContextMockTest {
  // 2022-07-20T08:36:00Z
  private static final Date JOB_START_DATETIME = new Date(1658306160000L);

  @Mock
  private SchedulerService schedulerService;

  @Test
  public void scheduleRunOnceJob_shouldScheduleJob() throws SchedulerException {
    final Map<String, Object> eventParameters = new HashMap<>();
    eventParameters.put(Constants.PARAM_FLOW_NAME, "TestFlow");
    eventParameters.put(Constants.PARAM_CONFIG, "text-mobile");
    eventParameters.put(Constants.PARAM_PARAMS, Collections.singletonMap("testParam", "testParamValue"));
    eventParameters.put(Constants.PARAM_HEADERS, new HashMap<>());
    eventParameters.put(Constants.PARAM_JOB_ID, "5175a336-b6ea-4fcc-a2d7-fc41d2724e0e");
    final CallFlowEvent event = new CallFlowEvent("Test", eventParameters);

    final CallFlowSchedulerServiceImpl service = new CallFlowSchedulerServiceImpl();
    service.scheduleRunOnceJob(event, JOB_START_DATETIME, new CallFlowScheduledTask());

    final ArgumentCaptor<TaskDefinition> taskDefinitionCaptor = ArgumentCaptor.forClass(TaskDefinition.class);
    Mockito.verify(schedulerService, Mockito.times(1)).scheduleTask(taskDefinitionCaptor.capture());
    final TaskDefinition capturedTaskDefinition = taskDefinitionCaptor.getValue();

    Assert.assertEquals(event.generateTaskName(), capturedTaskDefinition.getName());
    Assert.assertEquals(CallFlowScheduledTask.class.getName(), capturedTaskDefinition.getTaskClass());
    Assert.assertEquals(JOB_START_DATETIME, capturedTaskDefinition.getStartTime());
    // TODO: verify if this is correct requirement
    Assert.assertEquals(false, capturedTaskDefinition.getStartOnStartup());
  }
}
