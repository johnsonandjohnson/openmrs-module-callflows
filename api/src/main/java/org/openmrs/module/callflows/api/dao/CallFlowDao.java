package org.openmrs.module.callflows.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.callflows.api.domain.CallFlow;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Repository service for managing call flow domain objects in the system
 *
 * @author bramak09
 */
public interface CallFlowDao extends OpenmrsDataDAO<CallFlow> {

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

}
