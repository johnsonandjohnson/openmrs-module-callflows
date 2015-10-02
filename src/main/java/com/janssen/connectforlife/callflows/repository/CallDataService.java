package com.janssen.connectforlife.callflows.repository;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;
import java.util.Set;

/**
 * Call Data Service
 *
 * @author bramak09
 */
public interface CallDataService extends MotechDataService<Call> {

    /**
     * Find a Call record using the call id
     *
     * @param callId
     * @return the call object
     */
    @Lookup
    Call findByCallId(@LookupField(name = "callId") String callId);

    /**
     * Find the calls based on the call direction and call status
     *
     * @param direction A direction of a call with respect to the system
     * @param statusSet The set of IVR Call status
     * @return list of calls for the specified Call direction and Call status
     */
    @Lookup
    List<Call> findCallsByDirectionAndStatus(@LookupField(name = "direction") CallDirection direction,
                                             @LookupField(name = "status") Set<CallStatus> statusSet);

    /**
     * Find the call count based on the call direction and call status
     *
     * @param direction A direction of a call with respect to the system
     * @param statusSet The set of IVR Call status
     * @return call count for the specified Call direction and Call status
     */
    long countFindCallsByDirectionAndStatus(@LookupField(name = "direction") CallDirection direction,
                                            @LookupField(name = "status") Set<CallStatus> statusSet);

}
