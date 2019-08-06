package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Renderer;

import org.springframework.stereotype.Component;

/**
 * Renderer Contract Builder
 *
 * @author bramak09
 */
@Component
public class RendererContractBuilder {

    /**
     * Create a Renderer Contract from a Renderer domain object
     *
     * @param renderer the domain
     * @return a renderer contract
     */
    public RendererContract createFrom(Renderer renderer) {
        RendererContract rendererContract = new RendererContract();
        rendererContract.setName(renderer.getName());
        rendererContract.setTemplate(renderer.getTemplate());
        rendererContract.setMimeType(renderer.getMimeType());
        return rendererContract;
    }
}

