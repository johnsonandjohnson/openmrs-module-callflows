package com.janssen.connectforlife.callflows.service.it;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.service.ConfigService;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Configuration Service Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ConfigServiceBundleIT extends BasePaxIT {

    @Inject
    private ConfigService configService;

    @Inject
    private SettingsFacade settingsFacade;

    private List<Config> configs;

    @Before
    public void setUp() throws IOException {

        // Save a bunch of configs in the database
        configs = ConfigHelper.createConfigs();
        configService.updateConfigs(configs);
    }

    @After
    public void tearDown() {
        // reset by emptying out the configuration
        configService.updateConfigs(new ArrayList<Config>());
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(configService);
    }

    @Test
    public void shouldGetConfigForValidName() {
        // When
        Config voxeo = configService.getConfig(Constants.CONFIG_VOXEO);
        // Then
        assertNotNull(voxeo);
        assertThat(voxeo.getName(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_VOXEO_METHOD));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_VOXEO_OUT_TEMPLATE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidConfig() {
        // When, Then
        Config voxeo = configService.getConfig(Constants.CONFIG_INVALID);
    }

    @Test
    public void shouldGetAllConfigs() {
        // When
        List<Config> allConfigs = configService.allConfigs();
        // Then
        assertNotNull(allConfigs);
        assertThat(allConfigs.size(), equalTo(2));
        Config voxeo = allConfigs.get(0);
        Config yo = allConfigs.get(1);

        assertThat(voxeo.getName(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_VOXEO_METHOD));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_VOXEO_OUT_TEMPLATE));

        assertThat(yo.getName(), equalTo(Constants.CONFIG_YO));
        assertThat(yo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_YO_METHOD));
        assertThat(yo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_YO_OUT_TEMPLATE));
    }

    @Test
    public void shouldReturnTrueIfCheckedForExistenceOfValidConfig() {
        // When
        boolean exists = configService.hasConfig(Constants.CONFIG_VOXEO);
        // Then
        assertThat(exists, equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfCheckedForExistenceOfInvalidConfig() {
        // When
        boolean exists = configService.hasConfig(Constants.CONFIG_INVALID);
        // Then
        assertThat(exists, equalTo(false));
    }

    @Test
    public void shouldUpdateConfigsSuccessfully() throws IOException {
        // Given some changes to the first configuration
        configs.get(0).setName(Constants.CONFIG_VOXEO + Constants.UPDATED);

        // When
        configService.updateConfigs(configs);

        // Then
        List<Config> allConfigs = configService.allConfigs();
        assertThat(allConfigs.size(), equalTo(configs.size()));
        // The first one must be updated
        assertThat(allConfigs.get(0).getName(), equalTo(Constants.CONFIG_VOXEO + Constants.UPDATED));
        // The second one is not updated
        assertThat(allConfigs.get(1).getName(), equalTo(Constants.CONFIG_YO));
    }

    private String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

}
