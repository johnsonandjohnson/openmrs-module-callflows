package org.openmrs.module.callflows.testbuilder;

import org.openmrs.module.callflows.api.contract.ConfigContract;

public class ConfigContractBuilder {
    private static final String DEFAULT_NAME = "John Smith";

    private String name;

    /**
     * Returns instance of {@link ConfigContractBuilder} with sample data.
     */
    public ConfigContractBuilder() {
        name = DEFAULT_NAME;
    }

    /**
     * Builds instance of {@link ConfigContract}.
     */
    public ConfigContract build() {
        ConfigContract configContract = new ConfigContract();
        configContract.setName(name);
        return configContract;
    }

    /**
     * Adds Module for new {@link ConfigContract}.
     */
    public ConfigContractBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
