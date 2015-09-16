package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.service.ConfigService;

import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.config.SettingsFacade;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

/**
 * Configuration Service Implementation
 *
 * @author bramak09
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    private static final String CONFIG_FILE_NAME = "callflows-configs.json";
    private static final String CONFIG_FILE_PATH = "/com.janssen.connectforlife.callflows/raw/" + CONFIG_FILE_NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    private SettingsFacade settingsFacade;

    private Map<String, Config> configs = new LinkedHashMap<>();

    @PostConstruct
    public void initialize() {
        loadConfigs();
    }

    @MotechListener(subjects = { ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT })
    public void handleFileChanged(MotechEvent event) {
        String filePath = (String) event.getParameters().get(ConfigurationConstants.FILE_PATH);
        if (!StringUtils.isBlank(filePath) && filePath.endsWith(CONFIG_FILE_PATH)) {
            LOGGER.info("{} has changed, reloading configs.", CONFIG_FILE_NAME);
            loadConfigs();
        }
    }

    @Override
    public Config getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        }
        String message = String.format("Unknown config: '%s'.", name);
        LOGGER.error(message);
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
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonText = gson.toJson(configs);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(CONFIG_FILE_NAME, resource);
        loadConfigs();
    }

    private synchronized void loadConfigs() {
        List<Config> configList;
        try (InputStream is = settingsFacade.getRawConfig(CONFIG_FILE_NAME)) {
            String jsonText = IOUtils.toString(is);
            LOGGER.debug("Loading {}", CONFIG_FILE_NAME);
            Gson gson = new Gson();
            configList = gson.fromJson(jsonText, new TypeToken<List<Config>>() {
            }.getType());
        } catch (Exception e) {
            String message = String.format("There seems to be a problem with the json text in %s: %s",
                                           CONFIG_FILE_NAME,
                                           e.getMessage());
            LOGGER.error(message, e);
            throw new JsonIOException(message, e);
        }

        configs = new LinkedHashMap<>();
        for (Config config : configList) {
            configs.put(config.getName(), config);
        }
    }

}
