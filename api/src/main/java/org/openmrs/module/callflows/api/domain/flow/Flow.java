package com.janssen.connectforlife.callflows.domain.flow;

import java.util.List;
import java.util.Map;
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

    /**
     * Audio map indicating in this flow, what strings are mapped to what audio files
     * The same text can be mapped in multiple points to different files. This is possible, though not recommended.
     * It's useful if a certain text always links to the same audio. Some places exceptions might be required, for instance
     * Hello in one place might be Hello, whereas Hello in an entry node can link to a signature music, as even if the signature music is not
     * present, then the text to speech can still say something useful
     */
    private Map<String, List<AudioMapping>> audio;

    /**
     * Stores  meta information about the flow. Currently this is used to store a serialized form of the visualization data
     * This is a string field to promote re-usability, as additional meta fields can continue to be stored here simply by changing
     * the front-end interface
     */
    private String meta;

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

    public Map<String, List<AudioMapping>> getAudio() {
        return audio;
    }

    public void setAudio(Map<String, List<AudioMapping>> audio) {
        this.audio = audio;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
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
