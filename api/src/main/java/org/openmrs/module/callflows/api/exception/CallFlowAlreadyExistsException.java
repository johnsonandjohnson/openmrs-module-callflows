package org.openmrs.module.callflows.api.exception;

/**
 * Exception to indicate a callflow already exists if added as a duplicate
 *
 * @author bramak09
 */
public class CallFlowAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 8862259136519432428L;

    public CallFlowAlreadyExistsException(String message) {
        super(message);
    }

}


