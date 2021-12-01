package org.openmrs.module.callflows.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import org.openmrs.module.callflows.api.builder.ConfigBuilder;
import org.openmrs.module.callflows.api.builder.ConfigContractBuilder;
import org.openmrs.module.callflows.api.builder.RendererBuilder;
import org.openmrs.module.callflows.api.builder.RendererContractBuilder;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.ConfigContractList;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.util.ValidationComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage configurations settings, adapted and enhanced from MoTeCH's IVR module
 *
 * @author bramak09
 */
@Api(
    value = "Manage configurations settings",
    tags = {
      "REST API to manage configurations settings, adapted and enhanced from MoTeCH's IVR module"
    })
@Controller
@RequestMapping("/callflows")
public class CallFlowSettingsController extends RestController {

  @Autowired
  @Qualifier("callflows.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("callflows.validationComponent")
  private ValidationComponent validationComponent;

  /**
   * API to get all IVR based configurations defined in system
   *
   * @return a list of configurations
   */
  @ApiOperation(
      value = "Get all IVR based configurations defined in system",
      notes = "Get all IVR based configurations defined in system")
  @ApiResponses(
      value = {
        @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successfully returns all configurations"),
              @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Successfully returns all configurations")
      })
  @RequestMapping(value = "/configs", method = RequestMethod.GET)
  @ResponseBody
  public List<ConfigContract> getConfigs() {
    return buildConfigContract(configService.allConfigs());
  }

  /**
   * API to update all IVR configurations in the system in one go
   *
   * @param configContracts a list of configurations to update
   * @return the updated configurations
   */
  @ApiOperation(
      value = "Update all IVR configurations in the system in one go",
      notes = "Update all IVR configurations in the system in one go")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully updated all configurations"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_BAD_REQUEST,
            message = "Unsuccessful update due to validation error")
      })
  @RequestMapping(value = "/configs", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<ConfigContract> updateConfigs(
      @ApiParam(name = "configContracts", value = "List of configurations to update") @RequestBody
          ConfigContractList configContracts) {
    validationComponent.validate(configContracts);

    List<Config> configs = new ArrayList<>();
    for (ConfigContract configContract : configContracts.getConfigContracts()) {
      configs.add(ConfigBuilder.createFrom(configContract));
    }
    configService.updateConfigs(configs);
    return buildConfigContract(configService.allConfigs());
  }

  /**
   * API to get all client side Renderers defined in the system
   *
   * @return a list of configurations
   */
  @ApiOperation(
      value = "Get all client side Renderers defined in the system",
      notes = "Get all client side Renderers defined in the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully retrieved all client side Renderers defined in the system")
      })
  @RequestMapping(value = "/renderers", method = RequestMethod.GET)
  @ResponseBody
  public List<RendererContract> getRenderers() {
    return buildRendererContract(configService.allRenderers());
  }

  /**
   * API to update all client side Renderers defined in the system
   *
   * @param rendererContracts a list of renderers to update
   * @return the updated renderers
   */
  @ApiOperation(
      value = "Update all client side Renderers defined in the system",
      notes = "Update all client side Renderers defined in the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully updated client side Renderers defined in the system")
      })
  @RequestMapping(value = "/renderers", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<RendererContract> updateRenderers(
      @ApiParam(name = "rendererContracts", value = "List of renderers to update") @RequestBody
          RendererContract[] rendererContracts) {
    List<Renderer> renderers = new ArrayList<>();
    for (RendererContract rendererContract : rendererContracts) {
      renderers.add(RendererBuilder.createFrom(rendererContract));
    }
    configService.updateRenderers(renderers);
    return buildRendererContract(configService.allRenderers());
  }

  private List<ConfigContract> buildConfigContract(List<Config> configs) {
    List<ConfigContract> configContracts = new ArrayList<>();
    for (Config config : configs) {
      configContracts.add(ConfigContractBuilder.createFrom(config));
    }
    return configContracts;
  }

  private List<RendererContract> buildRendererContract(List<Renderer> renderers) {
    List<RendererContract> rendererContracts = new ArrayList<>();
    for (Renderer renderer : renderers) {
      rendererContracts.add(RendererContractBuilder.createFrom(renderer));
    }
    return rendererContracts;
  }
}
