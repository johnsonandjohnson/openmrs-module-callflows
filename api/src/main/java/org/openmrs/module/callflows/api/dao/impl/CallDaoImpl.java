package org.openmrs.module.callflows.api.dao.impl;

import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;

import java.util.List;
import java.util.Set;

public class CallDaoImpl extends HibernateOpenmrsDataDAO<Call> implements CallDao {

    @Override
    public Call findByCallId(String callId) {
        return null;
    }

    @Override
    public List<Call> findCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        return null;
    }

    @Override
    public long countFindCallsByDirectionAndStatus(CallDirection direction, Set<CallStatus> statusSet) {
        return 0;
    }
}
