package org.openmrs.module.callflows.web.controller;

import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
        given(configService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(get("/callflows/configs").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(gsonFormatOfJson(configContracts)));

        // And one service call
        verify(configService, times(1)).allConfigs();
        // And two response builders
    }

    @Test
    public void shouldUpdateAllConfigsAndReturnBackAllAsJson() throws Exception {
        //Given
        given(configService.allConfigs()).willReturn(configs);
        doNothing().when(validationComponent).validate(configContracts);

        // When and Then
        mockMvc.perform(post("/callflows/configs").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(configContracts)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(gsonFormatOfJson(configContracts)));

        // And must update once with correct data
        verify(configService, times(1)).updateConfigs(configs);
        // and must retrieve again to return back
        verify(configService, times(1)).allConfigs();
    }

    @Test
    public void shouldReturnValidationErrorIfConfigsHaveNotUniqueNames() throws Exception {
        // Given
        String path = "configContracts.name";
        String message = "Names of configs are not unique: voxeo";
        Map<String, String> violations = new HashMap<>();
        violations.put(path, message);

        doThrow(new ValidationException(violations))
                .when(validationComponent).validate(Mockito.any(ConfigContracts.class));

        // When and Then
        mockMvc.perform(post("/callflows/configs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBytes(buildNotUniqueConfigContacts())))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.constraintViolations['" + path + "']")
                        .value(message));
    }

    @Test
    public void shouldReturnAllRenderersAsJSON() throws Exception {
        // Given
        given(configService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(get("/callflows/renderers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(gsonFormatOfJson(rendererContracts)));

        // And one service call
        verify(configService, times(1)).allRenderers();
    }

    @Test
    public void shouldUpdateAllRenderersAndReturnBackAllAsJson() throws Exception {
        // Given
        given(configService.allRenderers()).willReturn(renderers);

        // When and Then
        mockMvc.perform(post("/callflows/renderers").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(rendererContracts)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(gsonFormatOfJson(rendererContracts)));

        // and must retrieve again to return back
        verify(configService, times(1)).allRenderers();
    }

    private String gsonFormatOfJson(Object obj) {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .serializeNulls()
                .create()
                .toJson(obj);
    }

    private List<ConfigContract> buildNotUniqueConfigContacts() {
        List<ConfigContract> result = ConfigHelper.createConfigContracts();
        result.add(result.get(0));
        return result;
    }
}
