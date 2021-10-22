package org.openmrs.module.callflows;

public final class ValidationMessageConstants {

    public static final String VALIDATION_ERROR_OCCURS = "Check the form and send it again.";
    public static final String SENT_DATA_IS_NOT_VALID = "Sent not valid data due to: \n";

    public static final String NOT_UNIQUE_NAME = "Name is not unique";
    public static final String NOT_UNIQUE_CONFIG_NAME = "Names of configs are not unique: %s";
    public static final String CALL_FLOW_INVALID = "The call flow is invalid";
    public static final String CALL_FLOW_NAME_BLANK_OR_NON_ALFA_NUMERIC = "Callflow name is required and must " +
            "contain only alphanumeric characters";
    public static final String CALL_FLOW_NAME_DUPLICATION = "Call flow with name %s already exists";
    public static final String CALL_FLOW_NODES_NULL = "Flow nodes cannot be null (use empty array" +
            " instead).";
    public static final String CALL_FLOW_NODE_NAME_BLANK_OR_NON_ALFA_NUMERIC = "Call flow " +
            "step name is required and must contain only alphanumeric characters";

    private ValidationMessageConstants() {
    }
}
