package org.openmrs.module.callflows.web.controller;

import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.builder.ConfigBuilder;
import org.openmrs.module.callflows.api.builder.ConfigContractBuilder;
import org.openmrs.module.callflows.api.builder.RendererBuilder;
import org.openmrs.module.callflows.api.builder.RendererContractBuilder;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContracts;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.Settings;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.GenericHelper;
import org.openmrs.module.callflows.api.helper.RendererHelper;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.util.ValidationComponent;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Config Controller Web Test
 *
 * @author bramak09
 */
@RunWith(PowerMockRunner.class)
public class SettingsControllerTest extends BaseTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CallFlowSettingsController settingsController = new CallFlowSettingsController();

    @Mock
    private ConfigService configService;

    @Mock
    private ConfigContractBuilder configContractBuilder;

    @Mock
    private ConfigBuilder configBuilder;

    @Mock
    private RendererContractBuilder rendererContractBuilder;

    @Mock
    private RendererBuilder rendererBuilder;

    @Mock
    private ValidationComponent validationComponent;

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
        mockMvc = MockMvcBuilders.standaloneSetup(settingsController)
                .setMessageConverters(new MappingJacksonHttpMessageConverter())
                .build();

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
        given(configService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(get("/callflows/configs").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(gsonFormatOfJson(configContracts)));

        // Then no incoming, so no builders
        verify(configBuilder, never()).createFrom(any(ConfigContract.class));
        // And one service call
        verify(configService, times(1)).allConfigs();
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
        given(configService.allConfigs()).willReturn(configs);
        doNothing().when(validationComponent).validate(configContracts);

        // When and Then
        mockMvc.perform(post("/callflows/configs").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(configContracts)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(gsonFormatOfJson(configContracts)));

        // Then two builders for request
        verify(configBuilder, times(3)).createFrom(any(ConfigContract.class));
        // And must update once with correct data
        verify(configService, times(1)).updateConfigs(configs);
        // and must retrieve again to return back
        verify(configService, times(1)).allConfigs();
        // And must call response builder twice
        verify(configContractBuilder, times(3)).createFrom(any(Config.class));
    }

    @Test
    public void shouldReturnValidationErrorIfConfigsHaveNotUniqueNames() throws Exception {
        // Given
        List<ConfigContract> configContracts = buildNotUniqueConfigContacts();
        String path = "configContracts.name";
        String message = "Names of configs are not unique: voxeo";
        Map<String, String> violations = new HashMap<>();
        violations.put(path, message);

        doThrow(new ValidationException(violations))
            .when(validationComponent).validate(Mockito.any(ConfigContracts.class));

        // When and Then
        mockMvc.perform(post("/callflows/configs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBytes(configContracts)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.constraintViolations['" + path + "']")
                    .value(message));
    }

    @Test
    public void shouldReturnAllRenderersAsJSON() throws Exception {
        // Given
        given(rendererContractBuilder.createFrom(vxml)).willReturn(vxmlContract);
        given(rendererContractBuilder.createFrom(txt)).willReturn(txtContract);
        // And
        given(configService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(get("/callflows/renderers").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(gsonFormatOfJson(rendererContracts)));

        // Then no incoming, so no builders
        verify(configBuilder, never()).createFrom(any(ConfigContract.class));
        // And one service call
        verify(configService, times(1)).allRenderers();
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
        given(configService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(post("/callflows/renderers").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(rendererContracts)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(gsonFormatOfJson(rendererContracts)));

        // Then two builders for request
        verify(rendererBuilder, times(2)).createFrom(any(RendererContract.class));
        // And must update once with correct data
        verify(configService, times(1)).updateRenderers(renderers);
        // and must retrieve again to return back
        verify(configService, times(1)).allRenderers();
        // And must call response builder twice
        verify(rendererContractBuilder, times(2)).createFrom(any(Renderer.class));
    }

    private String gsonFormatOfJson(Object obj) {
        return new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create()
            .toJson(obj);
    }

    private List<ConfigContract> buildNotUniqueConfigContacts() {
        List<ConfigContract> configContracts = ConfigHelper.createConfigContracts();
        configContracts.add(configContracts.get(0));
        return configContracts;
    }
}
