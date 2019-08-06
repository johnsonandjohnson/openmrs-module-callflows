package org.openmrs.module.callflows.api.domain.types;

/**
 * The status of a IVR call
 * Some statuses adapted from the IVR module
 *
 * @author bramak09
 */
public enum CallStatus {

    /**
     * Motech received an request to initiate an outbound call
     */
    MOTECH_INITIATED,

    /**
     * A call was initiated at the IVR provider
     */
    INITIATED,

    /**
     * A call is in progress
     */
    IN_PROGRESS,

    /**
     * A call was successfully answered & terminated
     */
    ANSWERED,

    /**
     * A call was unsuccessful because the line was busy
     */
    BUSY,

    /**
     * A call was unsuccessful for some other reason than BUSY or NO_ANSWER
     */
    FAILED,

    /**
     * A call was unsuccessful because the recipient did not answer
     */
    NO_ANSWER,

    /**
     * A call was completed
     */
    COMPLETED,

    /**
     * I don't know what happened
     */
    UNKNOWN,
}
