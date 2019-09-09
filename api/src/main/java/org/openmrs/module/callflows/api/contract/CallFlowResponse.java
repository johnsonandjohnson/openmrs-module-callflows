package org.openmrs.module.callflows.api.contract;

/**
 * The Callflow response contract
 *
 * @author bramak09
 */
public class CallFlowResponse {

    /**
     * Call flow ID
     */
    private Integer id;

    /**
     * Call flow name
     */
    private String name;

    /**
     * Call flow description
     */
    private String description;

    /**
     * Call flow status
     */
    private String status;

    /**
     * Call flow raw representation as a string
     */
    private String raw;

    public CallFlowResponse(Integer id, String name, String description, String status, String raw) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.raw = raw;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

}
