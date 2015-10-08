package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Config Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest extends BaseTest {

    private ConfigContract voxeoContract;

    @InjectMocks
    private ConfigBuilder configBuilder = new ConfigBuilder();

    @Before
    public void setUp() {
        voxeoContract = ConfigHelper.createConfigContracts().get(0);

    }

    @Test
    public void shouldBuildConfigFromContract() throws IOException {
        // Given

        // When
        Config voxeo = configBuilder.createFrom(voxeoContract);

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

