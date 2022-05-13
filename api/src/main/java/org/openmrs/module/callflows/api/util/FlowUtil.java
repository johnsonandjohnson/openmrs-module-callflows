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
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.evaluation.EvalNodeDaemon;
import org.openmrs.module.callflows.api.evaluation.EvaluationCommand;
import org.openmrs.module.callflows.api.evaluation.EvaluationContext;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of Flow Utilities for working with flows and nodes
 *
 * @author bramak09
 */
public class FlowUtil {

    private static final String DOT = ".";

    private static final Pattern STEP_REGEX = Pattern.compile("\\|\\s*([^|]+)\\s*\\|");

    private EvaluationCommand evaluationCommand;

    /**
     * Get the next node to a given node. This is typically used to retrieve the handler node (system node) given a user node
     *
     * @param flow to search
     * @param step to find in order to find the next step
     * @return a node if found, returns null if not found
     */
    public Node getNextNodeByStep(Flow flow, String step) {
        List<Node> nodes = flow.getNodes();
        Node out = null;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (step.equals(node.getStep())) {
                out = nodes.get(i + 1);
                break;
            }
        }
        return out;
    }

    /**
     * Get a node by a given step in a given flow
     *
     * @param flow to search
     * @param step to find
     * @return the node found, null if not found
     */
    public Node getNodeByStep(Flow flow, String step) {

        List<Node> nodes = flow.getNodes();
        Node out = null;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (step.equals(node.getStep())) {
                out = node;
                break;
            }
        }
        return out;
    }

    /**
     * Parse a string into a string array of two components. The first component is the flow name and the second is the step name
     * input is expected to be in one of the following formats
     * |MainFlow.| =&gt; MainFlow, null
     * |MainFlow.active| =&gt; MainFlow, active
     * |entry| =&gt; currentFlow, entry - currentFlow is a passed argument
     * |.entry| =&gt; currentFlow, entry - currentFlow is a passed argument
     * <p></p>
     * Note: This method does not validate whether the parsed flow name and step names are valid. That is handled at FlowService
     *
     * @param input       to parse
     * @param currentFlow mandatory in certain cases of the input as indicated above, optional otherwise
     * @return a string array containing the parsed flow name and step name
     * @throws IllegalArgumentException if the input is empty or not in the allowed format as indicated above
     */
    public String[] parse(String input, String currentFlow) {
        // bad input
        if (StringUtils.isEmpty(input)) {
            throw new IllegalArgumentException("input to parse cannot be empty!");
        }

        Matcher matcher = STEP_REGEX.matcher(input);
        // bad format
        if (!matcher.find()) {
            throw new IllegalArgumentException("input to parse is in bad format! " + input);
        }

        // remove the pipe delimiters
        String inputWithoutPipes = matcher.group(1);

        inputWithoutPipes = inputWithoutPipes.trim();
        // the output components
        String[] parsed = {null, null};

        if (inputWithoutPipes.endsWith(DOT)) {
            // only flow information is present, "MainFlow."
            inputWithoutPipes = inputWithoutPipes.replace(DOT, StringUtils.EMPTY);
            parsed[0] = inputWithoutPipes.trim();
        } else if (inputWithoutPipes.startsWith(DOT)) {
            // only node information is present ".entry"
            inputWithoutPipes = inputWithoutPipes.replace(DOT, StringUtils.EMPTY);
            parsed[0] = currentFlow;
            parsed[1] = inputWithoutPipes.trim();
        } else if (inputWithoutPipes.contains(DOT)) {
            // both flow and node information is present "MainFlow.entry"
            parsed = inputWithoutPipes.split("\\.");
            parsed[0] = parsed[0].trim();
            parsed[1] = parsed[1].trim();
        } else {
            // only node information "entry"
            // current flow is mandatory
            parsed[0] = currentFlow;
            parsed[1] = inputWithoutPipes.trim();
        }

        return parsed;
    }

    /**
     * Evaluates a node in a given flow using a certain context for a certain renderer
     * If the template is passed as "json", generates a JSON string output containing the output of multiple templates
     * If the template is passed not as "json", then directly evaluates the template to arrive at a string output
     *
     * @param node     to evaluate
     * @param flow     in which the node is present
     * @param context  containing a bunch of parameters
     * @param template the renderer template to be evaluated
     * @return the output of the evaluation - either a string or a json string depending on what was passed in template
     * @throws IOException if there is a error in evaluation
     */
    public String evalNode(Flow flow, Node node, VelocityContext context, String template) throws IOException {
        EvaluationContext evaluationContext = new EvaluationContext()
                .setFlow(flow).setNode(node).setContext(context).setTemplate(template);
        return EvalNodeDaemon.evalNode(evaluationCommand, evaluationContext);
    }

    public FlowUtil setEvaluationCommand(EvaluationCommand evaluationCommand) {
        this.evaluationCommand = evaluationCommand;
        return this;
    }
}

