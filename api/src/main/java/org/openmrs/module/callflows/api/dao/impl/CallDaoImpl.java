package org.openmrs.module.callflows.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Repository("callFlow.CallDao")
@Transactional
public class CallDaoImpl extends HibernateOpenmrsDataDAO<Call> implements CallDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    public CallDaoImpl(){
        super(Call.class);
    }

    @Override
    public Call findByCallId(String callId) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("callId", callId));

        return (Call) crit.uniqueResult();
    }

    @Override
    public List<Call> findCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        Criteria crit = createCriteriaForFinding(direction, statusSet);

        return crit.list();
    }

    @Override
    public long countFindCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        Criteria crit = createCriteriaForFinding(direction, statusSet);

        Number count = (Number) crit.setProjection(Projections.rowCount()).uniqueResult();
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
    public List<Call> retrieveAll(int startingRecord, int recordsAmount) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.setFirstResult(startingRecord);
        crit.setMaxResults(recordsAmount);

        return crit.list();
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from callFlow.Call").executeUpdate();
    }

    @Override
    public long count() {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.setProjection(Projections.rowCount());
        return (long) crit.uniqueResult();
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    private Criteria createCriteriaForFinding(CallDirection direction, Set<CallStatus> statusSet) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("direction", direction));
        Disjunction or = Restrictions.disjunction();
        for (CallStatus callStatus : statusSet) {
            or.add(Restrictions.eq("status", callStatus));
        }
        crit.add(or);

        return crit;
    }
}
