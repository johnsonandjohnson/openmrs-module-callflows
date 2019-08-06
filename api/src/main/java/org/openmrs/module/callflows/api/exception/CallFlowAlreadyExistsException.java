package org.openmrs.module.callflows.api.exception;

/**
 * Exception to indicate a callflow already exists if added as a duplicate
 *
 * @author bramak09
 */
public class CallFlowAlreadyExistsException extends Exception {

    public CallFlowAlreadyExistsException(String message) {
        super(message);
    }

}


