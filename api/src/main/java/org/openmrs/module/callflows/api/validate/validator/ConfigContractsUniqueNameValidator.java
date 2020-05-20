package org.openmrs.module.callflows.api.validate.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.callflows.ValidationMessages;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContracts;
import org.openmrs.module.callflows.api.validate.annotation.UniqueName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigContractsUniqueNameValidator implements ConstraintValidator<UniqueName, ConfigContracts> {

    private static final String PROPERTY_PATH = "configContracts.name";

    /**
     * Validates the name uniqueness.
     *
     * @param configContracts object which wraps configs to validate
     * @param context         validation context
     * @return the validation result
     */
    @Override
    public boolean isValid(ConfigContracts configContracts, ConstraintValidatorContext context) {
        if (configContracts != null && configContracts.getConfigContracts() != null) {
            List<String> notUniqueNames = findNotUniqueNames(configContracts);

            if (!notUniqueNames.isEmpty()) {
                String message = String.format(ValidationMessages.NOT_UNIQUE_CONFIG_NAME, StringUtils.join(notUniqueNames, ", "));
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addNode(PROPERTY_PATH)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private List<String> findNotUniqueNames(ConfigContracts configContracts) {
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
