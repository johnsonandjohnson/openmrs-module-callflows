package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.FlowStep;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.SystemNode;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.FlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Flow Service Implementation
 *
 * @author bramak09
 */
@Service("flowService")
@Transactional
public class FlowServiceImpl implements FlowService {

    private static final Log LOGGER = LogFactory.getLog(FlowServiceImpl.class);

    private static final String SPACE = " ";

    /**
     * Maximum number of sane jumps we can do when evaluating nodes continuously.
     * We need to guard ourselves against bad callflow designers who might inadvertently create infinite loops and this
     * serves to be a sane maximum before we crash and burn the CPU
     */
    private static final int MAX_JUMPS = 20;

    private static final String VELOCITY = "velocity";

    @Autowired
    private CallFlowService callFlowService;

    @Autowired
    private FlowUtil flowUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public FlowStep parse(String input, Flow currentFlow) {

        String currentFlowName = currentFlow == null ? null : currentFlow.getName();
        String[] parsed = flowUtil.parse(input, currentFlowName);

        Flow flow = load(parsed[0]);
        String step = parsed[1] == null ? flow.getNodes().get(0).getStep() : parsed[1];

        Node node = flowUtil.getNodeByStep(flow, step);
        if (null == node) {
            String message = String.format("Unable to load step %s in flow %s ", step, flow.getName());
            throw new IllegalArgumentException(message);
        }

        FlowStep flowStep = new FlowStep();
        flowStep.setFlow(flow).setStep(step);
        return flowStep;
    }

    @Override
    public Flow load(String name) {
        CallFlow callFlow = callFlowService.findByName(name);
        if (callFlow != null) {
            return loadByJson(callFlow.getRaw());
        } else {
            throw new IllegalArgumentException("Unable to load Flow : " + name);
        }
    }

    @Override
    public Flow loadByJson(String json) {
        try {
            return objectMapper.readValue(json, Flow.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON parse issue. Unable to load flow : ", e);
        }
    }

    @Override
    public FlowPosition evalNode(Flow startFlow, Node startNode, VelocityContext context) throws IOException {

        // capture the output after evaluation
        String output = null;
        // visited Nodes
        List<Node> visited = new ArrayList<>();
        // pointers to current flow and node
        Flow currentFlow = startFlow;
        Node currentNode = startNode;

        LOGGER.debug(String.format("evalNode:[START] %s of flow %s ", startNode, startFlow));

        // Idea is to keep evaluating until we arrive at a user node, cause at the time of confronting a user-node
        // we have to communicate to the user and hence stop processing
        int jumpNo = 0;
        try {
            while (currentNode instanceof SystemNode) {
                jumpNo += 1;
                // sanity check
                if (jumpNo > MAX_JUMPS) {
                    throw new IllegalStateException(String.format("Flow %s exceeded %d jumps. Last seen at %s",
                                                                  currentFlow.getName(),
                                                                  MAX_JUMPS,
                                                                  buildVisited(visited)));
                }
                visited.add(currentNode);
                LOGGER.debug(String.format("evalNode:[LOOP] %s of flow %s ", currentNode, currentFlow));
                output = flowUtil.evalNode(currentFlow, currentNode, context, VELOCITY);
                LOGGER.debug(String.format("evalNode:[LOOP] %s --> %s ", currentNode, output));
                FlowStep flowStep = parse(output, currentFlow);

                currentFlow = flowStep.getFlow();
                currentNode = flowUtil.getNodeByStep(flowStep.getFlow(), flowStep.getStep());
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return buildFlowPosition(startFlow, currentFlow, startNode, currentNode, visited, output, true);
        }
        LOGGER.debug(String.format("evalNode:[END] %s of flow %s ", currentNode, currentFlow));
        return buildFlowPosition(startFlow, currentFlow, startNode, currentNode, visited, output, false);
    }

    private FlowPosition buildFlowPosition(Flow startFlow,
                                           Flow endFlow,
                                           Node start,
                                           Node end,
                                           List<Node> visited,
                                           String output,
                                           boolean terminated) {
        return new FlowPosition().setStartFlow(startFlow)
                                 .setEndFlow(endFlow)
                                 .setStart(start)
                                 .setEnd(end)
                                 .setVisited(visited)
                                 .setOutput(output)
                                 .setTerminated(terminated);
    }

    private String buildVisited(List<Node> visited) {
        StringBuffer sb = new StringBuffer();
        for (Node node : visited) {
            sb.append(node.getStep()).append(SPACE);
        }
        return sb.toString();
    }
}




