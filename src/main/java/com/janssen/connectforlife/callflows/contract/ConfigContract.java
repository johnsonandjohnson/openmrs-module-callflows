package com.janssen.connectforlife.callflows.contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration Contract, used in both requests and responses
 * Maps directly to Config domain
 *
 * @author bramak09
 * @see com.janssen.connectforlife.callflows.domain.Config
 */
public class ConfigContract {

    /**
     * The configuration name, say voxeo
     */
    private String name;

    /**
     * The request method that is used when connecting to the IVR provider
     */
    private String outgoingCallMethod;

    /**
     * The url to use when connecting to the IVR provider.
     * This is IVR provider specific. Use [xxx], [yyy] to denote placeholders that will be substituted at runtime
     */
    private String outgoingCallUriTemplate;

    /**
     * A map of OSGI services that can be used in the callflows
     * The key is the friendly name to use in the templates and the value is the fully qualified name of the OSGI interface
     */
    private Map<String, String> servicesMap = new HashMap<>();

    /**
     * A map of test users
     * The key is a phone number and the value is a URL that can respond to a specific call request
     * This can be used to over-ride the outbound URL used for a given user and like in outgoingCallUriTemplate, this can also
     * use [xxx], [yyy] to denote placeholders that will be substituted at runtime
     * The test users can thus be used to connect individual simulators to specific phone numbers for testing
     */
    private Map<String, String> testUsersMap = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutgoingCallMethod() {
        return outgoingCallMethod;
    }

    public void setOutgoingCallMethod(String outgoingCallMethod) {
        this.outgoingCallMethod = outgoingCallMethod;
    }

    public String getOutgoingCallUriTemplate() {
        return outgoingCallUriTemplate;
    }

    public void setOutgoingCallUriTemplate(String outgoingCallUriTemplate) {
        this.outgoingCallUriTemplate = outgoingCallUriTemplate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigContract)) {
            return false;
        }
        final ConfigContract other = (ConfigContract) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

