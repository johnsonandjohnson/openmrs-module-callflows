/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.validate.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.callflows.ValidationMessageConstants;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContractList;
import org.openmrs.module.callflows.api.validate.annotation.UniqueName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigContractListUniqueNameValidator implements ConstraintValidator<UniqueName, ConfigContractList> {

    private static final String PROPERTY_PATH = "configContracts.name";

    /**
     * Validates the name uniqueness.
     *
     * @param configContracts object which wraps configs to validate
     * @param context         validation context
     * @return the validation result
     */
    @Override
    public boolean isValid(ConfigContractList configContracts, ConstraintValidatorContext context) {
        if (configContracts != null && configContracts.getConfigContracts() != null) {
            List<String> notUniqueNames = findNotUniqueNames(configContracts);

            if (!notUniqueNames.isEmpty()) {
                String message = String.format(
                        ValidationMessageConstants.NOT_UNIQUE_CONFIG_NAME, StringUtils.join(notUniqueNames, ", "));
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addNode(PROPERTY_PATH)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private List<String> findNotUniqueNames(ConfigContractList configContracts) {
        List<String> notUniqueNames = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();
        for (ConfigContract configContract : configContracts.getConfigContracts()) {
            String name = configContract.getName();
            if (name != null) { // it should be validate by another constraint
                boolean added = uniqueNames.add(name);
                if (!added) {
                    notUniqueNames.add(name);
                }
            }
        }
        Collections.sort(notUniqueNames);
        return notUniqueNames;
    }

    @Override
    public void initialize(UniqueName parameters) {
        // shouldn't any specific action be performed
    }
}
