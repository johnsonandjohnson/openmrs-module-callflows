/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
public class CallDaoImpl implements CallDao {

    private DbSessionFactory dbSessionFactory;
    private Class mappedClass;

    public CallDaoImpl() {
        this.mappedClass = Call.class;
    }

    /**
     * Looking the specific {@link Call} object based on provided callId.
     * If the object is not null then {@link DbSession#refresh(Object)} method is called in order to
     * make sure that the latest value will be returned in case of
     * processing the call flow by many threads.
     *
     * @param callId - provided call id
     * @return - found call object
     */
    @Override
    public Call findByCallId(String callId) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("callId", callId));
        Call call = (Call) crit.uniqueResult();
        if (call != null) {
            getSession().refresh(call);
        }
        return call;
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
        getSession().saveOrUpdate(call);
        return call;
    }

    /**
     * Updates the call using the {@link org.openmrs.api.db.hibernate.DbSession#update(Object)} method.
     * The following propagation is used {@link org.springframework.transaction.annotation.Propagation#REQUIRES_NEW}.
     * <p>
     * The new transaction is used because of we want to update always the latest value in case of
     * processing the call flow by many threads. Additionally {@link DbSession#flush()} is called after update.
     *
     * @param call- call resource which should be update
     * @return the updated value of the call
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Call update(Call call) {
        getSession().update(call);
        getSession().flush();
        return call;
    }

    @Override
    public Call findById(Integer id) {
        Object result = getSession().get(this.mappedClass, id);
        return result != null ? (Call) result : null;
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
        return dbSessionFactory.getCurrentSession();
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

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }
}
