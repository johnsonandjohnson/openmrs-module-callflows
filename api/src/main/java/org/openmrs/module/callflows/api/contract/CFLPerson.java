package org.openmrs.module.callflows.api.contract;

public class CFLPerson {

	private boolean caregiver = false;
	private boolean consent = true;
	private boolean activated = true;
	private String phoneNumber;
	private Integer consentId;
	private String personUuid;

	public boolean isCaregiver() {
		return caregiver;
	}

	public void setCaregiver(boolean caregiver) {
		this.caregiver = caregiver;
	}

	public boolean isConsent() {
		return consent;
	}

	public void setConsent(boolean consent) {
		this.consent = consent;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getConsentId() {
		return consentId;
	}

	public void setConsentId(Integer consentId) {
		this.consentId = consentId;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}
}
