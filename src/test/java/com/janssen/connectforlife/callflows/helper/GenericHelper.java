package com.janssen.connectforlife.callflows.helper;

import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Renderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic Helper to build oft-used objects across various other helpers
 *
 * @author bramak09
 */
public final class GenericHelper {

    // private constructor
    private GenericHelper() {
    }

    public static Map<String, String> buildServicesMap() {
        // OSGI Services
        Map<String, String> servicesMap = new HashMap<>();
        servicesMap.put(Constants.CONFIG_SRVC_CALL, Constants.CONFIG_SRVC_CALL_CLASS);
        return servicesMap;
    }

    public static Map<String, String> buildTestUsersMap(String phone, String url) {
        // Test Users map
        Map<String, String> testUsersMap = new HashMap<>();
        testUsersMap.put(phone, url);
        return testUsersMap;
    }

    public static Map<String, Renderer> buildRenderersMap() {
        // Renderers
        Renderer vxmlRenderer = new Renderer();
        vxmlRenderer.setName(Constants.CONFIG_RENDERER_VXML);
        vxmlRenderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        vxmlRenderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);

        Renderer textRenderer = new Renderer();
        textRenderer.setName(Constants.CONFIG_RENDERER_TXT);
        textRenderer.setMimeType(Constants.CONFIG_RENDERER_TXT_MIME);
        textRenderer.setTemplate(Constants.CONFIG_RENDERER_TXT_TPL);

        // Renderer Map
        Map<String, Renderer> rendererMap = new HashMap<>();
        rendererMap.put(Constants.CONFIG_RENDERER_VXML, vxmlRenderer);
        rendererMap.put(Constants.CONFIG_RENDERER_TXT, textRenderer);
        return rendererMap;
    }

    public static Map<String, RendererContract> buildRenderersContractMap() {
        // Renderer contract
        RendererContract vxmlRenderer = new RendererContract();
        vxmlRenderer.setName(Constants.CONFIG_RENDERER_VXML);
        vxmlRenderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        vxmlRenderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);

        RendererContract textRenderer = new RendererContract();
        textRenderer.setName(Constants.CONFIG_RENDERER_TXT);
        textRenderer.setMimeType(Constants.CONFIG_RENDERER_TXT_MIME);
        textRenderer.setTemplate(Constants.CONFIG_RENDERER_TXT_TPL);

        // Renderer Map
        Map<String, RendererContract> rendererMap = new HashMap<>();
        rendererMap.put(Constants.CONFIG_RENDERER_VXML, vxmlRenderer);
        rendererMap.put(Constants.CONFIG_RENDERER_TXT, textRenderer);
        return rendererMap;
    }

}

