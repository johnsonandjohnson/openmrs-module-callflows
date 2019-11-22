package org.openmrs.module.callflows.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class ValidationComponent {

    @Autowired
    @Qualifier("springValidationFactory")
    private LocalValidatorFactoryBean factory;
    /**
     * Generic method which validates objects according its annotations.
     *
     * @param objectToValidate object to validate
     * @param clazz            class of the object. If not passed automatically inferred from objectToValidate.

     * @throws ValidationException if validation error found
     */
    public <T> void validate(T objectToValidate, Class<?>... clazz) {

        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate, clazz);
        if (!violations.isEmpty()) {
            throw new ValidationException(buildValidationErrorCause(violations));
        }
    }

    private static <T> Map<String, String> buildValidationErrorCause(Set<ConstraintViolation<T>> violations) {
        Map<String, String> map = new HashMap<>();
        for (ConstraintViolation<T> violation : violations) {
            map.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return map;
    }

}
