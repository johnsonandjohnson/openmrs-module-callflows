/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.web.controller;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Velocity.class})
public class CallControllerUnitTest extends BaseTest {

  @Mock
  private CallService callService;

  @Mock
  private ConfigService configService;

  @Mock
  private CallFlowService callFlowService;

  @Mock
  private FlowService flowService;

  @Spy
  @InjectMocks
  private CallUtil callUtil = new CallUtil();

  @InjectMocks
  private CallController testCallController = new CallController();

  @Before
  public void setupMocks() {
    final Flow dummyFlow = new Flow();
    dummyFlow.setNodes(new ArrayList<>());
    dummyFlow.getNodes().add(new Node());

    Mockito.when(configService.getConfig(Mockito.anyString())).thenReturn(new Config());
    Mockito.when(callFlowService.findByName(Mockito.anyString())).thenReturn(new CallFlow());
    Mockito.when(flowService.load(Mockito.anyString())).thenReturn(dummyFlow);
  }

  @Test
  public void initialize_shouldThrowIllegalStateExceptionWhenVelocityInitFails() throws Exception {
    final Properties props = new Properties();
    props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
    props.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, "root");
    final String testMessage = "Exception from Velocity";

    PowerMockito.mockStatic(Velocity.class);
    // any(Properties.class) seems to be not supported here
    PowerMockito.when(Velocity.class, "init", props).thenThrow(new Exception(testMessage));

    try {
      testCallController.initialize();
      Assert.fail("Should throw exception");
    } catch (Exception expected) {
      Assert.assertEquals(IllegalStateException.class, expected.getClass());
      Assert.assertTrue(expected.getMessage().contains(testMessage));
    }
  }

  @Test
  public void handleIncoming_shouldHandleResponseForNoCallFoundForId() {
    final String testCallId = "123A";
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final Map<String, String> params = new HashMap<>();
    params.put("callId", testCallId);

    ResponseEntity<String> response =
        testCallController.handleIncoming(request, "conf", "flowName", "extension", params, new HashMap<>());

    Assert.assertNotNull(response);
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assert.assertTrue(response.getBody().contains(testCallId));
  }
}
