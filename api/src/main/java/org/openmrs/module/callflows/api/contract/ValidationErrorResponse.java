package org.openmrs.module.callflows.api.contract;

import java.util.Map;

/**
 * Error Response Contract extended with constraint validation details
 *
 */
public class ValidationErrorResponse extends ErrorResponse {

    /**
     * The cause of not valid request.
     */
    private Map<String, String> constraintViolations;

    public <T> ValidationErrorResponse(String code, String message, Map<String, String> constraintViolations) {
        super(code, message);
        this.constraintViolations = constraintViolations;
    }

    public Map<String, String> getConstraintViolations() {
        return constraintViolations;
    }
}
