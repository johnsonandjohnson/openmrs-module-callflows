package com.janssen.connectforlife.callflows.domain;

import org.apache.commons.lang.StringUtils;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Configuration entity, adapted from IVR Module
 */
public class Config {

    private String name;

    private String outgoingCallUriTemplate;

    private String outgoingCallMethod;

    private Map<String, String> servicesMap = new HashMap<>();

    private String testUsersMapString;

    private Map<String, String> testUsersMap = new HashMap<>();

    private String servicesMapString;

    private Gson gson;

    public Config() {
        this.gson = new Gson();
    }

    public Config(String name, String outgoingCallUriTemplate, String outgoingCallMethod, String servicesMapString,
                  String testUsersMapString) {
        gson = new Gson();
        this.name = name;
        this.outgoingCallUriTemplate = outgoingCallUriTemplate;
        this.outgoingCallMethod = outgoingCallMethod;
        this.servicesMapString = servicesMapString;
        this.servicesMap = parseStringToMap(servicesMapString);
        this.testUsersMapString = testUsersMapString;
        this.testUsersMap = parseJsonToMap(testUsersMapString);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutgoingCallUriTemplate() {
        return outgoingCallUriTemplate;
    }

    public void setOutgoingCallUriTemplate(String outgoingCallUriTemplate) {
        this.outgoingCallUriTemplate = outgoingCallUriTemplate;
    }

    public String getOutgoingCallMethod() {
        return outgoingCallMethod;
    }

    public void setOutgoingCallMethod(String outgoingCallMethod) {
        this.outgoingCallMethod = outgoingCallMethod;
    }

    public Map<String, String> getServicesMap() {
        return servicesMap;
    }

    public void setServicesMap(Map<String, String> servicesMap) {
        this.servicesMap = servicesMap;
    }

    public Map<String, String> getTestUsersMap() {
        return testUsersMap;
    }

    public void setTestUsersMap(Map<String, String> testUsersMap) {
        this.testUsersMap = testUsersMap;
    }

    public String getTestUsersMapString() {
        return testUsersMapString;
    }

    public void setTestUsersMapString(String testUsersMapString) {
        this.testUsersMapString = testUsersMapString;
        this.testUsersMap = parseJsonToMap(this.testUsersMapString);
    }

    public String getServicesMapString() {
        return servicesMapString;
    }

    public void setServicesMapString(String servicesMapString) {
        this.servicesMapString = servicesMapString;
        this.servicesMap = parseStringToMap(this.servicesMapString);
    }

    private Map<String, String> parseJsonToMap(String string) {
        if (string == null) {
            return new HashMap<>();
        }
        return gson.fromJson(string, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    private Map<String, String> parseStringToMap(String string) {
        //todo: replace that with guava Splitter when guava 18.0 is available in external-osgi-bundles
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(string)) {
            return map;
        }
        String[] strings = string.split("\\s*,\\s*");
        for (String s : strings) {
            String[] kv = s.split("\\s*:\\s*");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else {
                throw new IllegalArgumentException(String.format("%s is an invalid map", string));
            }
        }
        return map;
    }
}

