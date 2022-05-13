/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.flow.FieldElement;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.evaluation.EvaluationCommand;
import org.openmrs.module.callflows.api.evaluation.impl.BaseEvaluationCommand;
import org.openmrs.module.callflows.api.helper.FlowHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Flow Util Test Cases
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowUtilTest extends BaseTest {

    @Autowired
    private FlowUtil flowUtil = new FlowUtil();

    private EvaluationCommand evaluationCommand = new BaseEvaluationCommand();

    private Flow flow;

    private VelocityContext context;

    @Before
    public void setUp() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String flowString = TestUtil.loadFile("main_flow.json");
        flow = FlowHelper.createFlow(flowString);

        context = new VelocityContext();
        Context.logout();
        Module module = new Module("callflows");
        module.setModuleId("callflows");
        Method m = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        m.setAccessible(true); //if security settings allow this
        Object o = m.invoke(null, module); //use null if the method is static
        evaluationCommand.setDaemonToken((DaemonToken) o);
        flowUtil.setEvaluationCommand(evaluationCommand);
    }

    // parse
    // =====

    @Test
    public void shouldParseValidInputWithFlowAndStepName() {
        // Given
        String input = "|MainFlow.entry|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseValidInputWithOnlyFlowNameAndADot() {
        // Given
        String input = "|MainFlow.|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", null);
    }

    @Test
    public void shouldParseValidInputWithOnlyStepName() {
        // Given
        String input = "|entry|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, null, "entry");
    }

    @Test
    public void shouldParseValidInputWithOnlyStepNameAndADot() {
        // Given
        String input = "|.entry|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, null, "entry");
    }

    @Test
    public void shouldThrowIllegalArgumentForEmptyInputDuringParse() {
        expectException(IllegalArgumentException.class);
        // Given
        String input = "";

        // When
        flowUtil.parse(input, null);
    }

    @Test
    public void shouldThrowIllegalArgumentForNullInputDuringParse() {
        expectException(IllegalArgumentException.class);
        // Given
        String input = null;

        // When
        flowUtil.parse(input, null);
    }

    @Test
    public void shouldThrowIllegalArgumentForInputWithoutPipesDuringParse() {
        expectException(IllegalArgumentException.class);
        // Given
        String input = "MainFlow.";

        // When
        flowUtil.parse(input, null);
    }

    @Test
    public void shouldThrowIllegalArgumentForBadlyFormattednputDuringParse() {
        expectException(IllegalArgumentException.class);
        // Given
        String input = "|MainFlow.";

        // When
        flowUtil.parse(input, null);
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesInFrontOfPipe() {
        // Given
        String input = "     |MainFlow.entry|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesAtEndOfPipe() {
        // Given
        String input = "|MainFlow.entry|  ";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesInsidePipe() {
        // Given
        String input = "|    MainFlow.entry    |";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesDemarcatingFlowAndStep() {
        // Given
        String input = "|MainFlow    .     entry|";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseSuccessfullyWithSpaces() {
        // Given
        String input = "  |   MainFlow    .     entry  |    ";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "MainFlow", "entry");
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesInStepName() {
        // Given
        String input = "  |   entry         handler  |    ";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, null, "entry         handler");
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesInFlowName() {
        // Given
        String input = "  |   Main   Flow   .  |    ";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "Main   Flow", null);
    }

    @Test
    public void shouldParseSuccessfullyWithSpacesInFlowAndStepName() {
        // Given
        String input = "  |   Main   Flow   .  entry  handler  |    ";

        // When
        String[] parsed = flowUtil.parse(input, null);

        // Then
        assertFlowStep(parsed, "Main   Flow", "entry  handler");
    }

    // getNodeByStep
    // =============

    @Test
    public void shouldGetNodeByValidStep() {
        // Given a flow with nodes entry, entry-handler, active, active-handler, inactive

        // When
        Node node = flowUtil.getNodeByStep(flow, "active");

        // Then
        assertNotNull(node);
        assertThat(node.getStep(), equalTo("active"));
    }

    @Test
    public void shouldReturnNullIfAttemptedToGetNodeByInvalidStep() {
        // Given a flow with nodes entry, entry-handler, active, active-handler, inactive

        // When
        Node node = flowUtil.getNodeByStep(flow, "invalid");

        // Then
        assertNull(node);
    }

    // getNextNodeByStep
    // =================

    @Test
    public void shouldGetNextNodeByValidStep() {
        // Given a flow with nodes entry, entry-handler, active, active-handler, inactive

        // When
        Node node = flowUtil.getNextNodeByStep(flow, "active");

        // Then
        assertNotNull(node);
        assertThat(node.getStep(), equalTo("active-handler"));

    }

    @Test
    public void shouldReturnNullIfAttemptedToGetNextNodeByInvalidStep() {
        // Given a flow with nodes entry, entry-handler, active, active-handler, inactive

        // When
        Node node = flowUtil.getNextNodeByStep(flow, "invalid");

        // Then
        assertNull(node);
    }

    // evalNode
    // ========
    @Test
    public void shouldEvalNode() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_VXML);

        // When
        String output = flowUtil.evalNode(flow, flow.getNodes().get(0), context, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo(TestUtil.loadFile("main_flow_entry.vxml")));
    }

    @Test
    public void shouldEvalNodeWithAllAttributes() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_JSON);
        // And we take the first user node
        UserNode entry = (UserNode) flow.getNodes().get(0);
        // And it's first field element (2nd element by index)
        FieldElement field = (FieldElement) entry.getBlocks().get(0).getElements().get(1);
        // And set all field's properties
        field.setNoInput("No Input was provided");
        field.setNoMatch("Nothing matched. Try again!");
        field.setGoodBye("Goodbye!");

        // When
        String output = flowUtil.evalNode(flow, flow.getNodes().get(0), context, Constants.CONFIG_RENDERER_JSON);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo(TestUtil.loadFile("main_flow_entry_all_attrs.json")));
    }

    @Test
    public void shouldEvalNodeAsJSON() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_JSON);

        // When
        String output = flowUtil.evalNode(flow, flow.getNodes().get(0), context, Constants.CONFIG_RENDERER_JSON);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo(TestUtil.loadFile("main_flow_entry.json")));
    }

    @Test
    public void shouldEvalSystemNodeAsJSON() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_JSON);

        // When
        String output = flowUtil.evalNode(flow, flow.getNodes().get(1), context, Constants.CONFIG_RENDERER_JSON);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo("[]"));
    }

    @Test
    public void shouldEvalNullTemplateInNodeAndReturnEmptyString() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_VXML);
        Node entry = flow.getNodes().get(0);
        entry.getTemplates().get(Constants.CONFIG_RENDERER_VXML).setContent(null);
        // When
        String output = flowUtil.evalNode(flow, entry, context, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo(StringUtils.EMPTY));
    }

    @Test
    public void shouldEvalEmptyTemplateInNodeAndReturnEmptyString() throws IOException {
        // Given
        prepareInternalContext(context, Constants.PARAM_NEXT_URL, Constants.NEXT_URL_VXML);

        Node entry = flow.getNodes().get(0);
        entry.getTemplates().get(Constants.CONFIG_RENDERER_VXML).setContent(StringUtils.EMPTY);
        // When
        String output = flowUtil.evalNode(flow, entry, context, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(output);
        assertThat(output, equalTo(StringUtils.EMPTY));
    }

    private void prepareInternalContext(VelocityContext ctx, String property, String val) {
        Map<String, String> internalCtx = new HashMap<>();
        internalCtx.put(property, val);
        ctx.put(Constants.PARAM_INTERNAL, internalCtx);
    }

    private void assertFlowStep(String[] flowAndStep, String expectedFlow, String expectedStep) {
        assertNotNull(flowAndStep);
        assertThat(flowAndStep.length, equalTo(2));
        assertThat(flowAndStep[0], equalTo(expectedFlow));
        assertThat(flowAndStep[1], equalTo(expectedStep));
    }

}


