package org.openmrs.module.callflows.api.validate.annotation;

import org.openmrs.module.callflows.ValidationMessageConstants;
import org.openmrs.module.callflows.api.validate.validator.ConfigContractListUniqueNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Constraint(validatedBy = { ConfigContractListUniqueNameValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueName {

    /**
     * Specify the message in case of a validation error
     *
     * @return the message about the error
     */
    String message() default ValidationMessageConstants.NOT_UNIQUE_NAME;

    /**
     * Specify validation groups, to which this constraint belongs
     *
     * @return array with group classes
     */
    Class<?>[] groups() default {
    };

    /**
     * Specify custom payload objects
     *
     * @return array with payload classes
     */
    Class<? extends Payload>[] payload() default {
    };
}
