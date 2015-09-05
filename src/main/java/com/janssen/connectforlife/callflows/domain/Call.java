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
 * The Call class represents the context of a IVR call
 * This class is intended to provide enough information to fulfill high level overview call reporting
 * For more detailed information, refer to CallLog class
 *
 * @author bramak09
 * @see com.janssen.connectforlife.callflows.domain.CallLog
 */
@Entity
public class Call {

    private Long id;

    /**
     * A UUID used to identify a call.
     * This is used in the URL's to identify a HTTP request pertaining to a specific call,
     * cause using the ID in URL's is more prone to someone guessing a call identifier, we use a uuid
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_1)
    private String callId;

    /**
     * How did this call originate?
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_2)
    private CallDirection direction;

    /**
     * The current call status as we know it
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_3)
    private CallStatus status;

    /**
     * The call flow we started this call from. Doesn't change once set
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_4)
    private CallFlow startFlow;

    /**
     * The node we started at. Doesn't change once set
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_5)
    private String startNode;

    /**
     * The time we started the call at. Doesn't change once set
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_6)
    private DateTime startTime;

    /**
     * The flow we ended at for now. This can change as the call continues
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_7)
    private CallFlow endFlow;

    /**
     * The end node subject to change as the call continues.
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_8)
    private String endNode;

    /**
     * The end time prone to revision as the call continues further
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_9)
    private DateTime endTime;

    /**
     * Number of steps (or ping-pongs) in this call that has been going so far between the caller and the system
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_10)
    private Long steps;

    /**
     * The configuration we are using for this call, in case the platform supports multiple configurations
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_11)
    private String config;

    /**
     * The current context of this call
     * Context vars are as set in the various templates of the callflow
     * Typically params from the caller are short-lived per single request and are not all stored here for security reasons,
     * unless explicitly persisted in a template
     */
    @Field
    private Map<String, Object> context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public CallDirection getDirection() {
        return direction;
    }

    public void setDirection(CallDirection direction) {
        this.direction = direction;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public CallFlow getStartFlow() {
        return startFlow;
    }

    public void setStartFlow(CallFlow startFlow) {
        this.startFlow = startFlow;
    }

    public void setEndFlow(CallFlow endFlow) {
        this.endFlow = endFlow;
    }

    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public CallFlow getEndFlow() {
        return endFlow;
    }

    public String getEndNode() {
        return endNode;
    }

    public void setEndNode(String endNode) {
        this.endNode = endNode;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
        this.steps = steps;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Call)) {
            return false;
        }
        final Call other = (Call) o;
        return Objects.equals(this.id, other.id) && Objects.equals(this.callId, other.callId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, callId);
    }

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", callId='" + callId + '\'' +
                ", direction=" + direction +
                ", status=" + status +
                ", start='" + startFlow + '.' + startNode + '\'' +
                ", startTime=" + startTime +
                ", end='" + endFlow + '.' + endNode + '\'' +
                ", endTime='" + endTime + '\'' +
                ", config='" + config + '\'' +
                ", steps=" + steps +
                '}';
    }
}
