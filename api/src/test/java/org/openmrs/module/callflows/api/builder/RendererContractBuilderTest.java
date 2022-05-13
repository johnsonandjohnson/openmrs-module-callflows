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
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Renderer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Renderer Contract Builder Test
 *
 * @author bramak09
 */
public class RendererContractBuilderTest extends BaseTest {

    private Renderer renderer;

    @Before
    public void setUp() {
        // Given
        renderer = new Renderer();
        renderer.setName(Constants.CONFIG_RENDERER_VXML);
        renderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        renderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);
    }

    @Test
    public void shouldBuildRendererContract() {
        // When
        RendererContract rendererContract = RendererContractBuilder.createFrom(renderer);

        // Then
        assertThat(rendererContract.getName(), equalTo(renderer.getName()));
        assertThat(rendererContract.getMimeType(), equalTo(renderer.getMimeType()));
        assertThat(rendererContract.getTemplate(), equalTo(renderer.getTemplate()));
    }
}

