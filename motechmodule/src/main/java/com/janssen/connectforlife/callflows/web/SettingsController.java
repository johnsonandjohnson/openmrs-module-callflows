package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.builder.ConfigBuilder;
import com.janssen.connectforlife.callflows.builder.ConfigContractBuilder;
import com.janssen.connectforlife.callflows.builder.RendererBuilder;
import com.janssen.connectforlife.callflows.builder.RendererContractBuilder;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.service.SettingsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage configurations settings, adapted and enhanced from MoTeCH's IVR module
 *
 * @author bramak09
 */
@Controller
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private ConfigBuilder configBuilder;

    @Autowired
    private RendererBuilder rendererBuilder;

    @Autowired
    private ConfigContractBuilder configContractBuilder;

    @Autowired
    private RendererContractBuilder rendererContractBuilder;

    /**
     * API to get all IVR based configurations defined in system
     *
     * @return a list of configurations
     */
    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfigContract> getConfigs() {
        return buildConfigContract(settingsService.allConfigs());
    }

    /**
     * API to update all IVR configurations in the system in one go
     *
     * @param configContracts a list of configurations to update
     * @return the updated configurations
     */
    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ConfigContract> updateConfigs(@RequestBody ConfigContract[] configContracts) {
        List<Config> configs = new ArrayList<>();
        for (ConfigContract configContract : configContracts) {
            configs.add(configBuilder.createFrom(configContract));
        }
        settingsService.updateConfigs(configs);
        return buildConfigContract(settingsService.allConfigs());
    }

    /**
     * API to get all client side Renderers defined in the system
     *
     * @return a list of configurations
     */
    @RequestMapping(value = "/renderers", method = RequestMethod.GET)
    @ResponseBody
    public List<RendererContract> getRenderers() {
        return buildRendererContract(settingsService.allRenderers());
    }

    /**
     * API to update all client side Renderers defined in the system
     *
     * @param rendererContracts a list of renderers to update
     * @return the updated renderers
     */
    @RequestMapping(value = "/renderers", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RendererContract> updateRenderers(@RequestBody RendererContract[] rendererContracts) {
        List<Renderer> renderers = new ArrayList<>();
        for (RendererContract rendererContract : rendererContracts) {
            renderers.add(rendererBuilder.createFrom(rendererContract));
        }
        settingsService.updateRenderers(renderers);
        return buildRendererContract(settingsService.allRenderers());
    }

    private List<ConfigContract> buildConfigContract(List<Config> configs) {
        List<ConfigContract> configContracts = new ArrayList<>();
        for (Config config : configs) {
            configContracts.add(configContractBuilder.createFrom(config));
        }
        return configContracts;
    }

    private List<RendererContract> buildRendererContract(List<Renderer> renderers) {
        List<RendererContract> rendererContracts = new ArrayList<>();
        for (Renderer renderer : renderers) {
            rendererContracts.add(rendererContractBuilder.createFrom(renderer));
        }
        return rendererContracts;
    }
}
