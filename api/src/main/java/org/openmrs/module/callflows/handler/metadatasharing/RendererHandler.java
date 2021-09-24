package org.openmrs.module.callflows.handler.metadatasharing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.service.ConfigService;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * The RendererHandler Class is a Handler which exposes {@link Renderer}s to be exported and imported by Metadata Sharing
 * module.
 * <p>
 * Bean configured in moduleApplicationContext.xml
 * </p>
 *
 * @implNote The Metadata Sharing handlers us an ID an UUID to read the objects to import and export.
 * Since Renderer has no ID or UUID and there is no validation for UUID, these values are generated as follows:
 * the ID is equal to hashCode of the name; the UUID is equal to the name.
 */
@OpenmrsProfile(modules = {"metadatasharing:1.*"})
public class RendererHandler extends BaseSettingsItemHandler<Renderer> {

    public RendererHandler() {
        super("CallFlow Renderer", Renderer.class, Renderer::getName,
                () -> Context.getService(ConfigService.class).allRenderers());
    }

    @Override
    public Map<String, Object> getProperties(Renderer renderer) {
        return singletonMap("Mime type", renderer.getMimeType());
    }

    @Override
    public Renderer saveItem(Renderer renderer) throws DAOException {
        final ConfigService configService = Context.getService(ConfigService.class);
        final List<Renderer> allRenderers = configService.allRenderers();

        final Renderer savedItem;

        if (configService.hasRenderer(renderer.getName())) {
            // We trust this is the same object as in allRenderers
            final Renderer currentState = configService.getRenderer(renderer.getName());
            currentState.setMimeType(renderer.getMimeType());
            currentState.setTemplate(renderer.getTemplate());
            savedItem = currentState;
        } else {
            allRenderers.add(renderer);
            savedItem = renderer;
        }

        // Writes to file
        configService.updateRenderers(allRenderers);
        return savedItem;
    }
}
