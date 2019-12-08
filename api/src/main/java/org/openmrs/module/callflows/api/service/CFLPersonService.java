package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.contract.CFLPerson;

public interface CFLPersonService {

	CFLPerson findByPhone(String phone);

	void saveConsent(int consentId, String value);
}
