package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.FlowPosition;
import com.janssen.connectforlife.callflows.domain.FlowStep;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.flow.Node;
import com.janssen.connectforlife.callflows.domain.flow.SystemNode;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.FlowService;
import com.janssen.connectforlife.callflows.util.FlowUtil;

import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Flow Service Implementation
 *
 * @author bramak09
 */
@Service("flowService")
public class FlowServiceImpl implements FlowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowServiceImpl.class);

    private static final String SPACE = " ";

    /**
     * Maximum number of sane jumps we can do when evaluating nodes continuously.
     * We need to guard ourselves against bad callflow designers who might inadvertently create infinite loops and this
     * serves to be a sane maximum before we crash and burn the CPU
     */
    private static final int MAX_JUMPS = 20;

    private static final String VELOCITY = "velocity";

    @Autowired
    private CallFlowDataService callFlowDataService;

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
        try {
            CallFlow callFlow = callFlowDataService.findByName(name);
            if (callFlow != null) {
                return objectMapper.readValue(callFlow.getRaw(), Flow.class);
            } else {
                throw new IllegalArgumentException("Unable to load Flow : " + name);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON parse issue. Unable to load flow : " + name, e);
        }
    }

    @Override
    public FlowPosition evalNode(Flow flow, Node startNode, VelocityContext context) throws IOException {

        // capture the output after evaluation
        String output = null;
        // visited Nodes
        List<Node> visited = new ArrayList<>();
        // pointers to current flow and node
        Flow currentFlow = flow;
        Node currentNode = startNode;

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
                output = flowUtil.evalNode(currentFlow, currentNode, context, VELOCITY);
                FlowStep flowStep = parse(output, flow);

                currentFlow = flowStep.getFlow();
                currentNode = flowUtil.getNodeByStep(flowStep.getFlow(), flowStep.getStep());
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return buildFlowPosition(flow, startNode, currentNode, visited, output, true);
        }
        return buildFlowPosition(flow, startNode, currentNode, visited, output, false);
    }

    private FlowPosition buildFlowPosition(Flow flow,
                                           Node start,
                                           Node end,
                                           List<Node> visited,
                                           String output,
                                           boolean terminated) {
        return new FlowPosition().setFlow(flow)
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




