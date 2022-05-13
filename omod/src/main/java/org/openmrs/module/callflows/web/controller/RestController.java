/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.callflows.api.contract.ErrorResponse;
import org.openmrs.module.callflows.api.contract.ValidationErrorResponse;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.openmrs.module.callflows.ValidationMessageConstants.SENT_DATA_IS_NOT_VALID;
import static org.openmrs.module.callflows.ValidationMessageConstants.VALIDATION_ERROR_OCCURS;

/**
 * Base Rest Controller
 * All controllers in this module extend this for easy error handling
 *
 * @author bramak09
 */
public abstract class RestController {

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
     * Exception handler for validation bad request - Http status code of 400
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleException(ValidationException e) {
        LOGGER.debug(SENT_DATA_IS_NOT_VALID, e);
        return new ValidationErrorResponse(ERR_BAD_PARAM, VALIDATION_ERROR_OCCURS, e.getConstraintViolations());
    }

    /**
     * Exception handler for lack of the adequate permissions - Http status code of 403
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(APIAuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleException(APIAuthenticationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_SYSTEM, e.getMessage());
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

