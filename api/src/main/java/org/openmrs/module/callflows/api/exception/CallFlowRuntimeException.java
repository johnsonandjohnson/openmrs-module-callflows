package org.openmrs.module.callflows.api.exception;

public class CallFlowRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 8226846364778234649L;

    public CallFlowRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public CallFlowRuntimeException(String message) {
        super(message);
    }

    public CallFlowRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
