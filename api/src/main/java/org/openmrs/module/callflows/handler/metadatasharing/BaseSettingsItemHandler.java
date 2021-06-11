package org.openmrs.module.callflows.handler.metadatasharing;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.callflows.api.domain.Settings;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.singletonMap;

/**
 * The BaseSettingsItemHandler Class.
 * <p>
 * The base class for Metadata Sharing handlers for items of the {@link Settings} configuration.
 * </p>
 */
abstract class BaseSettingsItemHandler<T>
        implements MetadataTypesHandler<T>, MetadataSearchHandler<T>, MetadataSaveHandler<T>, MetadataPropertiesHandler<T> {

    private final Map<Class<? extends T>, String> types;
    private final Function<T, String> nameGetter;
    private final Supplier<List<T>> allItemsGetter;

    BaseSettingsItemHandler(String itemLabel, Class<T> itemClass, Function<T, String> nameGetter,
                            Supplier<List<T>> allItemsGetter) {
        this.types = singletonMap(itemClass, itemLabel);
        this.nameGetter = nameGetter;
        this.allItemsGetter = allItemsGetter;
    }

    @Override
    public Map<Class<? extends T>, String> getTypes() {
        return types;
    }

    @Override
    public int getPriority() {
        // Used to choose Handler in case of multiple handlers for the same type
        return 0;
    }

    @Override
    public Integer getId(T item) {
        return nameGetter.apply(item).hashCode();
    }

    @Override
    public void setId(T item, Integer integer) {
        // Nothing to do
    }

    @Override
    public String getUuid(T item) {
        return nameGetter.apply(item);
    }

    @Override
    public void setUuid(T item, String s) {
        // Nothing to do
    }

    @Override
    public Boolean getRetired(T item) {
        return Boolean.FALSE;
    }

    @Override
    public void setRetired(T item, Boolean aBoolean) {
        // Nothing to do
    }

    @Override
    public String getName(T item) {
        return nameGetter.apply(item);
    }

    @Override
    public String getDescription(T item) {
        return null;
    }

    @Override
    public Date getDateChanged(T item) {
        return null;
    }

    @Override
    public int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws DAOException {
        return allItemsGetter.get().size();
    }

    @Override
    public List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult,
                            Integer maxResults) throws DAOException {
        final List<T> allItems = allItemsGetter.get();

        final int start = firstResult == null ? 0 : firstResult;
        final int end = maxResults == null ? allItems.size() : Math.min(allItems.size(), start + maxResults);

        return allItems.subList(start, end);
    }

    @Override
    public T getItemByUuid(Class<? extends T> type, String uuid) throws DAOException {
        final List<T> allItems = allItemsGetter.get();

        for (final T item : allItems) {
            if (nameGetter.apply(item).equals(uuid)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public T getItemById(Class<? extends T> type, Integer id) throws DAOException {
        final List<T> allItems = allItemsGetter.get();

        for (final T item : allItems) {
            if (nameGetter.apply(item).hashCode() == id) {
                return item;
            }
        }

        return null;
    }
}
