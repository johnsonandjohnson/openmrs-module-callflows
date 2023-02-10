/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.db.UserDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.util.ValidationComponent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Call Flow service implementation
 *
 * @author bramak09
 */
@Transactional
public class CallFlowServiceImpl extends BaseOpenmrsService implements CallFlowService {

    private static final String ADMIN_USER = "admin";

    private CallFlowDao callFlowDao;
    private ValidationComponent validationComponent;

    private static final String USER_DAO_BEAN_NAME = "userDAO";

    /**
     * Creates the CallFlow
     *
     * @param callflow CallFlow
     * @return Return CallFlow
     */
    @Override
    public CallFlow create(CallFlow callflow) {
        validationComponent.validate(callflow);
        if (Context.isSessionOpen() && !Context.isAuthenticated()) {
            callflow.setCreator(Context.getRegisteredComponent(USER_DAO_BEAN_NAME, UserDAO.class)
                    .getUserByUsername(ADMIN_USER));
        }
        return callFlowDao.create(callflow);
    }

    /**
     * Saves the CallFlow
     *
     * @param callFlow CallFlow
     * @return Return CallFlow
     */
    @Override
    public CallFlow saveCallFlow(CallFlow callFlow) {
        return create(callFlow);
    }

    /**
     * Updates the CallFlow
     *
     * @param callflow CallFlow
     * @return Return CallFlow
     */
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

    /**
     * Finds all CallFlow names by prefix string.
     *
     * @param prefix String Prefix
     * @return Return list of CallFlows
     */
    @Override
    @Transactional(readOnly = true)
    public List<CallFlow> findAllByNamePrefix(String prefix) {
        return callFlowDao.findAllByName(prefix);
    }

    /**
     * Finds the CallFlow by name string
     *
     * @param name String name
     * @return Return CallFlow
     */
    @Override
    @Transactional(readOnly = true)
    public CallFlow findByName(String name) {
        CallFlow callflow = callFlowDao.findByName(name);
        if (null == callflow) {
            throw new IllegalArgumentException("Callflow cannot be found for name : " + name);
        } else {
            return callflow;
        }
    }

    /**
     * Deletes the CallFlow
     *
     * @param id CallFlow Id
     */
    @Override
    public void delete(Integer id) {
        CallFlow callflow = callFlowDao.findById(id);
        if (callflow == null) {
            throw new IllegalArgumentException("Callflow cannot be found for id : " + id);
        } else {
            callFlowDao.delete(callflow);
        }
    }

    /**
     * Sets the CallFlow Dao
     *
     * @param callFlowDao CallFlowDao
     */
    public void setCallFlowDao(CallFlowDao callFlowDao) {
        this.callFlowDao = callFlowDao;
    }

    /**
     * Sets the Validation component
     *
     * @param validationComponent Validation Component
     */
    public void setValidationComponent(ValidationComponent validationComponent) {
        this.validationComponent = validationComponent;
    }
}
