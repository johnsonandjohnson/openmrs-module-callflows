package org.openmrs.module.callflows.api.service.impl;

import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.repository.CallFlowDataService;
import org.openmrs.module.callflows.api.service.CallFlowService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Call Flow service implementation
 *
 * @author bramak09
 */
@Service("callFlowService")
public class CallFlowServiceImpl implements CallFlowService {

    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private CallFlowDataService callFlowDataService;

    @Override
    @Transactional
    public CallFlow create(CallFlow callflow) throws CallFlowAlreadyExistsException {
        if (StringUtils.isEmpty(callflow.getName()) || !ALPHA_NUMERIC.matcher(callflow.getName()).matches()) {
            throw new IllegalArgumentException(
                    "Callflow name is required and must contain only alphanumeric characters :" + callflow.getName());
        }
        // check for duplicates in database
        if (null != callFlowDataService.findByName(callflow.getName())) {
            throw new CallFlowAlreadyExistsException("CallFlow already exists! :" + callflow.getName());
        }
        return callFlowDataService.create(callflow);
    }

    @Override
    @Transactional
    public CallFlow update(CallFlow callflow) throws CallFlowAlreadyExistsException {
        if (StringUtils.isEmpty(callflow.getName()) || !ALPHA_NUMERIC.matcher(callflow.getName()).matches()) {
            throw new IllegalArgumentException(
                    "Callflow name is required and must contain only alphanumeric characters :" + callflow.getName());
        }
        // Attempt to find the flow we are trying to update by querying for name
        // The idea of querying by name instead of id serves dual purpose with one query.
        // First we can check whether the call flow that we are trying to update exists
        // Second we can check whether the id of this call flow matches the one we are trying to update
        // If the second condition fails, it means that the call flow name is being changed to another callflow's name
        CallFlow existingFlow = callFlowDataService.findByName(callflow.getName());
        if (null != existingFlow && !existingFlow.getId().equals(callflow.getId())) {
            throw new CallFlowAlreadyExistsException(
                    "Callflow name is already used by another flow :" + callflow.getName());
        }
        if (null == existingFlow) {
            existingFlow = callFlowDataService.findById(callflow.getId());
            if (null == existingFlow) {
                throw new IllegalArgumentException("Callflow not retrievable for invalid id : " + callflow.getId());
            }
        }
        // update the fields on the retrieved object, so that JDO correctly recognizes the object's state
        existingFlow.setName(callflow.getName());
        existingFlow.setDescription(callflow.getDescription());
        existingFlow.setRaw(callflow.getRaw());
        existingFlow.setStatus(callflow.getStatus());
        return callFlowDataService.update(existingFlow);
    }

    @Override
    @Transactional
    public List<CallFlow> findAllByNamePrefix(String prefix) {
        return callFlowDataService.findAllByName(prefix);
    }

    @Override
    public CallFlow findByName(String name) {
        CallFlow callflow = callFlowDataService.findByName(name);
        if (null == callflow) {
            throw new IllegalArgumentException("Callflow cannot be found for name : " + name);
        } else {
            return callflow;
        }
    }

    @Override
    public void delete(Long id) {
        CallFlow callflow = callFlowDataService.findById(id);
        if (callflow == null) {
            throw new IllegalArgumentException("Callflow cannot be found for id : " + id);
        } else {
            callFlowDataService.delete(callflow);
        }
    }
}

