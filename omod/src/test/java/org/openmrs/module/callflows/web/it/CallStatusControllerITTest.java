/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.web.it;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.helper.CallHelper;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Call Status Controller Integration Tests
 */
@WebAppConfiguration
public class CallStatusControllerITTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private CallDao callDao;

    @Autowired
    private CallFlowDao callFlowDao;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        callDao.deleteAll();
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldReturnStatusOkIfTheCallStatusUpdateIsSuccessful() throws Exception {
        //Given
        Call call = CallHelper.createOutboundCall();
        callDao.saveCall(call);

        mockMvc.perform(get("/callflows/status/" + call.getId().toString())
                .param("status", "ANSWERED")
                .param("reason", "call answered"))
                .andExpect(status().is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusOkIfTheCallIDIsNotFoundInDatabase() throws Exception {
        mockMvc.perform(get("/callflows/status/22")
                .param("status", "ANSWERED")
                .param("reason", "call answered"))
                .andExpect(status().is(HttpStatus.SC_OK));
    }

}
