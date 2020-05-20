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
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("callFlow.CallFlowDao")
public class CallFlowDaoImpl extends HibernateOpenmrsDataDAO<CallFlow> implements CallFlowDao {

    @Autowired
    private DbSessionFactory dbSessionFactory;

    public CallFlowDaoImpl() {
        super(CallFlow.class);
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

    @Override
    public CallFlow create(CallFlow callFlow) {
        return saveOrUpdate(callFlow);
    }

    @Override
    public CallFlow update(CallFlow callFlow) {
        return saveOrUpdate(callFlow);
    }

    @Override
    public CallFlow findById(Integer id) {
        return getById(id);
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from callFlow.CallFlow").executeUpdate();
    }

    private DbSession getSession() {
        return dbSessionFactory.getCurrentSession();
    }
}
