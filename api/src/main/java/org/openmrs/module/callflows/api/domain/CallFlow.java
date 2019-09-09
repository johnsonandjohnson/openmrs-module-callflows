package org.openmrs.module.callflows.api.domain;

import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * The call flow domain
 *
 * @author bramak09
 */
@Entity(name = "callFlow.CallFlow")
@Table(name = "cfl_callflows", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_CALLFLOW_IDX", columnNames = {"name"}))
public class CallFlow extends AbstractBaseOpenmrsData {

    private static final String TEXT = "text";

    @Id
    @GeneratedValue
    @Column(name = "cfl_callflows_id")
    private Integer id;

    /**
     * The callflow name. Typically alpha-numeric.
     * Can not contain dots strictly, as a dot is used to separate a call flow from a step in a call flow
     */
    @Column
    private String name;

    /**
     * A description of the call flow
     */
    @Column
    private String description;

    /**
     * The call flow status
     * Used to determine whether a call flow is active in the system or being currently worked on (DRAFT) mode
     */
    @Column
    private CallFlowStatus status;

    /**
     * A JSON serialized structure of the whole call flow model
     * The JSON structure itself is inspired from the VoiceXML format as that's the standard for Voice communications
     * However best effort has been made to choose those elements/attributes that can be applicable even in areas
     * where VoiceXML might not be used. The flow structure is therefore a very minified subset of the whole VoiceXML schema
     *
     * @see org.openmrs.module.callflows.api.domain.flow.Flow
     */
    @Column(columnDefinition = TEXT)
    private String raw;

    /**
     * Creates a CallFlow instance
     *
     * @param name        The name of a callflow, typically title cased and can contain only alphanumeric characters
     * @param description The callflow description
     * @param status      Callflow status
     * @param raw         A json serialized representation of the callflow
     */
    public CallFlow(String name, String description, CallFlowStatus status, String raw) {
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

    public CallFlowStatus getStatus() {
        return status;
    }

    public void setStatus(CallFlowStatus status) {
        this.status = status;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CallFlow)) {
            return false;
        }
        final CallFlow other = (CallFlow) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CallFlow{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

}
