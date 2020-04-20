package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.contract.CFLPerson;

import java.util.List;

public interface CFLPersonService {

    List<CFLPerson> findByPhone(String phone, boolean dead);

    void savePersonAttribute(Integer personId, String attributeTypeName, String attributeValue);
}
