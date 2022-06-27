/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.evaluation.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.callflows.api.domain.flow.Block;
import org.openmrs.module.callflows.api.domain.flow.Element;
import org.openmrs.module.callflows.api.domain.flow.FieldElement;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.FormBlock;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.Template;
import org.openmrs.module.callflows.api.domain.flow.TextElement;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.evaluation.EvaluationCommand;
import org.openmrs.module.callflows.api.evaluation.EvaluationContext;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseEvaluationCommand extends BaseOpenmrsService implements EvaluationCommand {

    private static final String JSON = "json";

    private static final String EMPTY_ACTIONS = "[]";

    private static final String VAR_SPEAK = "txt";

    private static final String VAR_ASK = "field";

    private static final String VAR_ASK_TYPE = "type";

    private static final String VAR_ASK_META = "meta";

    private static final String VAR_ASK_BARGE_IN = "bargeIn";

    private static final String VAR_ASK_DTMF = "dtmf";

    private static final String VAR_ASK_VOICE = "voice";

    private static final String VAR_ASK_NO_INPUT = "noInput";

    private static final String VAR_ASK_NO_MATCH = "noMatch";

    private static final String VAR_ASK_GOODBYE = "goodbye";

    private static final String VAR_ASK_REPROMPT = "reprompt";

    private DaemonToken daemonToken;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    @Override
    public DaemonToken getDaemonToken() {
        return daemonToken;
    }

    @Override
    public String execute(EvaluationContext evalContext) throws IOException {
        Flow flow = evalContext.getFlow();
        Node node = evalContext.getNode();
        VelocityContext context = evalContext.getContext();
        String template = evalContext.getTemplate();
        // if type of template to execute is JSON, we need to eval the whole node model, otherwise we just need to eval a specific typed template
        // JSON is used for the test runner and the runner needs enough information to run and hence we evaluate most
        // attributes of the node model.
        // For a specific typed template like vxml, all content is already in the VXML template, so we just have to execute the main template
        if (template.equals(JSON)) {
            if (node instanceof UserNode) {
                return evalJsonNode(flow, (UserNode) node, context);
            } else {
                return EMPTY_ACTIONS;
            }
        }

        Template tpl = node.getTemplates().get(template);

        return evalTemplate(flow, node, context, tpl.getContent());
    }

    private String evalJsonNode(Flow flow, UserNode node, VelocityContext context) throws IOException {
        // inside a node object, we can have different templates in various forms.
        // We need to eval all of these for our test runner to work

        // This is our output
        // A list of actions to perform on the client.
        // Since the output of this method is a JSON string, we don't bother with classes and just use the Java collections API
        List<Map<String, Object>> output = new ArrayList<>();

        // The node has a list of blocks
        List<Block> blocks = node.getBlocks();
        for (Block block : blocks) {
            Map<String, Object> part;
            if (block instanceof FormBlock) {
                // Each block has a list of elements
                for (Element element : block.getElements()) {
                    part = new LinkedHashMap<>();
                    output.add(part);

                    if (element instanceof TextElement) {
                        mergeTextProperties((TextElement) element, part, flow, node, context);
                    } else if (element instanceof FieldElement) {
                        mergeFieldProperties((FieldElement) element, part, flow, node, context);
                    }
                }
            }
        }
        // so we have a list of all the actions we require
        return objectMapper.writeValueAsString(output);
    }

    private String evalTemplate(Flow flow, Node node, VelocityContext context, String content) throws IOException {
        if (!StringUtils.isEmpty(content)) {
            StringWriter writer = new StringWriter();
            Velocity.evaluate(context, writer, String.format("%s.%s", flow.getName(), node.getStep()), content);
            return writer.toString();
        }
        return StringUtils.EMPTY;
    }

    private void mergeTextProperties(TextElement textElement,
                                     Map<String, Object> part,
                                     Flow flow,
                                     Node node,
                                     VelocityContext context) throws IOException {
        String template = textElement.getTxt();
        part.put(VAR_SPEAK, evalTemplate(flow, node, context, template));
    }

    private void mergeFieldProperties(FieldElement fieldElement,
                                      Map<String, Object> part,
                                      Flow flow,
                                      Node node,
                                      VelocityContext context) throws IOException {
        String template = fieldElement.getTxt();
        part.put(VAR_SPEAK, evalTemplate(flow, node, context, template));
        part.put(VAR_ASK, fieldElement.getName());
        part.put(VAR_ASK_TYPE, fieldElement.getFieldType());
        part.put(VAR_ASK_BARGE_IN, fieldElement.isBargeIn());
        part.put(VAR_ASK_DTMF, fieldElement.isDtmf());
        part.put(VAR_ASK_VOICE, fieldElement.isVoice());
        part.put(VAR_ASK_META, fieldElement.getFieldMeta());

        // The noInput Prompt
        if (fieldElement.getNoInput() != null) {
            part.put(VAR_ASK_NO_INPUT, evalTemplate(flow, node, context, fieldElement.getNoInput()));
        }

        // The noMatch Prompt
        if (fieldElement.getNoMatch() != null) {
            part.put(VAR_ASK_NO_MATCH, evalTemplate(flow, node, context, fieldElement.getNoMatch()));
        }

        if (fieldElement.getGoodBye() != null) {
            part.put(VAR_ASK_GOODBYE, evalTemplate(flow, node, context, fieldElement.getGoodBye()));
        }
        // reprompt
        part.put(VAR_ASK_REPROMPT, fieldElement.getReprompt());
    }
}
