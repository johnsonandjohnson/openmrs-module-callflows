package org.openmrs.module.callflows.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class CallDaoImpl extends HibernateOpenmrsDataDAO<Call> implements CallDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    public CallDaoImpl(){
        super(Call.class);
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Call findByCallId(String callId) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("callId", callId));

        return (Call) crit.uniqueResult();
    }

    @Override
    public List<Call> findCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("direction", direction));
        Disjunction or = Restrictions.disjunction();
        for (CallStatus callStatus : statusSet) {
            or.add(Restrictions.eq("status", callStatus));
        }
        crit.add(or);

        return crit.list();
    }

    @Override
    public long countFindCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("direction", direction));
        Disjunction or = Restrictions.disjunction();
        for (CallStatus callStatus : statusSet) {
            or.add(Restrictions.eq("status", callStatus));
        }
        crit.add(or);

        Number count = (Number) crit.uniqueResult();
        return count.longValue();
    }

    @Override
    public Call create(Call call) {
        return saveOrUpdate(call);
    }

    @Override
    public Call update(Call call) {
        return saveOrUpdate(call);
    }

    @Override
    public Call findById(Integer id) {
        return getById(id);
    }

    @Override
    public List<Call> retrieveAll() {
        return getAll(false);
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from callFlow.Call");
    }

    @Override
    public long count() {
        return getAllCount(false);
    }
}
