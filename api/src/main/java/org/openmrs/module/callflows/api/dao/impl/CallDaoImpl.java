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
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.HibernateOpenmrsObjectDAO;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;

import java.util.List;
import java.util.Set;

public class CallDaoImpl extends HibernateOpenmrsObjectDAO<Call> implements CallDao {

  public CallDaoImpl() {
    this.mappedClass = Call.class;
  }

  /**
   * Looking the specific {@link Call} object based on provided callId. If the object is not null
   * then {@link DbSession#refresh(Object)} method is called in order to make sure that the latest
   * value will be returned in case of processing the call flow by many threads.
   *
   * @param callId - provided call id
   * @return - found call object
   */
  @Override
  public Call findByCallId(String callId) {
    Criteria criteria = getSession().createCriteria(this.mappedClass);
    criteria.add(Restrictions.eq("callId", callId));
    Call call = (Call) criteria.uniqueResult();
    if (call != null) {
      getSession().refresh(call);
    }
    return call;
  }

  @Override
  public List<Call> findCallsByDirectionAndStatus(
      CallDirection direction, Set<CallStatus> statusSet) {
    return createCriteriaForFinding(direction, statusSet).list();
  }

  @Override
  public long countFindCallsByDirectionAndStatus(
      CallDirection direction, Set<CallStatus> statusSet) {
    Criteria crit = createCriteriaForFinding(direction, statusSet);

    Number count = (Number) crit.setProjection(Projections.rowCount()).uniqueResult();
    return count.longValue();
  }

  @Override
  public Call saveCall(Call call) {
    return saveOrUpdate(call);
  }

  @Override
  public Call findById(Integer id) {
    Object result = getSession().get(this.mappedClass, id);
    return result != null ? (Call) result : null;
  }

  @Override
  public List<Call> retrieveAll(int startingRecord, int recordsAmount) {
    Criteria criteria = getSession().createCriteria(this.mappedClass);
    criteria.setFirstResult(startingRecord);
    criteria.setMaxResults(recordsAmount);

    return criteria.list();
  }

  @Override
  public void deleteAll() {
    getSession().createQuery("delete from callFlow.Call").executeUpdate();
  }

  @Override
  public long count() {
    Criteria criteria = getSession().createCriteria(this.mappedClass);
    criteria.setProjection(Projections.rowCount());
    return (long) criteria.uniqueResult();
  }

  private Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  private Criteria createCriteriaForFinding(CallDirection direction, Set<CallStatus> statusSet) {
    Criteria criteria = getSession().createCriteria(this.mappedClass);
    criteria.add(Restrictions.eq("direction", direction));
    Disjunction or = Restrictions.disjunction();
    for (CallStatus callStatus : statusSet) {
      or.add(Restrictions.eq("status", callStatus));
    }
    criteria.add(or);

    return criteria;
  }
}
