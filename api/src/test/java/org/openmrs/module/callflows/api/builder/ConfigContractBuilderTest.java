package org.openmrs.module.callflows.api.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.helper.ConfigHelper;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Config Contract Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigContractBuilderTest extends BaseTest {

    private Config voxeo;

    @InjectMocks
    private ConfigContractBuilder configContractBuilder = new ConfigContractBuilder();

    @Before
    public void setUp() {
        voxeo = ConfigHelper.createConfigs().get(0);

    }

    @Test
    public void shouldBuildConfigContractFromConfig() throws IOException {
        // Given

        // When
        ConfigContract voxeoContract = configContractBuilder.createFrom(voxeo);

        // Then
        assertThat(voxeoContract.getName(), equalTo(voxeo.getName()));
        assertThat(voxeoContract.getOutgoingCallMethod(), equalTo(voxeo.getOutgoingCallMethod()));
        assertThat(voxeoContract.getOutgoingCallUriTemplate(), equalTo(voxeo.getOutgoingCallUriTemplate()));
        assertThat(voxeoContract.getOutboundCallLimit(), equalTo(voxeo.getOutboundCallLimit()));
        assertThat(voxeoContract.getOutboundCallRetryAttempts(), equalTo(voxeo.getOutboundCallRetryAttempts()));
        assertThat(voxeoContract.getOutboundCallRetrySeconds(), equalTo(voxeo.getOutboundCallRetrySeconds()));
        assertThat(voxeoContract.isCallAllowed(), equalTo(voxeo.getCallAllowed()));

        assertThat(voxeoContract.getTestUsersMap().size(), equalTo(voxeo.getTestUsersMap().size()));
        assertThat(voxeoContract.getTestUsersMap(), equalTo(voxeo.getTestUsersMap()));

        assertThat(voxeoContract.getServicesMap().size(), equalTo(voxeo.getServicesMap().size()));
        assertThat(voxeoContract.getServicesMap(), equalTo(voxeo.getServicesMap()));

    }
}

