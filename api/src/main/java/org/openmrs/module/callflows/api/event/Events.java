package org.openmrs.module.callflows.api.event;

/**
 * Call flow events
 */
public final class Events {

    /**
     * The callflows module listens to this event to initiate a call
     */
    public static final String CALLFLOWS_INITIATE_CALL = "callflows-call-initiate";

    /**
     * The callflows module sends this event continuously for clients to listen to, provided the CCXML Handler is setup
     */
    public static final String CALLFLOWS_CALL_STATUS = "callflows-call-status";

    // private constructor
    private Events() {
    }

}
