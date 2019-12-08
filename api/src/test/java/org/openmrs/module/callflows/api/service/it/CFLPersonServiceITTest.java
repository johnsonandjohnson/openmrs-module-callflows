package org.openmrs.module.callflows.api.service.it;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.module.callflows.api.contract.CFLPerson;
import org.openmrs.module.callflows.api.service.CFLPersonService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CFLPersonServiceITTest extends BaseModuleContextSensitiveTest {

	private static final String PERSON1_PHONE = "111111111111";
	private static final String PERSON1_CONSENT = "true";

	private static final String PERSON2_PHONE = "2222222222";
	private static final String PERSON2_CONSENT = "false";

	@Autowired
	private CFLPersonService cflPersonService;

	@Autowired
	private PersonService personService;

	private PersonAttributeType phoneType;
	private PersonAttributeType consentType;

	private PersonAttribute consentAttribute1;
	private PersonAttribute consentAttribute2;

	@Before
	public void setUp() {
		phoneType = new PersonAttributeType();
		phoneType.setName("Telephone Number");
		phoneType.setFormat(String.class.getName());

		consentType = new PersonAttributeType();
		consentType.setName("dndConsent");
		consentType.setFormat(String.class.getName());

		personService.savePersonAttributeType(phoneType);
		personService.savePersonAttributeType(consentType);

		consentAttribute1 = new PersonAttribute();
		consentAttribute1.setAttributeType(consentType);
		consentAttribute1.setValue(PERSON1_CONSENT);

		consentAttribute2 = new PersonAttribute();
		consentAttribute2.setAttributeType(consentType);
		consentAttribute2.setValue(PERSON2_CONSENT);

		createPersonWithData(PERSON1_PHONE, consentAttribute1);
		createPersonWithData(PERSON2_PHONE, consentAttribute2);
	}

	@Test
	public void shouldReturnProperCFLPerson() {
		CFLPerson actual = cflPersonService.findByPhone(PERSON1_PHONE);

		assertThat(actual.getPhoneNumber(), equalTo(PERSON1_PHONE));
		assertTrue(actual.isConsent());
	}

	@Test
	public void shouldReturnNullIfCFLPersonNotFound() {
		CFLPerson actual = cflPersonService.findByPhone("wrong number");

		assertNull(actual);
	}

	@Test
	public void shouldUpdateExistingConsent() {
		assertThat(consentAttribute1.getValue(), equalTo(PERSON1_CONSENT));

		cflPersonService.saveConsent(consentAttribute1.getId(), PERSON2_CONSENT);

		assertThat(
				personService.getPersonAttribute(consentAttribute1.getId()).getValue(), equalTo(PERSON2_CONSENT));
	}

	private void createPersonWithData(String phoneNumber, PersonAttribute consentAttribute) {
		Person person = new Person();

		PersonName personName = new PersonName();
		personName.setFamilyName("Family Name");
		personName.setGivenName("Given Name");
		person.addName(personName);

		PersonAttribute phoneAttribute = new PersonAttribute();
		phoneAttribute.setAttributeType(phoneType);
		phoneAttribute.setValue(phoneNumber);

		person.addAttribute(phoneAttribute);
		person.addAttribute(consentAttribute);

		personService.savePerson(person);
	}
}
