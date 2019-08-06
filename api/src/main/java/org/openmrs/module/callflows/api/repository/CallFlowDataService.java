package com.janssen.connectforlife.callflows.repository;

import com.janssen.connectforlife.callflows.domain.CallFlow;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Repository service for managing call flow domain objects in the system
 *
 * @author bramak09
 */
public interface CallFlowDataService extends MotechDataService<CallFlow> {

    /**
     * Finds a single call flow by name using an exact match
     *
     * @param name
     * @return the callflow
     */
    @Lookup
    CallFlow findByName(@LookupField(name = "name") String name);

    /**
     * Finds all call flows that start with the given name
     *
     * @param prefix
     * @return a list of the callflows
     */
    @Lookup
    List<CallFlow> findAllByName(@LookupField(name = "name", customOperator = "startsWith()") String prefix);

}
