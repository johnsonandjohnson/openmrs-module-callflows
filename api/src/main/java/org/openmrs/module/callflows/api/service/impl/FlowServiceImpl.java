/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.FlowStep;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.SystemNode;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.FlowUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Flow Service Implementation
 *
 * @author bramak09
 */
@Transactional(propagation = Propagation.SUPPORTS)
public class FlowServiceImpl extends BaseOpenmrsService implements FlowService {

  private static final Log LOGGER = LogFactory.getLog(FlowServiceImpl.class);

  private static final String SPACE = " ";

  /**
   * Maximum number of sane jumps we can do when evaluating nodes continuously. We need to guard
   * ourselves against bad callflow designers who might inadvertently create infinite loops and this
   * serves to be a sane maximum before we crash and burn the CPU
   */
  private static final int MAX_JUMPS = 20;

  private static final String VELOCITY = "velocity";

  private CallFlowService callFlowService;
  private FlowUtil flowUtil;

  private ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Parses a input string into Flow and step components The format supported is one of the
   * following types and leads to the following parse outputs All formats are delimited by pipes, as
   * in velocity templates they are aesthetically pleasant to visualize and they also communicate
   * that this is a pipe to elsewhere, i.e a pipe to another flow or step Supported formats:
   *
   * <p>|MainFlow.| =&gt; flow = MainFlow, step = entry, where entry is the first node in the
   * MainFlow |MainFlow.active| =&gt; flow = MainFlow, step = active, where active is a valid node
   * in the MainFlow flow |entry| =&gt; flow = MainFlow, step = entry, where MainFlow is the
   * currentFlow. currentFlow is mandatory here |.entry| =&gt; flow = MainFlow, step = entry, where
   * MainFlow is the currentFlow. currentFlow is mandatory here
   *
   * <p>Note: spaces are supported liberally all across the pattern and will be truncated before
   * parse
   *
   * @param input to parse
   * @param currentFlow that is being executed
   * @return a flow step containing a successful parse
   * @throws IllegalArgumentException if either of flow or step could not be identified or step is
   *     not a valid step in the given flow
   * @see org.openmrs.module.callflows.api.util.FlowUtil
   */
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

  /**
   * Loads a flow by a given name
   *
   * @param name to search
   * @return the flow
   * @throws IllegalArgumentException if the flow could not be loaded
   */
  @Override
  @Transactional(readOnly = true, noRollbackFor = IllegalArgumentException.class)
  public Flow load(String name) {
    CallFlow callFlow = callFlowService.findByName(name);
    if (callFlow != null) {
      return loadByJson(callFlow.getRaw());
    } else {
      throw new IllegalArgumentException("Unable to load Flow : " + name);
    }
  }

  /**
   * Loads a flow by a given JSON
   *
   * @param json to search
   * @return the flow
   * @throws IllegalArgumentException if the flow could not be loaded
   */
  @Override
  public Flow loadByJson(String json) {
    try {
      return objectMapper.readValue(json, Flow.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("JSON parse issue. Unable to load flow : ", e);
    }
  }

  /**
   * Evaluate a node If a user node was passed, will not evaluate, but return the current position
   * If a system node was passed, will evaluate until control reaches a user node. The user node
   * encountered will not be evaulated After each evaluation, if the output is a regular jump syntax
   * in one of the acceptable formats and the result points to a subsequent System Node, then
   * evaluation is continued Has guards against infinite and circular loops by checking the number
   * of jumps, max allowed = 20
   *
   * @param startFlow current
   * @param startNode current - either a user node or a system node
   * @param context current context
   * @return FlowPosition with current flow, node, output of evaluation and whether the flow is
   *     supposed to be terminated
   * @throws IllegalStateException if a long running loop with respect to number of jumps is
   *     detected
   */
  @Override
  public FlowPosition evalNode(Flow startFlow, Node startNode, VelocityContext context)
      throws IOException {

    // capture the output after evaluation
    String output = null;
    // visited Nodes
    List<Node> visited = new ArrayList<>();
    // pointers to current flow and node
    Flow currentFlow = startFlow;
    Node currentNode = startNode;

    LOGGER.debug(String.format("evalNode:[START] %s of flow %s ", startNode, startFlow));

    // Idea is to keep evaluating until we arrive at a user node, cause at the time of confronting a
    // user-node
    // we have to communicate to the user and hence stop processing
    int jumpNo = 0;
    try {
      while (currentNode instanceof SystemNode) {
        jumpNo += 1;
        // sanity check
        if (jumpNo > MAX_JUMPS) {
          throw new IllegalStateException(
              String.format(
                  "Flow %s exceeded %d jumps. Last seen at %s",
                  currentFlow.getName(), MAX_JUMPS, buildVisited(visited)));
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
      return buildFlowPosition(
          startFlow, currentFlow, startNode, currentNode, visited, output, true);
    }
    LOGGER.debug(String.format("evalNode:[END] %s of flow %s ", currentNode, currentFlow));
    return buildFlowPosition(
        startFlow, currentFlow, startNode, currentNode, visited, output, false);
  }

  private FlowPosition buildFlowPosition(
      Flow startFlow,
      Flow endFlow,
      Node start,
      Node end,
      List<Node> visited,
      String output,
      boolean terminated) {
    return new FlowPosition()
        .setStartFlow(startFlow)
        .setEndFlow(endFlow)
        .setStart(start)
        .setEnd(end)
        .setVisited(visited)
        .setOutput(output)
        .setTerminated(terminated);
  }

  private String buildVisited(List<Node> visited) {
    final StringBuilder sb = new StringBuilder();
    for (Node node : visited) {
      sb.append(node.getStep()).append(SPACE);
    }
    return sb.toString();
  }

  /**
   * Sets the CallFlow service
   *
   * @param callFlowService to set
   */
  public void setCallFlowService(CallFlowService callFlowService) {
    this.callFlowService = callFlowService;
  }

  /**
   * Sets the Flow Util
   *
   * @param flowUtil to set
   */
  public void setFlowUtil(FlowUtil flowUtil) {
    this.flowUtil = flowUtil;
  }
}
