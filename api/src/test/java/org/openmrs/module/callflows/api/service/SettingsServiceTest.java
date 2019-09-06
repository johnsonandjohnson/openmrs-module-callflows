package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.BaseTest;
import org.openmrs.module.callflows.api.Constants;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.Settings;
import org.openmrs.module.callflows.api.helper.GenericHelper;
import org.openmrs.module.callflows.api.service.impl.SettingsServiceImpl;
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
public class SettingsServiceTest extends BaseTest {

    private List<Config> configs;

    private List<Renderer> renderers;

    private Settings settings;

    @Mock
    private SettingsManagerService settingsManagerService;

    @InjectMocks
    private SettingsService settingsService = new SettingsServiceImpl();

    @Before
    public void setUp() throws IOException {

        settings = GenericHelper.createSettings();
        configs = settings.getConfigs();
        renderers = settings.getRenderers();

        String json = json(settings);
        InputStream is = new ByteArrayInputStream(json.getBytes());

        //Given
        given(settingsManagerService.getRawConfig(GenericHelper.SETTINGS_FILE_NAME)).willReturn(is);

        ((SettingsServiceImpl) settingsService).initialize();
    }

    @Test
    public void shouldGetConfigForValidName() {
        // When
        Config voxeo = settingsService.getConfig(Constants.CONFIG_VOXEO);
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
        Config voxeo = settingsService.getConfig(Constants.INVALID);
    }

    @Test
    public void shouldGetAllConfigs() {
        // When
        List<Config> allConfigs = settingsService.allConfigs();
        // Then
        assertNotNull(allConfigs);
        assertThat(allConfigs.size(), equalTo(3));
        Config voxeo = allConfigs.get(0);
        Config yo = allConfigs.get(1);

        assertThat(voxeo.getName(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_VOXEO_METHOD));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_VOXEO_OUT_TEMPLATE));
        assertThat(voxeo.getOutboundCallLimit(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT));
        assertThat(voxeo.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(voxeo.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(voxeo.getCallAllowed(), equalTo(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL));

        assertThat(yo.getName(), equalTo(Constants.CONFIG_YO));
        assertThat(yo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_YO_METHOD));
        assertThat(yo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_YO_OUT_TEMPLATE));
        assertThat(yo.getOutboundCallLimit(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_LIMIT));
        assertThat(yo.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(yo.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(yo.getCallAllowed(), equalTo(Constants.CONFIG_YO_CAN_PLACE_OUTBOUND_CALL));
    }

    @Test
    public void shouldReturnTrueIfCheckedForExistenceOfValidConfig() {
        // When
        boolean exists = settingsService.hasConfig(Constants.CONFIG_VOXEO);
        // Then
        assertThat(exists, equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfCheckedForExistenceOfInvalidConfig() {
        // When
        boolean exists = settingsService.hasConfig(Constants.INVALID);
        // Then
        assertThat(exists, equalTo(false));
    }

    @Test
    public void shouldUpdateConfigsSuccessfully() throws IOException {
        // Given some changes to the first configuration
        configs.get(0).setName(Constants.CONFIG_VOXEO + Constants.UPDATED);
        String json = json(settings);
        ByteArrayResource resource = new ByteArrayResource(json.getBytes());
        InputStream is = new ByteArrayInputStream(json.getBytes());
        given(settingsManagerService.getRawConfig(GenericHelper.SETTINGS_FILE_NAME)).willReturn(is);

        // When
        settingsService.updateConfigs(configs);

        // Then
        verify(settingsManagerService, times(1)).saveRawConfig(GenericHelper.SETTINGS_FILE_NAME, resource);
        List<Config> allConfigs = settingsService.allConfigs();
        assertThat(allConfigs.size(), equalTo(configs.size()));
        // The first one must be updated
        assertThat(allConfigs.get(0).getName(), equalTo(Constants.CONFIG_VOXEO + Constants.UPDATED));
        // The second one is not updated
        assertThat(allConfigs.get(1).getName(), equalTo(Constants.CONFIG_YO));
    }

    /* Renderers */
    @Test
    public void shouldGetRendererForValidName() {
        // When
        Renderer vxml = settingsService.getRenderer(Constants.CONFIG_RENDERER_VXML);
        // Then
        assertNotNull(vxml);
        assertThat(vxml.getName(), equalTo(Constants.CONFIG_RENDERER_VXML));
        assertThat(vxml.getMimeType(), equalTo(Constants.CONFIG_RENDERER_VXML_MIME));
        assertThat(vxml.getTemplate(), equalTo(Constants.CONFIG_RENDERER_VXML_TPL));
    }

    @Test
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidRenderer() {
        expectException(IllegalArgumentException.class);
        // When, Then
        settingsService.getRenderer(Constants.INVALID);
    }

    @Test
    public void shouldGetAllRenderers() {
        // When
        List<Renderer> allRenderers = settingsService.allRenderers();
        // Then
        assertNotNull(allRenderers);
        assertThat(allRenderers.size(), equalTo(2));
        Renderer vxml = allRenderers.get(0);
        Renderer txt = allRenderers.get(1);

        assertThat(vxml.getName(), equalTo(Constants.CONFIG_RENDERER_VXML));
        assertThat(vxml.getMimeType(), equalTo(Constants.CONFIG_RENDERER_VXML_MIME));
        assertThat(vxml.getTemplate(), equalTo(Constants.CONFIG_RENDERER_VXML_TPL));

        assertThat(txt.getName(), equalTo(Constants.CONFIG_RENDERER_TXT));
        assertThat(txt.getMimeType(), equalTo(Constants.CONFIG_RENDERER_TXT_MIME));
        assertThat(txt.getTemplate(), equalTo(Constants.CONFIG_RENDERER_TXT_TPL));
    }

    @Test
    public void shouldReturnTrueIfCheckedForExistenceOfValidRenderer() {
        // When
        boolean exists = settingsService.hasRenderer(Constants.CONFIG_RENDERER_VXML);
        // Then
        assertThat(exists, equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfCheckedForExistenceOfInvalidRenderer() {
        // When
        boolean exists = settingsService.hasRenderer(Constants.INVALID);
        // Then
        assertThat(exists, equalTo(false));
    }

    @Test
    public void shouldUpdateRenderersSuccessfully() throws IOException {
        // Given some changes to the first configuration
        renderers.get(0).setName(Constants.CONFIG_RENDERER_VXML + Constants.UPDATED);
        String json = json(settings);
        ByteArrayResource resource = new ByteArrayResource(json.getBytes());
        InputStream is = new ByteArrayInputStream(json.getBytes());
        given(settingsManagerService.getRawConfig(GenericHelper.SETTINGS_FILE_NAME)).willReturn(is);

        // When
        settingsService.updateRenderers(renderers);

        // Then
        verify(settingsManagerService, times(1)).saveRawConfig(GenericHelper.SETTINGS_FILE_NAME, resource);
        List<Renderer> allRenderers = settingsService.allRenderers();
        assertThat(allRenderers.size(), equalTo(renderers.size()));
        // The first one must be updated
        assertThat(allRenderers.get(0).getName(), equalTo(Constants.CONFIG_RENDERER_VXML + Constants.UPDATED));
        // The second one is not updated
        assertThat(allRenderers.get(1).getName(), equalTo(Constants.CONFIG_RENDERER_TXT));
    }

}
