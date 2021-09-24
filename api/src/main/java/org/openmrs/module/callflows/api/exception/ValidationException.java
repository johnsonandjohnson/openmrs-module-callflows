package org.openmrs.module.callflows.api.exception;

import com.google.gson.GsonBuilder;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ValidationException extends RuntimeException {

    /**
     * Constraint violations that describe the error causes.
     */
    private Map<String, String> constraintViolations;

    /**
     * Creates new exception according to constraint violations.
     *
     * @param constraintViolations set of constraints that were violated
     */
    public ValidationException(Map<String, String> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}\n{1}",
                super.toString(),
                toJson(constraintViolations));
    }

    private static String toJson(Map<String, String> violations) {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(violations);
    }

    public Map<String, String> getConstraintViolations() {
        return Collections.unmodifiableMap(constraintViolations);
    }
}
