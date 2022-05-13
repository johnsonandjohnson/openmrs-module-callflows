/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.contract;

/**
 * A Response at one stage of the call execution
 *
 * @author bramak09
 */
public class JsonExecutionResponse {

    /**
     * The body of the response.
     * Typically this is the output of a velocity template interpreted at a certain step in the call flow.
     * If however there was an error during the execution of a velocity template, then the body contains the error message instead
     */
    private String body;

    /**
     * The call id, so that the client that's processing this response can continue the conversation.
     */
    private String callId;

    /**
     * The current node we are at
     */
    private String node;

    /**
     * The current node's continue attribute
     */
    private Boolean continueNode;

    /**
     * Does this response signify a error response
     */
    private boolean isError;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public Boolean isContinueNode() {
        return continueNode;
    }

    public void setContinueNode(Boolean continueNode) {
        this.continueNode = continueNode;
    }
}
