/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.util;

/**
 * Call flow events
 */
public final class CallFlowEventSubjectConstants {

    /**
     * The callflows module listens to this event to initiate a call
     */
    public static final String CALLFLOWS_INITIATE_CALL = "callflows-call-initiate";

    /**
     * The callflows module sends this event continuously for clients to listen to, provided the CCXML Handler is setup
     */
    public static final String CALLFLOWS_CALL_STATUS = "callflows-call-status";

    private CallFlowEventSubjectConstants() {
    }
}
