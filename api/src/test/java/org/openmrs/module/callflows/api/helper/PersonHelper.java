package org.openmrs.module.callflows.api.helper;

import org.h2.util.StringUtils;
import org.openmrs.module.callflows.api.contract.CFLPerson;

import java.util.List;

public final class PersonHelper {

	// John First Caregiver , number - 48222444666, old number - 48222444555
	public static final String FIRST_CAREGIVER_NAME = "John First Caregiver";
	public static final Integer FIRST_CAREGIVER_ID = 100;

	// John Second Caregiver, number - 48222444666
	public static final String SECOND_CAREGIVER_NAME = "John Second Caregiver";

	// John Third Caregiver, number - 48222444555
	public static final String THIRD_CAREGIVER_NAME = "John Third Caregiver";
	public static final Integer THIRD_CAREGIVER_ID = 102;

	// George Active Patient, number - 48222444111, ACTIVE
	public static final String ACTIVE_PATIENT_NAME = "George Active Patient";
	public static final Integer ACTIVE_PATIENT_ID = 103;

	// George NO_CONSENT Patient, number - 48222444666, NO_CONSENT
	public static final String NO_CONSENT_PATIENT_NAME = "George NO_CONSENT Patient";
	public static final Integer NO_CONSENT_PATIENT_ID = 104;

	// George Dead Patient, number - 48222444555, Dead true, current status - DEACTIVATE, old status - ACTIVATE
	public static final String DEAD_PATIENT_NAME = "George Dead Patient";
	public static final Integer DEAD_PATIENT_ID = 105;

	// Relationships
	// John First Caregiver -> George Active Patient
	// John Second Caregiver -> George NO_CONSENT Patient
	// John Third Caregiver -> George Dead Patient
	public static final String PHONE_NUMBER1 = "48222444666";
	public static final String PHONE_NUMBER2 = "48222444555";
	public static final String PHONE_NUMBER3 = "48222444111";

	public static boolean containsPerson(List<CFLPerson> cflPeople, String fullName) {
		boolean contain = false;
		for (CFLPerson cflPerson : cflPeople) {
			if (StringUtils.equals(fullName, cflPerson.getPerson().getPersonName().toString())) {
				contain = true;
				break;
			}
		}

		return contain;
	}

	private PersonHelper() {
	}
}
