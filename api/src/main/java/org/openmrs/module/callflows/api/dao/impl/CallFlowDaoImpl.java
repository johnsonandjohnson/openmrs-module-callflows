package org.openmrs.module.callflows.api.dao.impl;

import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CallFlowDaoImpl extends HibernateOpenmrsDataDAO<CallFlow> implements CallFlowDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    public CallFlowDaoImpl() {
        super(CallFlow.class);
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public CallFlow findByName(String name) {
        return null;
    }

    @Override
    public List<CallFlow> findAllByName(String prefix) {
        return null;
    }
}
