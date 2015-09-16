package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.service.impl.ConfigServiceImpl;

import org.motechproject.server.config.SettingsFacade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Config Service Tests
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest extends BaseTest {

    private List<Config> configs;

    @Mock
    private SettingsFacade settingsFacade;

    @InjectMocks
    private ConfigService configService = new ConfigServiceImpl();

    @Before
    public void setUp() throws IOException {

        configs = ConfigHelper.createConfigs();

        String json = json(configs);
        InputStream is = new ByteArrayInputStream(json.getBytes());

        //Given
        given(settingsFacade.getRawConfig(ConfigHelper.CONFIG_FILE_NAME)).willReturn(is);

        ((ConfigServiceImpl) configService).initialize();
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

    @Test
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidConfig() {
        expectException(IllegalArgumentException.class);
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
        String json = json(configs);
        ByteArrayResource resource = new ByteArrayResource(json.getBytes());
        InputStream is = new ByteArrayInputStream(json.getBytes());
        given(settingsFacade.getRawConfig(ConfigHelper.CONFIG_FILE_NAME)).willReturn(is);

        // When
        configService.updateConfigs(configs);

        // Then
        verify(settingsFacade, times(1)).saveRawConfig(ConfigHelper.CONFIG_FILE_NAME, resource);
        List<Config> allConfigs = configService.allConfigs();
        assertThat(allConfigs.size(), equalTo(configs.size()));
        // The first one must be updated
        assertThat(allConfigs.get(0).getName(), equalTo(Constants.CONFIG_VOXEO + Constants.UPDATED));
        // The second one is not updated
        assertThat(allConfigs.get(1).getName(), equalTo(Constants.CONFIG_YO));
    }

}
