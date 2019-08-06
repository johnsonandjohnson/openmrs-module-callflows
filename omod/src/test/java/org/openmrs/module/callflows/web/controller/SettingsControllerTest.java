package org.openmrs.module.callflows.web.controller;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.builder.ConfigBuilder;
import com.janssen.connectforlife.callflows.builder.ConfigContractBuilder;
import com.janssen.connectforlife.callflows.builder.RendererBuilder;
import com.janssen.connectforlife.callflows.builder.RendererContractBuilder;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.domain.Settings;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.GenericHelper;
import com.janssen.connectforlife.callflows.helper.RendererHelper;
import com.janssen.connectforlife.callflows.service.SettingsService;

import org.motechproject.mds.util.SecurityUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Config Controller Web Test
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtil.class)
public class SettingsControllerTest extends BaseTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SettingsController settingsController = new SettingsController();

    @Mock
    private SettingsService settingsService;

    @Mock
    private ConfigContractBuilder configContractBuilder;

    @Mock
    private ConfigBuilder configBuilder;

    @Mock
    private RendererContractBuilder rendererContractBuilder;

    @Mock
    private RendererBuilder rendererBuilder;

    private Config voxeo;

    private Config yo;

    private Config imiMobile;

    private Renderer vxml;

    private Renderer txt;

    private ConfigContract voxeoContract;

    private ConfigContract yoContract;

    private ConfigContract imiMobileContract;

    private List<Config> configs;

    private List<ConfigContract> configContracts;

    private RendererContract vxmlContract;

    private RendererContract txtContract;

    private List<Renderer> renderers;

    private List<RendererContract> rendererContracts;

    private Settings settings;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(settingsController).build();

        settings = GenericHelper.createSettings();
        configs = settings.getConfigs();
        renderers = settings.getRenderers();
        configContracts = ConfigHelper.createConfigContracts();
        rendererContracts = RendererHelper.createRendererContracts();

        voxeo = configs.get(0);
        yo = configs.get(1);
        imiMobile = configs.get(2);

        vxml = renderers.get(0);
        txt = renderers.get(1);

        voxeoContract = configContracts.get(0);
        yoContract = configContracts.get(1);
        imiMobileContract = configContracts.get(2);

        vxmlContract = rendererContracts.get(0);
        txtContract = rendererContracts.get(1);
    }


    @Test
    public void shouldReturnAllConfigsAsJSON() throws Exception {
        // Given
        given(configContractBuilder.createFrom(voxeo)).willReturn(voxeoContract);
        given(configContractBuilder.createFrom(yo)).willReturn(yoContract);
        given(configContractBuilder.createFrom(imiMobile)).willReturn(imiMobileContract);
        // And
        given(settingsService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(get("/configs").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(configContracts)));

        // Then no incoming, so no builders
        verify(configBuilder, never()).createFrom(any(ConfigContract.class));
        // And one service call
        verify(settingsService, times(1)).allConfigs();
        // And two response builders
        verify(configContractBuilder, times(3)).createFrom(any(Config.class));
    }

    @Test
    public void shouldUpdateAllConfigsAndReturnBackAllAsJson() throws Exception {
        // Given
        given(configBuilder.createFrom(voxeoContract)).willReturn(voxeo);
        given(configBuilder.createFrom(yoContract)).willReturn(yo);
        given(configBuilder.createFrom(imiMobileContract)).willReturn(imiMobile);
        // And
        given(configContractBuilder.createFrom(voxeo)).willReturn(voxeoContract);
        given(configContractBuilder.createFrom(yo)).willReturn(yoContract);
        given(configContractBuilder.createFrom(imiMobile)).willReturn(imiMobileContract);
        // And
        given(settingsService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(post("/configs").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(configContracts)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(configContracts)));

        // Then two builders for request
        verify(configBuilder, times(3)).createFrom(any(ConfigContract.class));
        // And must update once with correct data
        verify(settingsService, times(1)).updateConfigs(configs);
        // and must retrieve again to return back
        verify(settingsService, times(1)).allConfigs();
        // And must call response builder twice
        verify(configContractBuilder, times(3)).createFrom(any(Config.class));
    }


    @Test
    public void shouldReturnAllRenderersAsJSON() throws Exception {
        // Given
        given(rendererContractBuilder.createFrom(vxml)).willReturn(vxmlContract);
        given(rendererContractBuilder.createFrom(txt)).willReturn(txtContract);
        // And
        given(settingsService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(get("/renderers").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(rendererContracts)));

        // Then no incoming, so no builders
        verify(configBuilder, never()).createFrom(any(ConfigContract.class));
        // And one service call
        verify(settingsService, times(1)).allRenderers();
        // And two response builders
        verify(rendererContractBuilder, times(2)).createFrom(any(Renderer.class));
    }

    @Test
    public void shouldUpdateAllRenderersAndReturnBackAllAsJson() throws Exception {
        // Given
        given(rendererBuilder.createFrom(vxmlContract)).willReturn(vxml);
        given(rendererBuilder.createFrom(txtContract)).willReturn(txt);
        // And
        given(rendererContractBuilder.createFrom(vxml)).willReturn(vxmlContract);
        given(rendererContractBuilder.createFrom(txt)).willReturn(txtContract);
        // And
        given(settingsService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(post("/renderers").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(rendererContracts)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(rendererContracts)));

        // Then two builders for request
        verify(rendererBuilder, times(2)).createFrom(any(RendererContract.class));
        // And must update once with correct data
        verify(settingsService, times(1)).updateRenderers(renderers);
        // and must retrieve again to return back
        verify(settingsService, times(1)).allRenderers();
        // And must call response builder twice
        verify(rendererContractBuilder, times(2)).createFrom(any(Renderer.class));
    }
}
