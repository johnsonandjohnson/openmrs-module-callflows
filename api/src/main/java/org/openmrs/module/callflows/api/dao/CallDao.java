package org.openmrs.module.callflows.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
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
public interface CallDao extends OpenmrsDataDAO<Call> {

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
