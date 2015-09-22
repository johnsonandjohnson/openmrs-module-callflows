package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.domain.flow.Block;
import com.janssen.connectforlife.callflows.domain.flow.Element;
import com.janssen.connectforlife.callflows.domain.flow.FieldElement;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.flow.FormBlock;
import com.janssen.connectforlife.callflows.domain.flow.Node;
import com.janssen.connectforlife.callflows.domain.flow.Template;
import com.janssen.connectforlife.callflows.domain.flow.TextElement;
import com.janssen.connectforlife.callflows.domain.flow.UserNode;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of Flow Utilities for working with flows and nodes
 *
 * @author bramak09
 */
@Component("flowUtil")
public class FlowUtil {

    private static final String DOT = ".";

    private static final Pattern STEP_REGEX = Pattern.compile("\\|\\s*([^|]+)\\s*\\|");

    private static final String EMPTY_ACTIONS = "[]";

    private static final String JSON = "json";

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

    private ObjectMapper objectMapper = new ObjectMapper();

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
     * |MainFlow.| => {MainFlow, null}
     * |MainFlow.active| => {MainFlow, active}
     * |entry| => {currentFlow, entry} - currentFlow is a passed argument
     * |.entry| => {currentFlow, entry} - currentFlow is a passed argument
     * <p/>
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
        String[] parsed = { null, null };

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

