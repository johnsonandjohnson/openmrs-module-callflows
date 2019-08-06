package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Renderer;

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
