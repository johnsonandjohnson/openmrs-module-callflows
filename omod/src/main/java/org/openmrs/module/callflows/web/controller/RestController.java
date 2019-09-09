package org.openmrs.module.callflows.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.contract.ErrorResponse;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base Rest Controller
 * All controllers in this module extend this for easy error handling
 *
 * @author bramak09
 */
public class RestController {

    private static final Log LOGGER = LogFactory.getLog(RestController.class);

    private static final String ERR_CALLFLOW_DUPLICATE = "callflow.duplicate";

    private static final String ERR_SYSTEM = "system.error";

    private static final String ERR_BAD_PARAM = "system.param";

    /**
     * Exception handler for bad request - Http status code of 400
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleException(IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_BAD_PARAM, e.getMessage());
    }


    /**
     * Exception handler for conflict request or entity already exists exception - Http status code of 409
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(CallFlowAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleException(CallFlowAlreadyExistsException e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_CALLFLOW_DUPLICATE, e.getMessage());
    }

    /**
     * Exception handler for anything not covered above - Http status code of 500
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_SYSTEM, e.getMessage());
    }
}

