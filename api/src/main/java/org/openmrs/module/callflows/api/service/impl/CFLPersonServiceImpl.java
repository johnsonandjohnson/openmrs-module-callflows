package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.contract.CFLPerson;
import org.openmrs.module.callflows.api.service.CFLPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("cflPersonService")
@Transactional
public class CFLPersonServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute> implements CFLPersonService {

	@Autowired
	private DbSessionFactory sessionFactory;

	@Autowired
	private PersonDAO personDAO;

	public CFLPersonServiceImpl() {
		super(PersonAttribute.class);
	}

	@Override
	public CFLPerson findByPhone(String phone) {
		Criteria crit = getSession().createCriteria(this.mappedClass);
		crit.add(Restrictions.like("value", phone, MatchMode.EXACT));

		List<PersonAttribute> personAttributes = crit.list();

		if (CollectionUtils.isEmpty(personAttributes)) {
			return null;
		} else {
			return convertToCFLPerson(personAttributes.get(0).getPerson(), phone);
		}
	}

	@Override
	public void saveConsent(int consentId, String value) {
		PersonAttribute personAttribute = personDAO.getPersonAttribute(consentId);
		personAttribute.setValue(value);
		getSession().saveOrUpdate(personAttribute);
	}

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	private CFLPerson convertToCFLPerson(Person person, String phone) {
		List<RelationshipType> relationshipTypes =
				personDAO.getRelationshipTypes("Caregiver/Caretaker", null);
		List<Relationship> relationships = personDAO.getRelationships(person, null,
				CollectionUtils.isEmpty(relationshipTypes) ? null : relationshipTypes.get(0));

		CFLPerson cflPerson = new CFLPerson();
		cflPerson.setCaregiver(!CollectionUtils.isEmpty(relationships));

		PersonAttribute dndConsent = person.getAttribute("dndConsent");
		if (dndConsent != null) {
			cflPerson.setConsent(StringUtils.equalsIgnoreCase(dndConsent.getValue(), "true"));
			cflPerson.setConsentId(dndConsent.getId());
		}

		PersonAttribute patientStatus = person.getAttribute("Patient status");
		if (patientStatus != null) {
			cflPerson.setActivated(StringUtils.equalsIgnoreCase(patientStatus.getValue(), "Active"));
		}

		cflPerson.setPhoneNumber(phone);
		return cflPerson;
	}
}
