package org.openmrs.module.callflows.handler.metadatasharing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class RendererHandler
        implements MetadataTypesHandler<Renderer>, MetadataSearchHandler<Renderer>, MetadataSaveHandler<Renderer>,
        MetadataPropertiesHandler<Renderer> {
  private final Map<Class<? extends Renderer>, String> types;

  public RendererHandler() {
    final Map<Class<? extends Renderer>, String> tmpTypes = new HashMap<>();
    tmpTypes.put(Renderer.class, "CallFlow Renderer");
    this.types = Collections.unmodifiableMap(tmpTypes);
  }

  @Override
  public Integer getId(Renderer renderer) {
    return renderer.getName().hashCode();
  }

  @Override
  public void setId(Renderer renderer, Integer integer) {
    // Nothing to do
  }

  @Override
  public String getUuid(Renderer renderer) {
    return renderer.getName();
  }

  @Override
  public void setUuid(Renderer renderer, String s) {
    // Nothing to do
  }

  @Override
  public Boolean getRetired(Renderer renderer) {
    return Boolean.FALSE;
  }

  @Override
  public void setRetired(Renderer renderer, Boolean aBoolean) {
    // Nothing to do
  }

  @Override
  public String getName(Renderer renderer) {
    return renderer.getName();
  }

  @Override
  public String getDescription(Renderer renderer) {
    return null;
  }

  @Override
  public Date getDateChanged(Renderer renderer) {
    return null;
  }

  @Override
  public Map<String, Object> getProperties(Renderer renderer) {
    return Collections.singletonMap("Mime type", renderer.getMimeType());
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

  @Override
  public int getItemsCount(Class<? extends Renderer> type, boolean includeRetired, String phrase) throws DAOException {
    return Context.getService(ConfigService.class).allRenderers().size();
  }

  @Override
  public List<Renderer> getItems(Class<? extends Renderer> type, boolean includeRetired, String phrase, Integer firstResult,
                                 Integer maxResults) throws DAOException {
    final List<Renderer> allRenderers = Context.getService(ConfigService.class).allRenderers();

    final int start = firstResult == null ? 0 : firstResult;
    final int end = maxResults == null ? allRenderers.size() : Math.min(allRenderers.size(), start + maxResults);

    return allRenderers.subList(start, end);
  }

  @Override
  public Renderer getItemByUuid(Class<? extends Renderer> type, String uuid) throws DAOException {
    final List<Renderer> allRenderers = Context.getService(ConfigService.class).allRenderers();

    for (final Renderer renderer : allRenderers) {
      if (renderer.getName().equals(uuid)) {
        return renderer;
      }
    }

    return null;
  }

  @Override
  public Renderer getItemById(Class<? extends Renderer> type, Integer id) throws DAOException {
    final List<Renderer> allRenderers = Context.getService(ConfigService.class).allRenderers();

    for (final Renderer renderer : allRenderers) {
      if (renderer.getName().hashCode() == id) {
        return renderer;
      }
    }

    return null;
  }

  @Override
  public Map<Class<? extends Renderer>, String> getTypes() {
    return types;
  }

  @Override
  public int getPriority() {
    // Used to choose Handler in case of multiple for the same types
    return 0;
  }
}
