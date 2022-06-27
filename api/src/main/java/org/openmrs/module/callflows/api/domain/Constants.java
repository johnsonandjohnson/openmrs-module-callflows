/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.domain;

/**
 * Constants
 *
 * @author bramak09
 */
public final class Constants {

  public static final String PARAM_PHONE = "phone";

  public static final String PARAM_JUMP_TO = "jumpTo";

  public static final String PARAM_INTERNAL = "internal";

  public static final String PARAM_CALL_ID = "callId";

  public static final String PARAM_ERROR = "error";

  public static final String PARAM_ACTOR_ID = "actorId";

  public static final String PARAM_PERSON_ID = "personId";

  public static final String PARAM_ACTOR_TYPE = "actorType";

  public static final String PARAM_EXTERNAL_ID = "externalId";

  public static final String PARAM_EXTERNAL_TYPE = "externalType";

  public static final String PARAM_PLAYED_MESSAGES = "playedMessages";

  public static final String PARAM_REF_KEY = "refKey";

  public static final String PARAM_REASON = "reason";

  public static final String PARAM_STATUS = "status";

  public static final String PARAM_PARAMS = "params";

  public static final String PARAM_HEADERS = "headers";

  public static final String PARAM_CONFIG = "config";

  public static final String PARAM_FLOW_NAME = "flowName";

  public static final String PARAM_RETRY_ATTEMPTS = "retryAttempts";

  public static final String PARAM_JOB_ID = "JobID";

  public static final String CALLFLOW_ENDED_STATUSES_GP_KEY = "messages.statusesEndingCallflow";

  public static final String CALLFLOW_ENDED_STATUSES =
      "UNANSWERED,MACHINE,BUSY,CANCELLED,FAILED,REJECTED,NO_ANSWER,TIMEOUT,COMPLETED,UNKNOWN";

  // private constructor
  private Constants() {}
}
