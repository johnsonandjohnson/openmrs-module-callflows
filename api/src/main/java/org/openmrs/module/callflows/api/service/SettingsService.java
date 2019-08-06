package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;

import java.util.List;

/**
 * Settings Service to manage configuration data of callflows
 * Currently maintains IVR provider data as a Config object and a set of Renderers
 * This interface is inspired and adapted from MOTECH IVR module
 *
 * @author bramak09
 */
public interface SettingsService {

    /**
     * Get a configuration for a specific name
     *
     * @param name identifying a configuration uniquely
     * @return the configuration if found
     * @throws IllegalArgumentException if configuration could not be found
     */
    Config getConfig(String name);

    /**
     * Get all configurations present in the system
     *
     * @return a list of configurations
     */
    List<Config> allConfigs();

    /**
     * Checks if configuration for a given name exists
     *
     * @param name to check
     * @return true or false indicating whether the configuration exists or not
     */
    boolean hasConfig(String name);

    /**
     * Updates a list of configurations
     *
     * @param configs a list of configurations
     */
    void updateConfigs(List<Config> configs);

    /**
     * Get a specific renderer
     *
     * @param name of the renderer to retrieve
     * @return the renderer object if available
     * @throws IllegalArgumentException if the renderer is not available
     */
    Renderer getRenderer(String name);

    /**
     * Checks if renderer for a given name exists
     *
     * @param name to check
     * @return true or false indicating whether the renderer exists or not
     */
    boolean hasRenderer(String name);

    /**
     * Get all renderers in the system
     *
     * @return a list of renderers
     */
    List<Renderer> allRenderers();

    /**
     * Updates a list of renderers
     *
     * @param renderers a list of renderers
     */
    void updateRenderers(List<Renderer> renderers);
}
