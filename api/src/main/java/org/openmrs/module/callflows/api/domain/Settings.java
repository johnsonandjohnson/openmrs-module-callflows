/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The master settings domain manages both IVR configurations and renderers and represents the object that is serialized
 * in the database using OpenMRS configuration service
 *
 * @author bramak09
 */
public class Settings {

    private List<Config> configs;

    private List<Renderer> renderers;

    public Settings() {
        configs = new ArrayList<Config>();
        renderers = new ArrayList<Renderer>();
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    public void setRenderers(List<Renderer> renderers) {
        this.renderers = renderers;
    }
}
