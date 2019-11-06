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
