package org.openmrs.module.callflows.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.db.UserDAO;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.util.ValidationComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Call Flow service implementation
 *
 * @author bramak09
 */
@Service("callFlowService")
@Transactional
public class CallFlowServiceImpl implements CallFlowService {


    private static final String ADMIN_USER = "admin";

    @Autowired
    private CallFlowDao callFlowDao;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ValidationComponent validationComponent;

    @Override
    public CallFlow create(CallFlow callflow) {
        validationComponent.validate(callflow);
        if (Context.isSessionOpen() && !Context.isAuthenticated()) {
            callflow.setCreator(userDAO.getUserByUsername(ADMIN_USER));
        }
        return callFlowDao.create(callflow);
    }

    @Override
    public CallFlow update(CallFlow callflow) {
        validationComponent.validate(callflow);

        // Attempt to find the flow we are trying to update by querying for name
        // The idea of querying by name instead of id serves dual purpose with one query.
        // First we can check whether the call flow that we are trying to update exists
        // Second we can check whether the id of this call flow matches the one we are trying to update
        // If the second condition fails, it means that the call flow name is being changed to another callflow's name
        CallFlow existingFlow = callFlowDao.findByName(callflow.getName());
        if (null == existingFlow) {
            existingFlow = callFlowDao.findById(callflow.getId());
            if (null == existingFlow) {
                throw new IllegalArgumentException("Callflow not retrievable for invalid id : " + callflow.getId());
            }
        }
        // update the fields on the retrieved object, so that JDO correctly recognizes the object's state
        existingFlow.setName(callflow.getName());
        existingFlow.setDescription(callflow.getDescription());
        existingFlow.setRaw(callflow.getRaw());
        existingFlow.setStatus(callflow.getStatus());
        return callFlowDao.update(existingFlow);
    }

    @Override
    public List<CallFlow> findAllByNamePrefix(String prefix) {
        return callFlowDao.findAllByName(prefix);
    }

    @Override
    public CallFlow findByName(String name) {
        CallFlow callflow = callFlowDao.findByName(name);
        if (null == callflow) {
            throw new IllegalArgumentException("Callflow cannot be found for name : " + name);
        } else {
            return callflow;
        }
    }

    @Override
    public void delete(Integer id) {
        CallFlow callflow = callFlowDao.findById(id);
        if (callflow == null) {
            throw new IllegalArgumentException("Callflow cannot be found for id : " + id);
        } else {
            callFlowDao.delete(callflow);
        }
    }
}
