package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.builder.ConfigBuilder;
import com.janssen.connectforlife.callflows.builder.ConfigContractBuilder;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.helper.ConfigContractHelper;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.service.ConfigService;

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
public class ConfigControllerTest extends BaseTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ConfigController configController = new ConfigController();

    @Mock
    private ConfigService configService;

    @Mock
    private ConfigContractBuilder configContractBuilder;

    @Mock
    private ConfigBuilder configBuilder;

    private Config voxeo;

    private Config yo;

    private ConfigContract voxeoContract;

    private ConfigContract yoContract;

    private List<Config> configs;

    private List<ConfigContract> configContracts;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configController).build();

        configs = ConfigHelper.createConfigs();
        configContracts = ConfigContractHelper.createConfigContracts();

        voxeo = configs.get(0);
        yo = configs.get(1);

        voxeoContract = configContracts.get(0);
        yoContract = configContracts.get(1);
    }


    @Test
    public void shouldReturnAllConfigsAsJSON() throws Exception {
        // Given
        given(configContractBuilder.createFrom(voxeo)).willReturn(voxeoContract);
        given(configContractBuilder.createFrom(yo)).willReturn(yoContract);
        // And
        given(configService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(get("/configs").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(configContracts)));

        // Then no incoming, so no builders
        verify(configBuilder, never()).createFrom(any(ConfigContract.class));
        // And one service call
        verify(configService, times(1)).allConfigs();
        // And two response builders
        verify(configContractBuilder, times(2)).createFrom(any(Config.class));
    }

    @Test
    public void shouldUpdateAllConfigsAndReturnBackAllAsJson() throws Exception {
        // Given
        given(configBuilder.createFrom(voxeoContract)).willReturn(voxeo);
        given(configBuilder.createFrom(yoContract)).willReturn(yo);
        // And
        given(configContractBuilder.createFrom(voxeo)).willReturn(voxeoContract);
        given(configContractBuilder.createFrom(yo)).willReturn(yoContract);
        // And
        given(configService.allConfigs()).willReturn(configs);

        // When and Then
        mockMvc.perform(post("/configs").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(configContracts)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(configContracts)));

        // Then two builders for request
        verify(configBuilder, times(2)).createFrom(any(ConfigContract.class));
        // And must update once with correct data
        verify(configService, times(1)).updateConfigs(configs);
        // and must retrieve again to return back
        verify(configService, times(1)).allConfigs();
        // And must call response builder twice
        verify(configContractBuilder, times(2)).createFrom(any(Config.class));
    }
}
