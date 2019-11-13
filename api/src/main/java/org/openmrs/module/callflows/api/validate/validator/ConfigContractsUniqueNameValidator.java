package org.openmrs.module.callflows.api.validate.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.callflows.ValidationMessages;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContracts;
import org.openmrs.module.callflows.api.validate.annotation.UniqueName;

public class ConfigContractsUniqueNameValidator implements ConstraintValidator<UniqueName, ConfigContracts> {

    private static final String PROPERTY_PATH = "configContracts.name";
    private static final long ONE = 1;

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
        Map<String, Long> map = new HashMap<>();
        for (ConfigContract configContract : configContracts.getConfigContracts()) {
            String name = configContract.getName();
            if (name != null) { // it should be validate by another constraint
                map.merge(name, ONE, Long::sum);
            }
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Long> e : map.entrySet()) {
            if (e.getValue() > ONE) {
                list.add(e.getKey());
            }
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public void initialize(UniqueName parameters) {
    }
}
