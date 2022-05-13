/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.helper;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.callflows.api.domain.flow.Flow;

import java.io.IOException;

/**
 * Flow Helper to manage Flow objects
 *
 * @author bramak09
 */
public final class FlowHelper {

    // private constructor
    private FlowHelper() {

    }

    public static Flow createFlow(String raw) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(raw, Flow.class);
    }
}


