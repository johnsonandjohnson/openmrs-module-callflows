package org.openmrs.module.callflows.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
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
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("name", name));

        return (CallFlow) crit.uniqueResult();
    }

    @Override
    public List<CallFlow> findAllByName(String prefix) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.like("name", prefix, MatchMode.START));
        
        return crit.list();
    }
}
