package org.openmrs.module.callflows.api.contract;

import org.openmrs.module.callflows.api.domain.Call;

/**
 * Used only for testing outbound calls
 *
 * @author bramak09
 */
public class OutboundCallResponse {

    private String callId;

    private String status;

    private String reason;

    public OutboundCallResponse(Call call) {
        callId = call.getCallId();
        status = call.getStatus().name();
        reason = call.getStatusText();
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
