package org.openmrs.module.callflows.api.domain.types;

/**
 * Status of a CallFlow
 *
 * @author bramak09
 */
public enum CallFlowStatus {

    /**
     * Saved, but not a active flow
     */
    DRAFT,

    /**
     * An active flow
     */
    ACTIVE
}
