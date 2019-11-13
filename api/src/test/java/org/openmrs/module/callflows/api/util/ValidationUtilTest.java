package org.openmrs.module.callflows.api.util;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collections;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Validation.class})
public class ValidationUtilTest extends BaseTest {

    private Object simpleObject = new Object();

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Mock
    private Path path;

    @Mock
    private ValidatorFactory validatorFactory;

    @Mock
    private Validator validator;

    @Before
    public void setUp() {
        mockStatic(Validation.class);
        when(Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactory);
        when(validatorFactory.getValidator()).thenReturn(validator);

        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(constraintViolation.getMessage()).thenReturn("message");
        when(path.toString()).thenReturn("path");
    }

    @Test
    public void shouldNotThrowExceptionWhenObjectIsValid() {
        when(validator.validate(Matchers.any())).thenReturn(Collections.emptySet());

        ValidationUtil.validate(simpleObject);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenObjectIsNotValid() {
        when(validator.validate(Matchers.any())).thenReturn(Collections.singleton(constraintViolation));

        ValidationUtil.validate(simpleObject);
    }
}