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

import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Renderer;

/**
 * Renderer Domain Builder
 *
 * @author bramak09
 */
public final class RendererBuilder {

    /**
     * Create a Renderer Domain object from a RendererContract object
     *
     * @param rendererContract the object to convert
     * @return a new Renderer object
     */
    public static Renderer createFrom(RendererContract rendererContract) {
        Renderer renderer = new Renderer();
        renderer.setName(rendererContract.getName());
        renderer.setTemplate(rendererContract.getTemplate());
        renderer.setMimeType(rendererContract.getMimeType());
        return renderer;
    }

    private RendererBuilder() {
    }
}

