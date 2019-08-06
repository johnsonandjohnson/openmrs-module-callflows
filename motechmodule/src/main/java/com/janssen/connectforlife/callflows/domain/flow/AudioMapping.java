package com.janssen.connectforlife.callflows.domain.flow;

/**
 * Audio Mapping to indicate the information against a specific text mapping
 *
 * @author bramak09
 * @see Flow
 */
public class AudioMapping {

    /**
     * The mapping key, typically this could be a CMS-Lite key, but that is not mandatory and this could also be a custom key
     * representing a application specific value
     */
    private String mapping;

    /**
     * A target to where this mapping is applicable. This is typically a JSON string with multiple properties
     * Since this is all required at the client and the server here is just a dumb pipe, we just keep this as a string to allow
     * the UI to iterate independently from the server
     */
    private String target;

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
