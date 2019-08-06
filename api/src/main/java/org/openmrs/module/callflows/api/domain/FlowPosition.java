package org.openmrs.module.callflows.api.domain;

import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;

import java.util.List;

/**
 * A Class to capture the current position of a flow i.e. it's current step and whether that is a final terminating position
 * and all nodes visited to arrive at this point
 *
 * @author bramak09
 */
public class FlowPosition {

    /**
     * The start flow in this instance
     */
    private Flow startFlow;

    /**
     * The end flow in this instance
     */
    private Flow endFlow;

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

    public Flow getStartFlow() {
        return startFlow;
    }

    public FlowPosition setStartFlow(Flow startFlow) {
        this.startFlow = startFlow;
        return this;
    }

    public Flow getEndFlow() {
        return endFlow;
    }

    public FlowPosition setEndFlow(Flow endFlow) {
        this.endFlow = endFlow;
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
