/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.Settings;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.openmrs.module.callflows.api.util.CallFlowConstants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration Service Implementation
 *
 * @author bramak09
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ConfigServiceImpl extends BaseOpenmrsService implements ConfigService {

  private static final Log LOGGER = LogFactory.getLog(ConfigServiceImpl.class);

  private SettingsManagerService settingsManagerService;

  private Settings settings = new Settings();

  private Map<String, Config> configs = new LinkedHashMap<>();

  private Map<String, Renderer> renderers = new LinkedHashMap<>();

  /** Initializes and loads the Settings. */
  @PostConstruct
  public void initialize() {
    loadSettings();
  }

  /**
   * Get a configuration for a specific name
   *
   * @param name identifying a configuration uniquely
   * @return the configuration if found
   * @throws IllegalArgumentException if configuration could not be found
   */
  @Override
  public Config getConfig(String name) {
    if (configs.containsKey(name)) {
      return configs.get(name);
    }
    String message = String.format("Unknown config: '%s'.", name);
    throw new IllegalArgumentException(message);
  }

  /**
   * Get all configurations present in the system
   *
   * @return a list of configurations
   */
  @Override
  public List<Config> allConfigs() {
    return new ArrayList<>(configs.values());
  }

  /**
   * Checks if configuration for a given name exists
   *
   * @param name to check
   * @return true or false indicating whether the configuration exists or not
   */
  @Override
  public boolean hasConfig(String name) {
    return configs.containsKey(name);
  }

  /**
   * Updates a list of configurations
   *
   * @param configs a list of configurations
   */
  @Override
  public void updateConfigs(List<Config> configs) {
    settings.setConfigs(configs);
    updateSettings(settings);
  }

  /**
   * Checks if renderer for a given name exists
   *
   * @param name to check
   * @return true or false indicating whether the renderer exists or not
   */
  @Override
  public boolean hasRenderer(String name) {
    return renderers.containsKey(name);
  }

  /**
   * Get a specific renderer
   *
   * @param name of the renderer to retrieve
   * @return the renderer object if available
   * @throws IllegalArgumentException if the renderer is not available
   */
  @Override
  public Renderer getRenderer(String name) {
    if (renderers.containsKey(name)) {
      return renderers.get(name);
    }
    String message = String.format("Unknown config: '%s'.", name);
    throw new IllegalArgumentException(message);
  }

  /**
   * Get all renderers in the system
   *
   * @return a list of renderers
   */
  @Override
  public List<Renderer> allRenderers() {
    return new ArrayList<Renderer>(renderers.values());
  }

  /**
   * Updates a list of renderers
   *
   * @param renderers a list of renderers
   */
  @Override
  public void updateRenderers(List<Renderer> renderers) {
    settings.setRenderers(renderers);
    updateSettings(settings);
  }

  private void updateSettings(Settings settings) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    String jsonText = gson.toJson(settings);
    ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
    settingsManagerService.saveRawConfig(CallFlowConstants.CONFIG_FILE_NAME, resource);
    loadSettings();
  }

  private synchronized void loadSettings() {
    List<Config> configList;
    List<Renderer> rendererList;
    loadDefaultConfigurationIfNotExists(CallFlowConstants.CONFIG_FILE_NAME);
    try (InputStream is = settingsManagerService.getRawConfig(CallFlowConstants.CONFIG_FILE_NAME)) {
      String jsonText = IOUtils.toString(is);
      LOGGER.debug(String.format("Loading %s", CallFlowConstants.CONFIG_FILE_NAME));
      Gson gson = new Gson();
      settings = gson.fromJson(jsonText, Settings.class);
      configList = settings.getConfigs();
      rendererList = settings.getRenderers();
    } catch (Exception e) {
      String message =
          String.format(
              "There seems to be a problem with the json text in %s: %s",
              CallFlowConstants.CONFIG_FILE_NAME, e.getMessage());
      throw new JsonIOException(message, e);
    }

    configs = new LinkedHashMap<>();
    for (Config config : configList) {
      configs.put(config.getName(), config);
    }

    renderers = new LinkedHashMap<>();
    for (Renderer renderer : rendererList) {
      renderers.put(renderer.getName(), renderer);
    }
  }

  private void loadDefaultConfigurationIfNotExists(String filename) {
    if (!settingsManagerService.configurationExist(filename)) {
      String defaultConfiguration = readResourceFile(filename);
      ByteArrayResource resource = new ByteArrayResource(defaultConfiguration.getBytes());
      settingsManagerService.saveRawConfig(filename, resource);
    }
  }

  private String readResourceFile(String filename) throws CallFlowRuntimeException {
    try (InputStream in = ConfigServiceImpl.class.getClassLoader().getResourceAsStream(filename)) {
      if (in == null) {
        throw new CallFlowRuntimeException("Resource '" + filename + "' doesn't exist");
      }
      return IOUtils.toString(in);
    } catch (IOException e) {
      throw new CallFlowRuntimeException(e);
    }
  }

  /**
   * Sets the SettingsManagerService
   *
   * @param settingsManagerService to set
   */
  public void setSettingsManagerService(SettingsManagerService settingsManagerService) {
    this.settingsManagerService = settingsManagerService;
  }
}
