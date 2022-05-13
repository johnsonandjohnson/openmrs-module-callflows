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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.helper.ConfigHelper;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Config Contract Builder Test
 *
 * @author bramak09
 */

public class ConfigContractBuilderTest extends BaseTest {

    private Config voxeo;

    @Before
    public void setUp() {
        voxeo = ConfigHelper.createConfigs().get(0);
    }

    @Test
    public void shouldBuildConfigContractFromConfig() throws IOException {
        // Given

        // When
        ConfigContract voxeoContract = ConfigContractBuilder.createFrom(voxeo);

        // Then
        assertThat(voxeoContract.getName(), equalTo(voxeo.getName()));
        assertThat(voxeoContract.getOutgoingCallMethod(), equalTo(voxeo.getOutgoingCallMethod()));
        assertThat(voxeoContract.getOutgoingCallUriTemplate(), equalTo(voxeo.getOutgoingCallUriTemplate()));
        assertThat(voxeoContract.getOutboundCallLimit(), equalTo(voxeo.getOutboundCallLimit()));
        assertThat(voxeoContract.getOutboundCallRetryAttempts(), equalTo(voxeo.getOutboundCallRetryAttempts()));
        assertThat(voxeoContract.getOutboundCallRetrySeconds(), equalTo(voxeo.getOutboundCallRetrySeconds()));
        assertThat(voxeoContract.isCallAllowed(), equalTo(voxeo.getCallAllowed()));

        assertThat(voxeoContract.getTestUsersMap().size(), equalTo(voxeo.getTestUsersMap().size()));
        assertThat(voxeoContract.getTestUsersMap(), equalTo(voxeo.getTestUsersMap()));

        assertThat(voxeoContract.getServicesMap().size(), equalTo(voxeo.getServicesMap().size()));
        assertThat(voxeoContract.getServicesMap(), equalTo(voxeo.getServicesMap()));

    }
}

