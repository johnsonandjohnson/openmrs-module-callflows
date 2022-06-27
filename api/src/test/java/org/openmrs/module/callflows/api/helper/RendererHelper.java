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

import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Renderer Helper
 *
 * @author bramak09
 */
public final class RendererHelper {

    // Utility class, hence private
    private RendererHelper() {
    }

    public static List<Renderer> createRenderers() {
        List<Renderer> rendererList = new ArrayList<>();

        // Voice XML Renderer
        Renderer vxmlRenderer = new Renderer();
        vxmlRenderer.setName(Constants.CONFIG_RENDERER_VXML);
        vxmlRenderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        vxmlRenderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);

        // Textual Renderer
        Renderer textRenderer = new Renderer();
        textRenderer.setName(Constants.CONFIG_RENDERER_TXT);
        textRenderer.setMimeType(Constants.CONFIG_RENDERER_TXT_MIME);
        textRenderer.setTemplate(Constants.CONFIG_RENDERER_TXT_TPL);

        rendererList.add(vxmlRenderer);
        rendererList.add(textRenderer);

        return rendererList;
    }

    public static List<RendererContract> createRendererContracts() {
        List<RendererContract> rendererList = new ArrayList<>();

        // Voice XML Renderer
        RendererContract vxmlRenderer = new RendererContract();
        vxmlRenderer.setName(Constants.CONFIG_RENDERER_VXML);
        vxmlRenderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        vxmlRenderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);

        // Textual Renderer
        RendererContract textRenderer = new RendererContract();
        textRenderer.setName(Constants.CONFIG_RENDERER_TXT);
        textRenderer.setMimeType(Constants.CONFIG_RENDERER_TXT_MIME);
        textRenderer.setTemplate(Constants.CONFIG_RENDERER_TXT_TPL);

        rendererList.add(vxmlRenderer);
        rendererList.add(textRenderer);

        return rendererList;

    }
}
