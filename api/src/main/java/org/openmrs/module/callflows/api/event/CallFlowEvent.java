/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.event;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.util.CallFlowTaskUtil;

import java.util.HashMap;
import java.util.Map;

public class CallFlowEvent {

    private static final String CUSTOM_PARAMS_DELIMITER = ",";
    private static final String CUSTOM_PARAMS_KEY_VALUE_SEPARATOR = "=";
    private String subject;
    private Map<String, Object> parameters;

    public CallFlowEvent(String subject, Map<String, Object> parameters) {
        this.subject = subject;
        this.parameters = parameters;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getJobId() {
        return (String) getParameters().get(Constants.PARAM_JOB_ID);
    }

    public String generateTaskName() {
        return CallFlowTaskUtil.generateTaskName(getSubject(), getJobId());
    }

    public Map<String, String> convertProperties() {
        Map<String, String> result = new HashMap<>();

        for (String key : getParameters().keySet()) {
            if (Constants.PARAM_PARAMS.equals(key)) {
                result.put(key, Joiner.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .join((Map<?, ?>) getParameters().get(key)));
            } else {
                result.put(key, (String) getParameters().get(key));
            }
        }

        return result;
    }

    public static Map<String, Object> convertProperties(Map<String, String> properties) {
        Map<String, Object> result = new HashMap<>();
        for (String key : properties.keySet()) {
            if (Constants.PARAM_PARAMS.equals(key)) {
                Map<String, Object> params = new HashMap<>(Splitter.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .split(properties.get(key)));
                if (params.containsKey(Constants.PARAM_RETRY_ATTEMPTS)) {
                    params.put(Constants.PARAM_RETRY_ATTEMPTS, Integer.valueOf(
                            (String) params.get(Constants.PARAM_RETRY_ATTEMPTS)));
                }
                result.put(key, params);
            } else {
                result.put(key, properties.get(key));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
