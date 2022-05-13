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

import org.openmrs.module.callflows.api.domain.flow.Flow;

/**
 * A representation of a step in a flow that we will jump to from one of the templates
 *
 * @author bramak09
 */
public class FlowStep {

    /**
     * The flow object in this instance.
     */
    private Flow flow;

    /**
     * The step name
     */
    private String step;

    public Flow getFlow() {
        return flow;
    }

    public FlowStep setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    public String getStep() {
        return step;
    }

    public FlowStep setStep(String step) {
        this.step = step;
        return this;
    }
}


