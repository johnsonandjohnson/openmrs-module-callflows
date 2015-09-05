package com.janssen.connectforlife.callflows.domain;

import org.motechproject.mds.annotations.Entity;

import org.joda.time.DateTime;
import java.util.Map;

import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;

/**
 * Call Log, a log of what happens at each step of a call
 * This class is completely adapted from the CallDetailRecord of the MoTeCH IVR module
 *
 * @author bramak09
 * @see Call
 */
@Entity
public class CallLog {

    private Long id;

    private DateTime timestamp;

    private String providerTimestamp;

    private String from;

    private String to;

    private CallDirection direction;

    private CallStatus callStatus;

    private String callId;

    private String providerCallId;

    private Map<String, String> providerData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getProviderTimestamp() {
        return providerTimestamp;
    }

    public void setProviderTimestamp(String providerTimestamp) {
        this.providerTimestamp = providerTimestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public CallDirection getDirection() {
        return direction;
    }

    public void setDirection(CallDirection direction) {
        this.direction = direction;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getProviderCallId() {
        return providerCallId;
    }

    public void setProviderCallId(String providerCallId) {
        this.providerCallId = providerCallId;
    }

    public Map<String, String> getProviderData() {
        return providerData;
    }

    public void setProviderData(Map<String, String> providerData) {
        this.providerData = providerData;
    }
}
