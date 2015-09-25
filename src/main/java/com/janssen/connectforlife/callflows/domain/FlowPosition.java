package com.janssen.connectforlife.callflows.domain;

import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.flow.Node;

import java.util.List;

/**
 * A Class to capture the current position of a flow i.e. it's current step and whether that is a final terminating position
 * and all nodes visited to arrive at this point
 *
 * @author bramak09
 */
public class FlowPosition {

    /**
     * The flow in this instance
     */
    private Flow flow;

    /**
     * The node that we started at to arrive at this position
     */
    private Node start;

    /**
     * The current position
     */
    private Node end;

    /**
     * The final output that was generated at the last system node
     */
    private String output;

    /**
     * Nodes that were visited to arrive at the final output
     */
    private List<Node> visited;

    /**
     * Whether we have reached a terminating position
     */
    private boolean terminated;

    public Flow getFlow() {
        return flow;
    }

    public FlowPosition setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    public Node getStart() {
        return start;
    }

    public FlowPosition setStart(Node start) {
        this.start = start;
        return this;
    }

    public Node getEnd() {
        return end;
    }

    public FlowPosition setEnd(Node end) {
        this.end = end;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public FlowPosition setOutput(String output) {
        this.output = output;
        return this;
    }

    public List<Node> getVisited() {
        return visited;
    }

    public FlowPosition setVisited(List<Node> visited) {
        this.visited = visited;
        return this;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public FlowPosition setTerminated(boolean terminated) {
        this.terminated = terminated;
        return this;
    }
}
