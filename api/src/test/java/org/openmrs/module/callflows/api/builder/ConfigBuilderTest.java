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
 * Config Builder Test
 *
 * @author bramak09
 */
public class ConfigBuilderTest extends BaseTest {

    private ConfigContract voxeoContract;

    @Before
    public void setUp() {
        voxeoContract = ConfigHelper.createConfigContracts().get(0);
    }

    @Test
    public void shouldBuildConfigFromContract() throws IOException {
        // Given

        // When
        Config voxeo = ConfigBuilder.createFrom(voxeoContract);

        // Then

        assertThat(voxeo.getName(), equalTo(voxeoContract.getName()));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(voxeoContract.getOutgoingCallMethod()));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(voxeoContract.getOutgoingCallUriTemplate()));
        assertThat(voxeo.getOutboundCallLimit(), equalTo(voxeoContract.getOutboundCallLimit()));
        assertThat(voxeo.getOutboundCallRetryAttempts(), equalTo(voxeoContract.getOutboundCallRetryAttempts()));
        assertThat(voxeo.getOutboundCallRetrySeconds(), equalTo(voxeoContract.getOutboundCallRetrySeconds()));
        assertThat(voxeo.getCallAllowed(), equalTo(voxeoContract.isCallAllowed()));

        assertThat(voxeo.getTestUsersMap().size(), equalTo(voxeoContract.getTestUsersMap().size()));
        assertThat(voxeo.getTestUsersMap(), equalTo(voxeoContract.getTestUsersMap()));

        assertThat(voxeo.getServicesMap().size(), equalTo(voxeoContract.getServicesMap().size()));
        assertThat(voxeo.getServicesMap(), equalTo(voxeoContract.getServicesMap()));

    }

}

