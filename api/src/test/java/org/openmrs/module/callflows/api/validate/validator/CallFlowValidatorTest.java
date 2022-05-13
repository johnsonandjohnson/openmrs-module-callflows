/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.validate.validator;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.SystemNode;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.service.FlowService;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CallFlowValidatorTest extends BaseTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder validationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuilder;

    @Mock
    private CallFlowDao callFlowDao;

    @Mock
    private FlowService flowService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CallFlowValidator validator = new CallFlowValidator();


    @Before
    public void setUp() {
        given(context.buildConstraintViolationWithTemplate(any())).willReturn(validationBuilder);
        given(validationBuilder.addNode(any())).willReturn(nodeBuilder);
    }

    @Test
    public void shouldBeValidWhenNamesAndNodesArePresent() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();

        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        assertTrue(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenNameIsDuplicated() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        CallFlow mainFlow2 = CallFlowHelper.createMainFlow();
        mainFlow2.setName(mainFlow.getName());
        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(mainFlow2);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeValidWhenNameItIsSameInstanceUpdate() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setId(5);
        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(mainFlow);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        assertTrue(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenNameItIsSameInstanceCreation() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setId(null);
        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(mainFlow);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenNameIsNotAlfaNumeric() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setName("*&5@");

        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenNameIsBlank() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        mainFlow.setName("");

        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(objectMapper.readValue(mainFlow.getRaw(), Flow.class));

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenNodesAreNull() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        Flow flow = objectMapper.readValue(mainFlow.getRaw(), Flow.class);
        flow.setNodes(null);

        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(flow);

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeInvalidWhenUserNodeNameIsBlank() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        Flow flow = objectMapper.readValue(mainFlow.getRaw(), Flow.class);
        flow.getNodes().add(new UserNode() {{
            setStep("");
            setNodeType("user");
        }});
        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(flow);

        Assert.assertFalse(validator.isValid(mainFlow, context));
    }

    @Test
    public void shouldBeValidWhenSystemNodeNameIsBlank() throws IOException {
        CallFlow mainFlow = CallFlowHelper.createMainFlow();
        Flow flow = objectMapper.readValue(mainFlow.getRaw(), Flow.class);
        flow.getNodes().add(new SystemNode() {{
            setStep("");
            setNodeType("system");
        }});
        when(callFlowDao.findByName(mainFlow.getName()))
                .thenReturn(null);
        when(flowService.loadByJson(mainFlow.getRaw()))
                .thenReturn(flow);

        assertTrue(validator.isValid(mainFlow, context));
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void shouldBeInitializedWithoutAnyExceptionThrown() {
        validator.initialize(null);
    }
}
