package org.openmrs.module.callflows.api.domain;

import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;

import org.joda.time.DateTime;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Entity(name = "callFlow.Call")
@Table(name = "cfl_calls", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_CALLFLOWS_CALL_IDX", columnNames = {"callId"}))
public class Call extends AbstractBaseOpenmrsData {

    private static final String TEXT = "text";

    @Id
    @GeneratedValue
    @Column(name = "cfl_calls_id")
    private Integer id;

    /**
     * A UUID used to identify a call.
     * This is used in the URL's to identify a HTTP request pertaining to a specific call,
     * cause using the ID in URL's is more prone to someone guessing a call identifier, we use a uuid
     */
    @Column(nullable = false)
    private String callId;

    /**
     * A call identifier at the IVR provider's end
     */
    @Column
    private String providerCallId;

    /**
     * How did this call originate?
     */
    @Column(nullable = false)
    private CallDirection direction;

    /**
     * The current call status as we know it
     */
    @Column(nullable = false)
    private CallStatus status;

    /**
     * The call flow we started this call from. Doesn't change once set
     */
    @OneToOne
    @JoinColumn(name = "startFlow_id", nullable = false)
    private CallFlow startFlow;

    /**
     * The node we started at. Doesn't change once set
     */
    @Column(nullable = false)
    private String startNode;

    /**
     * The time we started the call at. Doesn't change once set
     */
    @Column
    private DateTime startTime;

    /**
     * The flow we ended at for now. This can change as the call continues
     */
    @OneToOne
    @JoinColumn(name = "endFlow_id", nullable = false)
    private CallFlow endFlow;

    /**
     * The end node subject to change as the call continues.
     */
    @Column(nullable = false)
    private String endNode;

    /**
     * The end time prone to revision as the call continues further
     */
    @Column
    private DateTime endTime;

    /**
     * Last time of update as per the provider's clock
     */
    @Column
    private String providerTime;

    /**
     * Number of steps (or ping-pongs) in this call that has been going so far between the caller and the system
     */
    @Column(nullable = false)
    private Long steps;

    /**
     * The configuration we are using for this call, in case the platform supports multiple configurations
     */
    @Column(nullable = false)
    private String config;

    /**
     * Identifies the actor involved in the call, say a patient or a doctor or some other user depending on the application
     * For outbound calls, other modules that initiate a call via the event system can set this field at call initiation time
     * For inbound calls, the callflow designer can update this information via the entry point flow
     * Do not store the phone number or other PII data in this field, if you have clear security requirements against storing them.
     * Instead store the primary key of your patient / doctor tables, which can uniquely identify a patient.
     * Applications can join their tables with this table directly via this column for reporting purposes
     */
    @Column
    private String actorId;

    /**
     * The type of actor involved in the call, i.e  a patient or a clinician etc in case the actorId comes from multiple tables
     * The value of this field is application dependent
     */
    @Column
    private String actorType;

    /**
     * This field will store any textual information about current call status
     */
    @Column
    private String statusText;

    /**
     * Identifies the unique id/ UUID used by other service providers in the call
     * For inbound calls, the callflow designer can update this information via the entry point flow
     * Do not store the phone number or other PII data in this field, if you have clear security requirements against storing them.
     * Instead store the primary key of your patient / doctor tables, which can uniquely identify a patient.
     * Applications can join their tables with this table directly via this column for reporting purposes
     */
    @Column
    private String externalId;

    /**
     * The type of service provider ids being used/stored in the call,
     * For eg, if separate UUID is being used from IMI provider, then this field can be used to store type like 'IMI_UUID'
     * and externalId can be used to store UUID. The value of this field is application dependent
     */
    @Column
    private String externalType;

    /**
     * This field will store information of messages played eg., voice files names
     */
    @Column(columnDefinition = TEXT)
    private String playedMessages;

    /**
     * A reference passed by different systems integrated with callflow module,
     * to identify the relation with calls, may be for reporting as well
     */
    @Column
    private String refKey;

    /**
     * The current context of this call
     * Context variables are as set in the various templates of the callflow and contain information to continue handling the call
     * Typically params from the caller are short-lived per single request and are not stored here for security reasons,
     * unless the callflow designer explicitly persists any of the params via a template.
     */
    @ElementCollection
    @CollectionTable(name = "cfl_calls_context", joinColumns=@JoinColumn(name="id_oid"))
    @MapKeyJoinColumn(name = "key")
    @Lob
    @Column(name = "value", columnDefinition = "MEDIUMBLOB")
    private Map<String, Object> context = new HashMap<>();

    /**
     * Data from the provider
     */
    @ElementCollection
    @CollectionTable(name = "cfl_calls_providerdata", joinColumns=@JoinColumn(name="id_oid"))
    @MapKeyJoinColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> providerData = new HashMap<>();

    @Column
    private DateTime creationDate;

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalType() {
        return externalType;
    }

    public void setExternalType(String externalType) {
        this.externalType = externalType;
    }

    public String getPlayedMessages() {
        return playedMessages;
    }

    public void setPlayedMessages(String playedMessages) {
        this.playedMessages = playedMessages;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
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
        return "Call{" + "id=" + id + ", callId='" + callId + '\'' + ", direction=" + direction + ", status=" + status +
                ", start='" + startFlow + '.' + startNode + '\'' + ", startTime=" + startTime + ", end='" + endFlow +
                '.' + endNode + '\'' + ", endTime='" + endTime + '\'' + ", config='" + config + '\'' + ", actorId='" +
                actorId + '\'' + ", actorType='" + actorType + '\'' + ", steps=" + steps + '}';
    }
}
