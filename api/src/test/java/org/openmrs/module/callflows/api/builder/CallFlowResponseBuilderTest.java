/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.builder;

import org.junit.Test;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Flow Response Builder Test
 *
 * @author bramak09
 */
public class CallFlowResponseBuilderTest extends BaseTest {

    private CallFlowResponse callFlowResponse;

    private CallFlow callFlow;

    @Test
    public void shouldBuildCallFlowResponseFromCallFlow() {
        // Given
        callFlow = CallFlowHelper.createMainFlow();
        callFlow.setId(1);
        // When
        callFlowResponse = CallFlowResponseBuilder.createFrom(callFlow);
        // Then
        assertThat(callFlowResponse.getId(), equalTo(callFlow.getId()));
        assertThat(callFlowResponse.getName(), equalTo(callFlow.getName()));
        assertThat(callFlowResponse.getDescription(), equalTo(callFlow.getDescription()));
        assertThat(callFlowResponse.getStatus(), equalTo(callFlow.getStatus().name()));
        assertThat(callFlowResponse.getRaw(), equalTo(callFlow.getRaw()));
    }

}
