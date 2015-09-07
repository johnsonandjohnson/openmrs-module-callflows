package com.janssen.connectforlife.callflows.domain.flow;

import java.util.List;
import java.util.Objects;

/**
 * The main flow model of a call flow
 * This is considered as the entry point to understanding the call flow model structure
 *
 * @author bramak09
 */
public class Flow {

    /**
     * The flow name
     */
    private String name;

    /**
     * The flow status
     */
    private String status;

    /**
     * A list of nodes in the flow
     */
    private List<Node> nodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flow)) {
            return false;
        }
        final Flow other = (Flow) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Flow{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
