package com.janssen.connectforlife.callflows.repository;

import com.janssen.connectforlife.callflows.domain.Call;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

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

}
