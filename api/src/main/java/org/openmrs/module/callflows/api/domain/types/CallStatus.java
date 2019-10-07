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
     * A call was created at the IVR provider
     */
    STARTED,

    /**
     * The destination has confirmed that the call is ringing
     */
    RINGING,

    /**
     * A call is in progress
     */
    IN_PROGRESS,

    /**
     * The destination has answered the call
     */
    ANSWERED,

    /**
     * The call was canceled by the caller
     */
    UNANSWERED,

    /**
     * The call is answered by a machine
     */
    MACHINE,

    /**
     * A call was unsuccessful because the line was busy
     */
    BUSY,

    /**
     * A call was initiated at the IVR provider
     */
    CANCELLED,

    /**
     * A call was unsuccessful for some other reason than BUSY or NO_ANSWER
     */
    FAILED,

    /**
     * The call attempt was rejected by the destination
     */
    REJECTED,

    /**
     * A call was unsuccessful because the recipient did not answer
     */
    NO_ANSWER,

    /**
     * The call timed out before it was answered
     */
    TIMEOUT,

    /**
     * A call was completed
     */
    COMPLETED,

    /**
     * I don't know what happened
     */
    UNKNOWN,
}