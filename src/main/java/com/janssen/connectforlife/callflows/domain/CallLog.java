package com.janssen.connectforlife.callflows.domain;

import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

import org.joda.time.DateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Call Log, a log of what happens at each step of a call
 * This class is completely adapted from the CallDetailRecord of the MoTeCH IVR module
 * Not tracking history in this table, as this is most rows in this table are typically write once.
 *
 * @author bramak09
 * @see Call
 */
@Entity(tableName = "cfl_calllogs")
public class CallLog {

    private Long id;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_1)
    private String callId;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_2)
    private DateTime timestamp;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_3)
    private String providerTimestamp;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_4)
    private String from;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_5)
    private String to;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_6)
    private CallDirection direction;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_7)
    private CallStatus callStatus;

    @Field
    @UIDisplayable(position = UIPositions.COLUMN_8)
    private String providerCallId;

    @Field
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CallLog)) {
            return false;
        }
        final CallLog other = (CallLog) o;
        return Objects.equals(this.callId, other.callId) && Objects.equals(this.timestamp, other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callId, timestamp);
    }

    @Override
    public String toString() {
        return "CallLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", providerTimestamp='" + providerTimestamp + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", direction=" + direction +
                ", callStatus=" + callStatus +
                ", callId='" + callId + '\'' +
                ", providerCallId='" + providerCallId + '\'' +
                '}';
    }
}
