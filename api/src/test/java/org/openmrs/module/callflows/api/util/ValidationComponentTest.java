package org.openmrs.module.callflows.api.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Validation.class})
public class ValidationComponentTest extends BaseTest {

    private Object simpleObject = new Object();

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Mock
    private Path path;

    @Mock
    private LocalValidatorFactoryBean factory;

    @Mock
    private Validator validator;

    @InjectMocks
    private ValidationComponent validationComponent = new ValidationComponent();

    @Before
    public void setUp() {
        mockStatic(Validation.class);
        when(Validation.buildDefaultValidatorFactory()).thenReturn(factory);
        when(factory.getValidator()).thenReturn(validator);

        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(constraintViolation.getMessage()).thenReturn("message");
        when(path.toString()).thenReturn("path");
    }

    @Test
    public void shouldNotThrowExceptionWhenObjectIsValid() {
        when(validator.validate(Matchers.any())).thenReturn(Collections.emptySet());

        validationComponent.validate(simpleObject);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenObjectIsNotValid() {
        when(validator.validate(Matchers.any())).thenReturn(Collections.singleton(constraintViolation));

        validationComponent.validate(simpleObject);
    }
}