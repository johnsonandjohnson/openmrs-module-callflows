package org.openmrs.module.callflows.api.builder;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.helper.ConfigHelper;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Config Builder Test
 *
 * @author bramak09
 */
public class ConfigBuilderTest extends BaseTest {

    private ConfigContract voxeoContract;

    @Before
    public void setUp() {
        voxeoContract = ConfigHelper.createConfigContracts().get(0);
    }

    @Test
    public void shouldBuildConfigFromContract() throws IOException {
        // Given

        // When
        Config voxeo = ConfigBuilder.createFrom(voxeoContract);

        // Then

        assertThat(voxeo.getName(), equalTo(voxeoContract.getName()));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(voxeoContract.getOutgoingCallMethod()));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(voxeoContract.getOutgoingCallUriTemplate()));
        assertThat(voxeo.getOutboundCallLimit(), equalTo(voxeoContract.getOutboundCallLimit()));
        assertThat(voxeo.getOutboundCallRetryAttempts(), equalTo(voxeoContract.getOutboundCallRetryAttempts()));
        assertThat(voxeo.getOutboundCallRetrySeconds(), equalTo(voxeoContract.getOutboundCallRetrySeconds()));
        assertThat(voxeo.getCallAllowed(), equalTo(voxeoContract.isCallAllowed()));

        assertThat(voxeo.getTestUsersMap().size(), equalTo(voxeoContract.getTestUsersMap().size()));
        assertThat(voxeo.getTestUsersMap(), equalTo(voxeoContract.getTestUsersMap()));

        assertThat(voxeo.getServicesMap().size(), equalTo(voxeoContract.getServicesMap().size()));
        assertThat(voxeo.getServicesMap(), equalTo(voxeoContract.getServicesMap()));

    }

}

