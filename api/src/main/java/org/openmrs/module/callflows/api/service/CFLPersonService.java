package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.contract.CFLPerson;

public interface CFLPersonService {

	CFLPerson findByPhone(String phone);

	void saveConsent(Integer consentId, String value, String personUuid);
}
