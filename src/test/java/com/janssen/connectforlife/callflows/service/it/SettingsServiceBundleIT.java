package com.janssen.connectforlife.callflows.service.it;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.RendererHelper;
import com.janssen.connectforlife.callflows.service.SettingsService;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

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
public class SettingsServiceBundleIT extends BasePaxIT {

    @Inject
    private SettingsService settingsService;

    @Inject
    private SettingsFacade settingsFacade;

    private List<Config> configs;

    private List<Renderer> renderers;

    @Before
    public void setUp() throws IOException {

        // Save a bunch of configs in the database
        configs = ConfigHelper.createConfigs();
        settingsService.updateConfigs(configs);

        // and some renderers
        renderers = RendererHelper.createRenderers();
        settingsService.updateRenderers(renderers);
    }

    @After
    public void tearDown() {
        // reset by emptying out the configuration
        settingsService.updateConfigs(new ArrayList<Config>());
        settingsService.updateRenderers(new ArrayList<Renderer>());
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(settingsService);
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidConfig() {
        // When, Then
        Config voxeo = settingsService.getConfig(Constants.INVALID);
    }

    @Test
    public void shouldGetAllConfigs() {
        // When
        List<Config> allConfigs = settingsService.allConfigs();
        // Then
        assertNotNull(allConfigs);
        assertThat(allConfigs.size(), equalTo(2));
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
        assertThat(voxeo.getOutboundCallLimit(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT));
        assertThat(voxeo.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(voxeo.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(voxeo.getCallAllowed(), equalTo(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL));
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

        // When
        settingsService.updateConfigs(configs);

        // Then
        List<Config> allConfigs = settingsService.allConfigs();
        assertThat(allConfigs.size(), equalTo(configs.size()));
        // The first one must be updated
        assertThat(allConfigs.get(0).getName(), equalTo(Constants.CONFIG_VOXEO + Constants.UPDATED));
        // The second one is not updated
        assertThat(allConfigs.get(1).getName(), equalTo(Constants.CONFIG_YO));
    }

    /* Renderers */

    @Test
    public void shouldGetRendererValidName() {
        // When
        Renderer vxml = settingsService.getRenderer(Constants.CONFIG_RENDERER_VXML);
        // Then
        assertNotNull(vxml);
        assertThat(vxml.getName(), equalTo(Constants.CONFIG_RENDERER_VXML));
        assertThat(vxml.getTemplate(), equalTo(Constants.CONFIG_RENDERER_VXML_TPL));
        assertThat(vxml.getMimeType(), equalTo(Constants.CONFIG_RENDERER_VXML_MIME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidRenderer() {
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

        // When
        settingsService.updateRenderers(renderers);

        // Then
        List<Renderer> allRenderers = settingsService.allRenderers();
        assertThat(allRenderers.size(), equalTo(renderers.size()));
        // The first one must be updated
        assertThat(allRenderers.get(0).getName(), equalTo(Constants.CONFIG_RENDERER_VXML + Constants.UPDATED));
        // The second one is not updated
        assertThat(allRenderers.get(1).getName(), equalTo(Constants.CONFIG_RENDERER_TXT));
    }


}
