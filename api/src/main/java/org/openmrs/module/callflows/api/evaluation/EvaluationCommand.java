/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.evaluation;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.DaemonToken;

import java.io.IOException;

/**
 * Evaluation Command
 */
public interface EvaluationCommand extends OpenmrsService {

    /**
     * Sets the Daemon token
     *
     * @param daemonToken Daemon Token
     */
    void setDaemonToken(DaemonToken daemonToken);

    /**
     * Gets the Daemon token
     *
     * @return returns the Daemon token
     */
    DaemonToken getDaemonToken();

    /**
     * Executes the Evaluation
     *
     * @param context Evaluation context
     * @throws IOException
     * @return Returns string
     */
    String execute(EvaluationContext context) throws IOException;
}
