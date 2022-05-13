/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
