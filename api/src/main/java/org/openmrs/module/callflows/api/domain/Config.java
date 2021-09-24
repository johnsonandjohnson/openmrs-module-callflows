package org.openmrs.module.callflows.api.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration entity, adapted from IVR Module and enhanced
 */
public class Config {

    /**
     * The configuration name, say voxeo
     */
    private String name;

    /**
     * The url to use when connecting to the IVR provider.
     * This is IVR provider specific. Use [xxx], [yyy] to denote placeholders that will be substituted at runtime
     */
    private String outgoingCallUriTemplate;

    /**
     * HTTP POST headers.
     */
    private Map<String, String> outgoingCallPostHeadersMap = new HashMap<>();

    /**
     * HTTP POST params.
     */
    private String outgoingCallPostParams;

    /**
     * The request method that is used when connecting to the IVR provider
     */
    private String outgoingCallMethod;

    private int outboundCallLimit;

    private int outboundCallRetrySeconds;

    private int outboundCallRetryAttempts;

    private Boolean callAllowed;

    private String authToken;

    private boolean hasAuthRequired;

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

    public int getOutboundCallLimit() {
        return outboundCallLimit;
    }

    public void setOutboundCallLimit(int outboundCallLimit) {
        this.outboundCallLimit = outboundCallLimit;
    }

    public int getOutboundCallRetrySeconds() {
        return outboundCallRetrySeconds;
    }

    public void setOutboundCallRetrySeconds(int outboundCallRetrySeconds) {
        this.outboundCallRetrySeconds = outboundCallRetrySeconds;
    }

    public int getOutboundCallRetryAttempts() {
        return outboundCallRetryAttempts;
    }

    public void setOutboundCallRetryAttempts(int outboundCallRetryAttempts) {
        this.outboundCallRetryAttempts = outboundCallRetryAttempts;
    }

    public Boolean getCallAllowed() {
        return callAllowed;
    }

    public void setCallAllowed(Boolean callAllowed) {
        this.callAllowed = callAllowed;
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

    public Map<String, String> getOutgoingCallPostHeadersMap() {
        return outgoingCallPostHeadersMap;
    }

    public void setOutgoingCallPostHeadersMap(Map<String, String> outgoingCallPostHeadersMap) {
        this.outgoingCallPostHeadersMap = outgoingCallPostHeadersMap;
    }

    public String getOutgoingCallPostParams() {
        return outgoingCallPostParams;
    }

    public void setOutgoingCallPostParams(String outgoingCallPostParams) {
        this.outgoingCallPostParams = outgoingCallPostParams;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean getHasAuthRequired() {
        return hasAuthRequired;
    }

    public void setHasAuthRequired(Boolean hasAuthRequired) {
        this.hasAuthRequired = hasAuthRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        final Config other = (Config) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
