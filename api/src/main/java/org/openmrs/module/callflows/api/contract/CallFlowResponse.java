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
 * The Callflow response contract
 *
 * @author bramak09
 */
public class CallFlowResponse {

    /**
     * Call flow ID
     */
    private Integer id;

    /**
     * Call flow name
     */
    private String name;

    /**
     * Call flow description
     */
    private String description;

    /**
     * Call flow status
     */
    private String status;

    /**
     * Call flow raw representation as a string
     */
    private String raw;

    public CallFlowResponse(Integer id, String name, String description, String status, String raw) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.raw = raw;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

}
