package org.openmrs.module.callflows.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;

import java.util.List;

public class CallFlowDaoImpl extends HibernateOpenmrsDataDAO<CallFlow> implements CallFlowDao {
    
    @Override
    public CallFlow findByName(String name) {
        return null;
    }

    @Override
    public List<CallFlow> findAllByName(String prefix) {
        return null;
    }
}
