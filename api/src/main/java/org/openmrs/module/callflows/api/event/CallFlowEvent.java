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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;
import org.openmrs.module.callflows.api.util.CallFlowTaskUtil;
import org.openmrs.module.callflows.api.util.DateUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * CallFlow Event
 */
public class CallFlowEvent {

  private static final String RETRY_PROPERTIES = "RETRY_PROPERTIES";
  private static final String SHORT_DATE_FORMAT = "yyyyMMddHHmmz";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
  public Map<String, String> convertToTaskDefinitionProperties() {
    try {
      return Collections.singletonMap(RETRY_PROPERTIES, OBJECT_MAPPER.writeValueAsString(getParameters()));
    } catch (IOException e) {
      throw new CallFlowRuntimeException("Failed to write retry parameters from event's parameters: " + getParameters(),
          e);
    }
  }

  /**
   * convert the properties with a map of properties
   *
   * @param properties is a map
   * @return is a map result
   */
  public static Map<String, Object> convertFromTaskDefinitionProperties(Map<String, String> properties) {
    try {
      return OBJECT_MAPPER.readValue(properties.get(RETRY_PROPERTIES), Map.class);
    } catch (IOException e) {
      throw new CallFlowRuntimeException("Failed to read retry parameters from task's properties: " + properties, e);
    }
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
