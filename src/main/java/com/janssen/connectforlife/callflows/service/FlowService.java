package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.domain.FlowStep;
import com.janssen.connectforlife.callflows.domain.flow.Flow;

/**
 * Flow Service to manage the representation of a callflow
 *
 * @author bramak09
 */
public interface FlowService {


    /**
     * Parses a input string into Flow and step components
     * The format supported is one of the following types and leads to the following parse outputs
     * All formats are delimited by pipes, as in velocity templates they are aesthetically pleasant to visualize
     * and they also communicate that this is a pipe to elsewhere, i.e a pipe to another flow or step
     * Supported formats:
     * <p />
     * |MainFlow.| => flow = MainFlow, step = entry, where entry is the first node in the MainFlow
     * |MainFlow.active| => flow = MainFlow, step = active, where active is a valid node in the MainFlow flow
     * |entry| => flow = MainFlow, step = entry, where MainFlow is the currentFlow. currentFlow is mandatory here
     * |.entry| => flow = MainFlow, step = entry, where MainFlow is the currentFlow. currentFlow is mandatory here
     * <p />
     * Note: spaces are supported liberally all across the pattern and will be truncated before parse
     *
     * @param input       to parse
     * @param currentFlow that is being executed
     * @return a flow step containing a successful parse
     * @throws IllegalArgumentException if either of flow or step could not be identified or step is not a valid step in the given flow
     * @see com.janssen.connectforlife.callflows.util.FlowUtil
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
}


