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
import org.openmrs.module.callflows.api.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * CallFlow Event
 */
public class CallFlowEvent {

  private static final String SHORT_DATE_FORMAT = "yyyyMMddHHmmz";
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

  /**
   * Get the Job Id
   *
   * @return Return the string
   */
  public String getJobId() {
    return (String) getParameters().get(Constants.PARAM_JOB_ID);
  }

  /**
   * @param startDate the start date time of a task to generate name for, not null
   * @return the task name, never null
   */
  public String generateTaskName(Date startDate) {
    return String.format("%s:%s", this.getSubject(), DateUtil.dateToString(startDate, SHORT_DATE_FORMAT));
  }

  /**
   * @return the task description, never null
   */
  public String generateTaskDescription() {
    return CallFlowTaskUtil.generateTaskName(getSubject(), getJobId());
  }

  /**
   * convert the properties
   *
   * @return is a map result
   */
  public Map<String, String> convertProperties() {
    Map<String, String> result = new HashMap<>();

    for (Map.Entry<String, Object> parameter : getParameters().entrySet()) {
      if (parameter.getValue() instanceof Map) {
        result.put(parameter.getKey(), Joiner
            .on(CUSTOM_PARAMS_DELIMITER)
            .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
            .join((Map<?, ?>) parameter.getValue()));
      } else {
        result.put(parameter.getKey(), Objects.toString(parameter.getValue(), null));
      }
    }

    return result;
  }

  /**
   * convert the properties with a map of properties
   *
   * @param properties is a map
   * @return is a map result
   */
  public static Map<String, Object> convertProperties(Map<String, String> properties) {
    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, String> property : properties.entrySet()) {
      if (Constants.PARAM_PARAMS.equals(property.getKey())) {
        Map<String, Object> params = new HashMap<>(Splitter
            .on(CUSTOM_PARAMS_DELIMITER)
            .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
            .split(property.getValue()));

        // Convert retry attempts to Integer
        params.computeIfPresent(Constants.PARAM_RETRY_ATTEMPTS, (key, value) -> Integer.valueOf(value.toString()));

        result.put(property.getKey(), params);
      } else {
        result.put(property.getKey(), property.getValue());
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
