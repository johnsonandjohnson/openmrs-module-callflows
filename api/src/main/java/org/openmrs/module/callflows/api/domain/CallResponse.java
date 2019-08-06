package org.openmrs.module.callflows.api.domain;

/**
 * Call Response - a response from processing a node
 *
 * @author bramak09
 */
public class CallResponse {

    private Call call;

    private String output;

    private String error;

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
