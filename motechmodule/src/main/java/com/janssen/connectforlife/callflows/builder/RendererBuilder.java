package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Renderer;

import org.springframework.stereotype.Component;

/**
 * Renderer Domain Builder
 *
 * @author bramak09
 */
@Component
public class RendererBuilder {

    /**
     * Create a Renderer Domain object from a RendererContract object
     *
     * @param rendererContract the object to convert
     * @return a new Renderer object
     */
    public Renderer createFrom(RendererContract rendererContract) {
        Renderer renderer = new Renderer();
        renderer.setName(rendererContract.getName());
        renderer.setTemplate(rendererContract.getTemplate());
        renderer.setMimeType(rendererContract.getMimeType());
        return renderer;
    }

}

