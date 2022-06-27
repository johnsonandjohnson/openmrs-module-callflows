/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.dao;

import org.openmrs.module.callflows.api.domain.CallFlow;

import java.util.List;

/**
 * Repository service for managing call flow domain objects in the system
 *
 * @author bramak09
 */
public interface CallFlowDao {

    /**
     * Finds a single call flow by name using an exact match
     *
     * @param name
     * @return the callflow
     */
    CallFlow findByName(String name);

    /**
     * Finds all call flows that start with the given name
     *
     * @param prefix
     * @return a list of the callflows
     */
    List<CallFlow> findAllByName(String prefix);

    CallFlow create(CallFlow callFlow);

    CallFlow update(CallFlow callFlow);

    CallFlow findById(Integer id);

    void deleteAll();

    void delete(CallFlow callFlow);
}
