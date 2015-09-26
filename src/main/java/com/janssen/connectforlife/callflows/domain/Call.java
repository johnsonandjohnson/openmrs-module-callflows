package com.janssen.connectforlife.callflows.domain;

import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

import org.joda.time.DateTime;
import javax.jdo.annotations.Unique;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Call class represents the context of a IVR call
 * This class is intended to provide enough information to fulfill high level overview call reporting
 * For more detailed information of what happens at each step, refer the history table
 * <p/>
 * The backing table of this entity can be directly joined with application tables via the actorId and actorType fields for
 * application specific reporting needs.
 *
 * @author bramak09
 */
@Entity(recordHistory = true, tableName = "cfl_calls")
@Unique(name = "UNIQUE_CALLFLOWS_CALL_IDX", members = { "callId" })
public class Call {

    private Long id;

    /**
     * A UUID used to identify a call.
     * This is used in the URL's to identify a HTTP request pertaining to a specific call,
     * cause using the ID in URL's is more prone to someone guessing a call identifier, we use a uuid
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_1)
    private String callId;

    /**
     * A call identifier at the IVR provider's end
     */
    @Field
    private String providerCallId;

    /**
     * How did this call originate?
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_2)
    private CallDirection direction;

    /**
     * The current call status as we know it
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_3)
    private CallStatus status;

    /**
     * The call flow we started this call from. Doesn't change once set
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_4)
    private CallFlow startFlow;

    /**
     * The node we started at. Doesn't change once set
     */
    @Field(required = true)
    private String startNode;

    /**
     * The time we started the call at. Doesn't change once set
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_5)
    private DateTime startTime;

    /**
     * The flow we ended at for now. This can change as the call continues
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_6)
    private CallFlow endFlow;

    /**
     * The end node subject to change as the call continues.
     */
    @Field(required = true)
    private String endNode;

    /**
     * The end time prone to revision as the call continues further
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_7)
    private DateTime endTime;

    /**
     * Last time of update as per the provider's clock
     */
    @Field
    private String providerTime;

    /**
     * Number of steps (or ping-pongs) in this call that has been going so far between the caller and the system
     */
    @Field(required = true)
    private Long steps;

    /**
     * The configuration we are using for this call, in case the platform supports multiple configurations
     */
    @Field(required = true)
    @UIDisplayable(position = UIPositions.COLUMN_8)
    private String config;

    /**
     * Identifies the actor involved in the call, say a patient or a doctor or some other user depending on the application
     * For outbound calls, other modules that initiate a call via the event system can set this field at call initiation time
     * For inbound calls, the callflow designer can update this information via the entry point flow
     * Do not store the phone number or other PII data in this field, if you have clear security requirements against storing them.
     * Instead store the primary key of your patient / doctor tables, which can uniquely identify a patient.
     * Applications can join their tables with this table directly via this column for reporting purposes
     */
    @Field
    @UIDisplayable(position = UIPositions.COLUMN_9)
    private String actorId;

    /**
     * The type of actor involved in the call, i.e  a patient or a clinician etc in case the actorId comes from multiple tables
     * The value of this field is application dependent
     */
    @Field
    private String actorType;

    /**
     * This field will store any textual information about current call status
     */
    @Field
    private String statusText;

    /**
     * The current context of this call
     * Context variables are as set in the various templates of the callflow and contain information to continue handling the call
     * Typically params from the caller are short-lived per single request and are not stored here for security reasons,
     * unless the callflow designer explicitly persists any of the params via a template.
     */
    @Field
    private Map<String, Object> context = new HashMap<>();

    /**
     * Data from the provider
     */
    @Field
    private Map<String, String> providerData = new HashMap<>();

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

    public String getProviderCallId() {
        return providerCallId;
    }

    public void setProviderCallId(String providerCallId) {
        this.providerCallId = providerCallId;
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

    public void setEndFlow(CallFlow endFlow) {
        this.endFlow = endFlow;
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

    public String getProviderTime() {
        return providerTime;
    }

    public void setProviderTime(String providerTime) {
        this.providerTime = providerTime;
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

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
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
        if (!(o instanceof Call)) {
            return false;
        }
        final Call other = (Call) o;
        return Objects.equals(this.callId, other.callId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callId);
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
                ", actorId='" + actorId + '\'' +
                ", actorType='" + actorType + '\'' +
                ", steps=" + steps +
                '}';
    }
}
