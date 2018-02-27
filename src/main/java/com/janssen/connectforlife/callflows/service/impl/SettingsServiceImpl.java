package com.janssen.connectforlife.callflows.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.domain.Settings;
import com.janssen.connectforlife.callflows.service.SettingsService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.config.SettingsFacade;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
@Service("settingsService")
public class SettingsServiceImpl implements SettingsService {

    private static final String CONFIG_FILE_NAME = "callflows-settings.json";
    private static final String CONFIG_FILE_PATH = "/com.janssen.connectforlife.callflows/raw/" + CONFIG_FILE_NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsServiceImpl.class);

    @Autowired
    private SettingsFacade settingsFacade;

    private Settings settings = new Settings();

    private Map<String, Config> configs = new LinkedHashMap<>();

    private Map<String, Renderer> renderers = new LinkedHashMap<>();

    @PostConstruct
    public void initialize() {
        loadSettings();
    }

    @MotechListener(subjects = { ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT })
    public void handleFileChanged(MotechEvent event) {
        String filePath = (String) event.getParameters().get(ConfigurationConstants.FILE_PATH);
        if (!StringUtils.isBlank(filePath) && filePath.endsWith(CONFIG_FILE_PATH)) {
            LOGGER.info("{} has changed, reloading configs.", CONFIG_FILE_NAME);
            loadSettings();
        }
    }

    @Override
    public Config getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        }
        String message = String.format("Unknown config: '%s'.", name);
        throw new IllegalArgumentException(message);
    }

    @Override
    public List<Config> allConfigs() {
        return new ArrayList<Config>(configs.values());
    }

    @Override
    public boolean hasConfig(String name) {
        return configs.containsKey(name);
    }

    @Override
    public void updateConfigs(List<Config> configs) {
        settings.setConfigs(configs);
        updateSettings(settings);
    }

    @Override
    public boolean hasRenderer(String name) {
        return renderers.containsKey(name);
    }

    @Override
    public Renderer getRenderer(String name) {
        if (renderers.containsKey(name)) {
            return renderers.get(name);
        }
        String message = String.format("Unknown config: '%s'.", name);
        throw new IllegalArgumentException(message);
    }

    @Override
    public List<Renderer> allRenderers() {
        return new ArrayList<Renderer>(renderers.values());
    }

    @Override
    public void updateRenderers(List<Renderer> renderers) {
        settings.setRenderers(renderers);
        updateSettings(settings);
    }

    private void updateSettings(Settings settings) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonText = gson.toJson(settings);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(CONFIG_FILE_NAME, resource);
        loadSettings();
    }

    private synchronized void loadSettings() {
        List<Config> configList;
        List<Renderer> rendererList;
        try (InputStream is = settingsFacade.getRawConfig(CONFIG_FILE_NAME)) {
            String jsonText = IOUtils.toString(is);
            LOGGER.debug("Loading {}", CONFIG_FILE_NAME);
            Gson gson = new Gson();
            settings = gson.fromJson(jsonText, Settings.class);
            configList = settings.getConfigs();
            rendererList = settings.getRenderers();
        } catch (Exception e) {
            String message = String.format("There seems to be a problem with the json text in %s: %s",
                                           CONFIG_FILE_NAME,
                                           e.getMessage());
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
}
