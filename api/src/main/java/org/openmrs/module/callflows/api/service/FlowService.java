/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service;

import org.apache.velocity.VelocityContext;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.FlowStep;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;

import java.io.IOException;

/**
 * Flow Service to manage the representation of a callflow
 *
 * @author bramak09
 */
public interface FlowService extends OpenmrsService {


    /**
     * Parses a input string into Flow and step components
     * The format supported is one of the following types and leads to the following parse outputs
     * All formats are delimited by pipes, as in velocity templates they are aesthetically pleasant to visualize
     * and they also communicate that this is a pipe to elsewhere, i.e a pipe to another flow or step
     * Supported formats:
     * <p></p>
     * |MainFlow.| =&gt; flow = MainFlow, step = entry, where entry is the first node in the MainFlow
     * |MainFlow.active| =&gt; flow = MainFlow, step = active, where active is a valid node in the MainFlow flow
     * |entry| =&gt; flow = MainFlow, step = entry, where MainFlow is the currentFlow. currentFlow is mandatory here
     * |.entry| =&gt; flow = MainFlow, step = entry, where MainFlow is the currentFlow. currentFlow is mandatory here
     * <p></p>
     * Note: spaces are supported liberally all across the pattern and will be truncated before parse
     *
     * @param input       to parse
     * @param currentFlow that is being executed
     * @return a flow step containing a successful parse
     * @throws IllegalArgumentException if either of flow or step could not be identified or step is not a valid step in the given flow
     * @see org.openmrs.module.callflows.api.util.FlowUtil
     */
    FlowStep parse(String input, Flow currentFlow);

    /**
     * Loads a flow by a given name
     *
     * @param name to search
     * @return the flow
     * @throws IllegalArgumentException if the flow could not be loaded
     */
    Flow load(String name);

    /**
     * Loads a flow by a given JSON
     *
     * @param json to search
     * @return the flow
     * @throws IllegalArgumentException if the flow could not be loaded
     */
    Flow loadByJson(String json);

    /**
     * Evaluate a node
     * If a user node was passed, will not evaluate, but return the current position
     * If a system node was passed, will evaluate until control reaches a user node. The user node encountered will not be evaulated
     * After each evaluation, if the output is a regular jump syntax in one of the acceptable formats and the result points
     * to a subsequent System Node, then evaluation is continued
     * Has guards against infinite and circular loops by checking the number of jumps, max allowed = 20
     *
     * @param flow    current
     * @param node    current - either a user node or a system node
     * @param context current context
     * @return FlowPosition with current flow, node, output of evaluation and whether the flow is supposed to be terminated
     * @throws IllegalStateException if a long running loop with respect to number of jumps is detected
     */
    FlowPosition evalNode(Flow flow, Node node, VelocityContext context) throws IOException;
}


