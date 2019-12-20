package org.openmrs.module.callflows.api.service.it;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.module.callflows.api.contract.CFLPerson;
import org.openmrs.module.callflows.api.helper.PersonHelper;
import org.openmrs.module.callflows.api.service.CFLPersonService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CFLPersonServiceITTest extends BaseModuleContextSensitiveTest {

	private static final String XML_DATA_SET_PATH = "datasets/";
	private static final String PERSON_DATA_SET = "PersonDataSet.xml";

	private static final String PATIENT_STATUS_TYPE = "Patient status";

	@Autowired
	private CFLPersonService cflPersonService;

	@Autowired
	private PersonDAO personDAO;

	@Before
	public void setUp() throws Exception {
		executeDataSet(XML_DATA_SET_PATH + PERSON_DATA_SET);
	}

	@Test
	public void shouldFind3PersonsByPhoneWithDeadFalse() {
		List<CFLPerson> persons = cflPersonService.findByPhone(PersonHelper.PHONE_NUMBER1, false);

		assertThat(persons.size(), equalTo(3));
		assertThat(PersonHelper.containsPerson(persons, PersonHelper.FIRST_CAREGIVER_NAME), equalTo(true));
		assertThat(PersonHelper.containsPerson(persons, PersonHelper.SECOND_CAREGIVER_NAME), equalTo(true));
		assertThat(PersonHelper.containsPerson(persons, PersonHelper.NO_CONSENT_PATIENT_NAME), equalTo(true));
	}

	@Test
	public void shouldFind1PersonByPhoneExcludingVoidedNumberWithDeadFalse() {
		List<CFLPerson> persons = cflPersonService.findByPhone(PersonHelper.PHONE_NUMBER2, false);

		assertThat(persons.size(), equalTo(1));
		assertTrue(PersonHelper.containsPerson(persons, PersonHelper.THIRD_CAREGIVER_NAME));
		assertThat(persons.get(0).getPhone(), equalTo(PersonHelper.PHONE_NUMBER2));
		assertNotNull(persons.get(0).getPerson());
		assertTrue(persons.get(0).isCaregiver());
		assertFalse(persons.get(0).isPatient());
		assertThat(persons.get(0).getPersonId(), equalTo(PersonHelper.THIRD_CAREGIVER_ID));
	}

	@Test
	public void shouldFind2PersonsByPhoneWithDeadTrue() {
		List<CFLPerson> persons = cflPersonService.findByPhone(PersonHelper.PHONE_NUMBER2, true);

		assertThat(persons.size(), equalTo(2));
		assertTrue(PersonHelper.containsPerson(persons, PersonHelper.THIRD_CAREGIVER_NAME));
		assertTrue(PersonHelper.containsPerson(persons, PersonHelper.DEAD_PATIENT_NAME));
	}

	@Test
	public void shouldFind1PersonByPhoneWithDeadFalse() {
		List<CFLPerson> persons = cflPersonService.findByPhone(PersonHelper.PHONE_NUMBER3, false);

		assertThat(persons.size(), equalTo(1));
		assertTrue(PersonHelper.containsPerson(persons, PersonHelper.ACTIVE_PATIENT_NAME));
		assertThat(persons.get(0).getPhone(), equalTo(PersonHelper.PHONE_NUMBER3));
		assertNotNull(persons.get(0).getPerson());
		assertFalse(persons.get(0).isCaregiver());
		assertTrue(persons.get(0).isPatient());
		assertThat(persons.get(0).getPersonId(), equalTo(PersonHelper.ACTIVE_PATIENT_ID));
	}

	@Test
	public void shouldSavePersonStatusIfNotExisting() {
		final String statusValue = "ACTIVE";

		cflPersonService.savePersonAttribute(
				PersonHelper.FIRST_CAREGIVER_ID, PATIENT_STATUS_TYPE, statusValue);

		PersonAttribute personAttribute =
				personDAO.getPerson(PersonHelper.FIRST_CAREGIVER_ID).getAttribute(PATIENT_STATUS_TYPE);

		assertNotNull(personAttribute);
		assertThat(personAttribute.getValue(), equalTo(statusValue));
	}

	@Test
	public void shouldSavePersonStatusWithExistingCurrentValue() {
		final String statusValue = "DEACTIVATE";

		cflPersonService.savePersonAttribute(
				PersonHelper.NO_CONSENT_PATIENT_ID, PATIENT_STATUS_TYPE, statusValue);

		PersonAttribute personAttribute =
				personDAO.getPerson(PersonHelper.NO_CONSENT_PATIENT_ID).getAttribute(PATIENT_STATUS_TYPE);

		assertNotNull(personAttribute);
		assertThat(personAttribute.getValue(), equalTo(statusValue));
	}

	@Test
	public void shouldSavePersonStatusWithExistingCurrentValueAndWithTheVoidedOne() {
		final String statusValue = "NO_CONSENT";

		cflPersonService.savePersonAttribute(
				PersonHelper.DEAD_PATIENT_ID, PATIENT_STATUS_TYPE, statusValue);

		PersonAttribute personAttribute =
				personDAO.getPerson(PersonHelper.DEAD_PATIENT_ID).getAttribute(PATIENT_STATUS_TYPE);

		assertNotNull(personAttribute);
		assertThat(personAttribute.getValue(), equalTo(statusValue));
	}
}
