package org.openmrs.module.callflows.api.exception;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ValidationExceptionTest extends BaseTest {

    private static final String FIELD_NAME = "fieldName";
    private static final String ERROR_CAUSE = "error cause";

    private Map<String, String> constraintViolations;

    @Before
    public void setUp() {
        constraintViolations = Collections.singletonMap(FIELD_NAME, ERROR_CAUSE);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void shouldCreateWithoutAnyExceptionThrown() {
        new ValidationException(constraintViolations);
    }

    @Test
    public void toStringShouldContainsValidationErrorCauseInPrettyPrintStyle() {
        ValidationException validationException = new ValidationException(constraintViolations);

        assertEquals("org.openmrs.module.callflows.api.exception.ValidationException\n{\n" +
                "  \"fieldName\": \"error cause\"\n}", validationException.toString());
    }
}
