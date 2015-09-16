package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.domain.Config;

import java.util.List;

/**
 * Config Service to manage configuration data of callflows
 * This is inspired and adapted from the IVR module
 *
 * @author bramak09
 */
public interface ConfigService {

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

}
