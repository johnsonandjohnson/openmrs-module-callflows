package org.openmrs.module.callflows.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.UserDAO;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.callflows.api.contract.CFLPerson;
import org.openmrs.module.callflows.api.service.CFLPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("cflPersonService")
@Transactional
public class CFLPersonServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute> implements CFLPersonService {

	private static final String CAREGIVER_RELATIONSHIP_UUID = "acec590b-825e-45d2-876a-0028f174903d";
	private static final String ADMIN_USER = "admin";

	@Autowired
	private DbSessionFactory sessionFactory;

	@Autowired
	private PersonDAO personDAO;

	@Autowired
	private UserDAO userDAO;

	public CFLPersonServiceImpl() {
		super(PersonAttribute.class);
	}

	@Override
	public List<CFLPerson> findByPhone(String phone, boolean dead) {
		Criteria crit = getSession().createCriteria(this.mappedClass);
		crit.add(Restrictions.like("value", phone, MatchMode.EXACT));
		crit.add(Restrictions.eq("voided", false));

		List<PersonAttribute> personAttributes = crit.list();

		List<CFLPerson> cflPersonList = new ArrayList<>();
		for (PersonAttribute personAttribute : personAttributes) {
			if (!personAttribute.getPerson().getVoided() &&
					(dead || !personAttribute.getPerson().getDead())) {
				Person person = personAttribute.getPerson();
				cflPersonList.add(new CFLPerson(person, isCaregiver(person)));
			}
		}

		return cflPersonList;
	}

	@Override
	public void savePersonAttribute(Integer personId, String attributeTypeName, String attributeValue) {
		Person person = personDAO.getPerson(personId);

		List<PersonAttributeType> personAttributeTypes = personDAO.getPersonAttributeTypes
				(attributeTypeName, null, null, null);

		if (CollectionUtils.isNotEmpty(personAttributeTypes)) {
			PersonAttribute personAttribute = new PersonAttribute();
			personAttribute.setAttributeType(personAttributeTypes.get(0));
			personAttribute.setValue(attributeValue);
			if (Context.isSessionOpen() && !Context.isAuthenticated()) {
				personAttribute.setCreator(userDAO.getUserByUsername(ADMIN_USER));
			}

			person.addAttribute(personAttribute);
			personDAO.savePerson(person);
		}
	}

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	private boolean isCaregiver(Person person) {
		RelationshipType relationshipType = personDAO.getRelationshipTypeByUuid(CAREGIVER_RELATIONSHIP_UUID);

		if (relationshipType == null) {
			return false;
		} else {
			List<Relationship> relationships = personDAO.getRelationships(person, null, relationshipType);
			return !CollectionUtils.isEmpty(relationships);
		}
	}
}
