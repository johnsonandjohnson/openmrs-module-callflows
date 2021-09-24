package org.openmrs.module.callflows.api.contract;

/**
 * Error Response Contract
 *
 * @author bramak09
 */
public class ErrorResponse {

    /**
     * The error code - more convenient for clients to look at
     */
    private String code;

    /**
     * The message of the error response for greater detail
     */
    private String message;


    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
