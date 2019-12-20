package org.openmrs.module.callflows.api.contract;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;

public class CFLPerson {
	private static final String TELEPHONE_NUMBER_TYPE = "Telephone Number";
	private Person person;
	private String phone;
	private boolean caregiver;
	private boolean patient;
	private Integer personId;

	public CFLPerson(Person person, boolean caregiver) {
		this.person = person;
		this.personId = person.getPersonId();
		this.caregiver = caregiver;
		this.patient = person.getIsPatient();
		PersonAttribute personAttribute = person.getAttribute(TELEPHONE_NUMBER_TYPE);
		this.phone = personAttribute != null ? personAttribute.getValue() : null;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isCaregiver() {
		return caregiver;
	}

	public void setCaregiver(boolean caregiver) {
		this.caregiver = caregiver;
	}

	public boolean isPatient() {
		return patient;
	}

	public void setPatient(boolean patient) {
		this.patient = patient;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
}
