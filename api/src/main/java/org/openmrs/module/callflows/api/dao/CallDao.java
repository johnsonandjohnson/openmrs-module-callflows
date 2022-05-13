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

import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;

import java.util.List;
import java.util.Set;

/**
 * Call Data Service
 *
 * @author bramak09
 */
public interface CallDao {

    /**
     * Find a Call record using the call id
     *
     * @param callId
     * @return the call object
     */
    Call findByCallId(String callId);

    /**
     * Find the calls based on the call direction and call status
     *
     * @param direction A direction of a call with respect to the system
     * @param statusSet The set of IVR Call status
     * @return list of calls for the specified Call direction and Call status
     */
    List<Call> findCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet);

    /**
     * Find the call count based on the call direction and call status
     *
     * @param direction A direction of a call with respect to the system
     * @param statusSet The set of IVR Call status
     * @return call count for the specified Call direction and Call status
     */
    long countFindCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet);

    Call create(Call call);

    Call update(Call call);

    Call findById(Integer id);

    List<Call> retrieveAll(int startingRecord, int recordsAmount);

    void deleteAll();

    long count();

}
