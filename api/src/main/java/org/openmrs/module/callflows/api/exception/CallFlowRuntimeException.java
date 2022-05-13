/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
