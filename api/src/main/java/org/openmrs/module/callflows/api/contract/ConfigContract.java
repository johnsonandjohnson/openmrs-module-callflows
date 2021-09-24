package org.openmrs.module.callflows.api.contract;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration Contract, used in both requests and responses
 * Maps directly to Config domain
 *
 * @author bramak09
 * @see org.openmrs.module.callflows.api.domain.Config
 */
public class ConfigContract {

    /**
     * The configuration name, say voxeo
     */
    @NotBlank
    private String name;

    /**
     * The request method that is used when connecting to the IVR provider
     */
    @NotNull
    @Pattern(regexp = "^(POST|GET)$")
    private String outgoingCallMethod;

    /**
     * True, if authentication required to connect IVR provider
     */
    private Boolean hasAuthRequired;

    /**
     * HTTP POST headers.
     */
    private Map<String, String> outgoingCallPostHeadersMap = new HashMap<>();

    /**
     * HTTP POST params.
     */
    private String outgoingCallPostParams;

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
     * The number of calls that are allowed concurrently as per the contract with the IVR provider.
     * It is in business benefit to not exceed this or atleast make best efforts to not exceed
     */
    private int outboundCallLimit;

    /**
     * The number of times to retry if the outbound call limit is hit
     */
    private int outboundCallRetryAttempts;

    /**
     * The retry wait seconds before a retry is made when the outbound call limit is hit
     */
    private int outboundCallRetrySeconds;

    /**
     * Indicates whether the call can be placed if the retry attempts have been exceeded and we are still above the outbound call limit
     */
    private Boolean callAllowed;

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

    public int getOutboundCallLimit() {
        return outboundCallLimit;
    }

    public void setOutboundCallLimit(int outboundCallLimit) {
        this.outboundCallLimit = outboundCallLimit;
    }

    public int getOutboundCallRetryAttempts() {
        return outboundCallRetryAttempts;
    }

    public void setOutboundCallRetryAttempts(int outboundCallRetryAttempts) {
        this.outboundCallRetryAttempts = outboundCallRetryAttempts;
    }

    public int getOutboundCallRetrySeconds() {
        return outboundCallRetrySeconds;
    }

    public void setOutboundCallRetrySeconds(int outboundCallRetrySeconds) {
        this.outboundCallRetrySeconds = outboundCallRetrySeconds;
    }

    public Boolean isCallAllowed() {
        return callAllowed;
    }

    public void setCallAllowed(Boolean callAllowed) {
        this.callAllowed = callAllowed;
    }

    public Map<String, String> getTestUsersMap() {
        return testUsersMap;
    }

    public void setTestUsersMap(Map<String, String> testUsersMap) {
        this.testUsersMap = testUsersMap;
    }

    public Boolean getHasAuthRequired() {
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
