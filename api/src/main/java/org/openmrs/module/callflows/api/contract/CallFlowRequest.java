package org.openmrs.module.callflows.api.contract;

/**
 * The CallFlow contract used in both create and update requests
 *
 * @author bramak09
 */
public class CallFlowRequest {

    /**
     * Name of the call flow
     */
    private String name;

    /**
     * Description of the call flow
     */
    private String description;

    /**
     * Raw JSON string representing the call flow
     */
    private String raw;

    /**
     * Call flow status
     */
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
