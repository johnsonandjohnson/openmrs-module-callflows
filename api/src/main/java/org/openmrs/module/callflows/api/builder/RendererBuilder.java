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

