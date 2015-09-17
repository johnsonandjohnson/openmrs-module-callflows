package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.builder.ConfigBuilder;
import com.janssen.connectforlife.callflows.builder.ConfigContractBuilder;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.service.ConfigService;

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
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigBuilder configBuilder;

    @Autowired
    private ConfigContractBuilder configContractBuilder;

    /**
     * API to get all IVR based configurations defined in system
     *
     * @return a list of configurations
     */
    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public List<ConfigContract> getConfigs() {
        return buildContract(configService.allConfigs());
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
        configService.updateConfigs(configs);
        return buildContract(configService.allConfigs());
    }

    private List<ConfigContract> buildContract(List<Config> configs) {
        List<ConfigContract> configContracts = new ArrayList<>();
        for (Config config : configs) {
            configContracts.add(configContractBuilder.createFrom(config));
        }
        return configContracts;
    }
}
