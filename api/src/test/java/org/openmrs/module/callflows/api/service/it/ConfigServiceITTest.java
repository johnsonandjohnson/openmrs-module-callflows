/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.RendererHelper;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Configuration Service Integration Tests
 *
 * @author bramak09
 */
public class ConfigServiceITTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConfigService configService;

    private List<Config> configs;

    private List<Renderer> renderers;

    @Before
    public void setUp() throws IOException {

        // Save a bunch of configs in the database
        configs = ConfigHelper.createConfigs();
        configService.updateConfigs(configs);

        // and some renderers
        renderers = RendererHelper.createRenderers();
        configService.updateRenderers(renderers);
    }

    @After
    public void tearDown() {
        // reset by emptying out the configuration
        configService.updateConfigs(new ArrayList<Config>());
        configService.updateRenderers(new ArrayList<Renderer>());
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(configService);
    }

    @Test
    public void shouldGetConfigForValidName() {
        // When
        Config voxeo = configService.getConfig(Constants.CONFIG_VOXEO);
        // Then
        assertNotNull(voxeo);
        assertThat(voxeo.getName(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_VOXEO_METHOD));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_VOXEO_OUT_TEMPLATE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidConfig() {
        // When, Then
        Config voxeo = configService.getConfig(Constants.INVALID);
    }

    @Test
    public void shouldGetAllConfigs() {
        // When
        List<Config> allConfigs = configService.allConfigs();
        // Then
        assertNotNull(allConfigs);
        assertThat(allConfigs.size(), equalTo(3));
        Config voxeo = allConfigs.get(0);
        Config yo = allConfigs.get(1);
        Config imiMobile = allConfigs.get(2);

        assertThat(voxeo.getName(), equalTo(Constants.CONFIG_VOXEO));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_VOXEO_METHOD));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_VOXEO_OUT_TEMPLATE));
        assertThat(voxeo.getOutboundCallLimit(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT));
        assertThat(voxeo.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(voxeo.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(voxeo.getCallAllowed(), equalTo(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL));

        assertThat(yo.getName(), equalTo(Constants.CONFIG_YO));
        assertThat(yo.getOutgoingCallMethod(), equalTo(Constants.CONFIG_YO_METHOD));
        assertThat(yo.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_YO_OUT_TEMPLATE));
        assertThat(yo.getOutboundCallLimit(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_LIMIT));
        assertThat(yo.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(yo.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(yo.getCallAllowed(), equalTo(Constants.CONFIG_YO_CAN_PLACE_OUTBOUND_CALL));

        assertThat(imiMobile.getName(), equalTo(Constants.CONFIG_IMI_MOBILE));
        assertThat(imiMobile.getOutgoingCallMethod(), equalTo(Constants.CONFIG_IMI_METHOD));
        assertThat(imiMobile.getOutgoingCallUriTemplate(), equalTo(Constants.CONFIG_IMI_OUT_TEMPLATE));
        assertThat(imiMobile.getOutboundCallLimit(), equalTo(Constants.CONFIG_IMI_OUTBOUND_CALL_LIMIT));
        assertThat(imiMobile.getOutboundCallRetryAttempts(), equalTo(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_ATTEMPTS));
        assertThat(imiMobile.getOutboundCallRetrySeconds(), equalTo(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_SECONDS));
        assertThat(imiMobile.getCallAllowed(), equalTo(Constants.CONFIG_IMI_CAN_PLACE_OUTBOUND_CALL));
    }

    @Test
    public void shouldReturnTrueIfCheckedForExistenceOfValidConfig() {
        // When
        boolean exists = configService.hasConfig(Constants.CONFIG_VOXEO);
        // Then
        assertThat(exists, equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfCheckedForExistenceOfInvalidConfig() {
        // When
        boolean exists = configService.hasConfig(Constants.INVALID);
        // Then
        assertThat(exists, equalTo(false));
    }

    @Test
    public void shouldUpdateConfigsSuccessfully() throws IOException {
        // Given some changes to the first configuration
        configs.get(0).setName(Constants.CONFIG_VOXEO + Constants.UPDATED);

        // When
        configService.updateConfigs(configs);

        // Then
        List<Config> allConfigs = configService.allConfigs();
        assertThat(allConfigs.size(), equalTo(configs.size()));
        // The first one must be updated
        assertThat(allConfigs.get(0).getName(), equalTo(Constants.CONFIG_VOXEO + Constants.UPDATED));
        // The second one is not updated
        assertThat(allConfigs.get(1).getName(), equalTo(Constants.CONFIG_YO));
    }

    /* Renderers */

    @Test
    public void shouldGetRendererValidName() {
        // When
        Renderer vxml = configService.getRenderer(Constants.CONFIG_RENDERER_VXML);
        // Then
        assertNotNull(vxml);
        assertThat(vxml.getName(), equalTo(Constants.CONFIG_RENDERER_VXML));
        assertThat(vxml.getTemplate(), equalTo(Constants.CONFIG_RENDERER_VXML_TPL));
        assertThat(vxml.getMimeType(), equalTo(Constants.CONFIG_RENDERER_VXML_MIME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfTriedToRetrieveInvalidRenderer() {
        // When, Then
        configService.getRenderer(Constants.INVALID);
    }

    @Test
    public void shouldGetAllRenderers() {
        // When
        List<Renderer> allRenderers = configService.allRenderers();
        // Then
        assertNotNull(allRenderers);
        assertThat(allRenderers.size(), equalTo(2));
        Renderer vxml = allRenderers.get(0);
        Renderer txt = allRenderers.get(1);

        assertThat(vxml.getName(), equalTo(Constants.CONFIG_RENDERER_VXML));
        assertThat(vxml.getMimeType(), equalTo(Constants.CONFIG_RENDERER_VXML_MIME));
        assertThat(vxml.getTemplate(), equalTo(Constants.CONFIG_RENDERER_VXML_TPL));

        assertThat(txt.getName(), equalTo(Constants.CONFIG_RENDERER_TXT));
        assertThat(txt.getMimeType(), equalTo(Constants.CONFIG_RENDERER_TXT_MIME));
        assertThat(txt.getTemplate(), equalTo(Constants.CONFIG_RENDERER_TXT_TPL));
    }

    @Test
    public void shouldReturnTrueIfCheckedForExistenceOfValidRenderer() {
        // When
        boolean exists = configService.hasRenderer(Constants.CONFIG_RENDERER_VXML);
        // Then
        assertThat(exists, equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfCheckedForExistenceOfInvalidRenderer() {
        // When
        boolean exists = configService.hasRenderer(Constants.INVALID);
        // Then
        assertThat(exists, equalTo(false));
    }

    @Test
    public void shouldUpdateRenderersSuccessfully() throws IOException {
        // Given some changes to the first configuration
        renderers.get(0).setName(Constants.CONFIG_RENDERER_VXML + Constants.UPDATED);

        // When
        configService.updateRenderers(renderers);

        // Then
        List<Renderer> allRenderers = configService.allRenderers();
        assertThat(allRenderers.size(), equalTo(renderers.size()));
        // The first one must be updated
        assertThat(allRenderers.get(0).getName(), equalTo(Constants.CONFIG_RENDERER_VXML + Constants.UPDATED));
        // The second one is not updated
        assertThat(allRenderers.get(1).getName(), equalTo(Constants.CONFIG_RENDERER_TXT));
    }


}
