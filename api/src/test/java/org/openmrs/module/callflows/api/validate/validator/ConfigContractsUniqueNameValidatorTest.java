package org.openmrs.module.callflows.api.validate.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContracts;
import org.openmrs.module.callflows.testbuilder.ConfigContractBuilder;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigContractsUniqueNameValidatorTest extends BaseTest {

    private ConfigContract configContractA;
    private ConfigContract configContractB;
    private ConfigContract configContractC;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder validationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuilder;

    private ConfigContractsUniqueNameValidator validator = new ConfigContractsUniqueNameValidator();

    @Before
    public void setUp() {
        configContractA = new ConfigContractBuilder().withName("A").build();
        configContractB = new ConfigContractBuilder().withName("B").build();
        configContractC = new ConfigContractBuilder().withName("C").build();

        given(context.buildConstraintViolationWithTemplate(any())).willReturn(validationBuilder);
        given(validationBuilder.addNode(any())).willReturn(nodeBuilder);
    }

    @Test
    public void shouldBeValidWhenContractsAreNull() {
        Assert.assertTrue(validator.isValid(null, context));
        Assert.assertTrue(validator.isValid(new ConfigContracts(), context));
    }

    @Test
    public void shouldBeValidWhenAllNamesAreUnique() {
        ConfigContracts contracts = buildValidConfigContracts();

        Assert.assertTrue(validator.isValid(contracts, context));
    }

    @Test
    public void shouldNotBeValidWhenNotAllNamesAreUnique() {
        ConfigContracts contracts = buildNotValidConfigContracts();

        assertFalse(validator.isValid(contracts, context));
    }

    @Test
    public void shouldBuildCorrectValidationCause() {
        ConfigContracts contracts = buildNotValidConfigContracts();

        assertFalse(validator.isValid(contracts, context));
        verify(validationBuilder).addNode(eq("configContracts.name"));
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void shouldBeInitializedWithoutAnyExceptionThrown() {
        validator.initialize(null);
    }

    private ConfigContracts buildValidConfigContracts() {
        return new ConfigContracts(Arrays.asList(
                configContractA, configContractB, configContractC
        ));
    }

    private ConfigContracts buildNotValidConfigContracts() {
        return new ConfigContracts(Arrays.asList(
                configContractC, configContractB, configContractC
        ));
    }
}
