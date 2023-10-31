/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.data;

import static com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationEntityNotSelectedException;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.zkoss.lang.Strings;

import com.google.common.base.Preconditions;
import com.hybris.cockpitng.util.notifications.NotificationService;

/**
 * Class that encapsulates a map where the keys are integration object codes and the values are sets of the root item's
 * selected {@link ItemModel} instances. Represents what instances are selected in the export configuration editor. Can generate
 * {@link ConfigurationBundleEntity} from the map.
 */
public class ExportConfigurationEditorPresentation implements Serializable
{
	private static final long serialVersionUID = -4866987764422909585L;
	private static final String NOTIFICATION_EVENT_SOURCE = Strings.EMPTY;
	private static final String NOTIFICATION_EVENT_TYPE = IntegrationbackofficeConstants.NOTIFICATION_TYPE;
	private static final Level NOTIFICATION_EVENT_LEVEL = Level.WARNING;

	private final transient NotificationService notificationService;

	private final Map<String, Set<ItemModel>> entityInstancesMap;
	private IntegrationObjectModel selectedEntity;

	/**
	 * Instantiate this export configuration presentation.
	 *
	 * @param notificationService service to display notifications in backoffice.
	 */
	public ExportConfigurationEditorPresentation(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
		entityInstancesMap = new HashMap<>();
	}

	/**
	 * Get currently selected entity.
	 *
	 * @return the {@link IntegrationObjectModel} representing the selected entity,
	 * or {@code null} if no entity has been selected yet.
	 * @see #setSelectedEntity(IntegrationObjectModel)
	 */
	public IntegrationObjectModel getSelectedEntity()
	{
		if (selectedEntity == null)
		{
			notificationService.notifyUser(NOTIFICATION_EVENT_SOURCE, NOTIFICATION_EVENT_TYPE, NOTIFICATION_EVENT_LEVEL,
					new ExportConfigurationEntityNotSelectedException());
		}
		return selectedEntity;
	}

	/**
	 * Determine if the given {@link IntegrationObjectModel} equals {@link #selectedEntity}
	 */
	public boolean isSelectedEntity(final IntegrationObjectModel integrationObjectModel)
	{
		return selectedEntity != null && selectedEntity == integrationObjectModel;
	}

	/**
	 * Set currently selected entity.
	 *
	 * @param selectedEntity the {@link IntegrationObjectModel} representing the selected entity. Can't be {@code null}.
	 */
	public void setSelectedEntity(@NotNull final IntegrationObjectModel selectedEntity)
	{
		Preconditions.checkNotNull(selectedEntity, "Selected entity can't be null.");
		this.selectedEntity = selectedEntity;
		if (!entityInstancesMap.containsKey(selectedEntity.getCode()))
		{
			entityInstancesMap.put(selectedEntity.getCode(), new HashSet<>());
		}
	}

	/**
	 * Get selected entity's root type code. The selected entity must be set first.
	 *
	 * @return the code of the selected entity's root item type, or an empty string if no entity has been selected yet.
	 * @see #setSelectedEntity(IntegrationObjectModel)
	 */
	public String getSelectedEntityRoot()
	{
		if (selectedEntity != null)
		{
			return selectedEntity.getRootItem().getType().getCode();
		}
		else
		{
			notificationService.notifyUser(NOTIFICATION_EVENT_SOURCE, NOTIFICATION_EVENT_TYPE, NOTIFICATION_EVENT_LEVEL,
					new ExportConfigurationEntityNotSelectedException());
			return "";
		}
	}

	/**
	 * Get a copy of the set of selected instances for currently selected entity. The selected entity must be set first.
	 *
	 * @return a set of {@link ItemModel} representing selected entity's selected instances,
	 * or an empty set if no entity has been selected yet.
	 * @see #setSelectedEntity(IntegrationObjectModel)
	 */
	public Set<ItemModel> getSelectedEntityInstances()
	{
		if (selectedEntity != null)
		{
			return new HashSet<>(entityInstancesMap.get(selectedEntity.getCode()));
		}
		else
		{
			notificationService.notifyUser(NOTIFICATION_EVENT_SOURCE, NOTIFICATION_EVENT_TYPE, NOTIFICATION_EVENT_LEVEL,
					new ExportConfigurationEntityNotSelectedException());
			return new HashSet<>();
		}
	}

	/**
	 * Update the set of selected instances with a copy of the argument for currently selected entity. The selected entity must be set first.
	 *
	 * @param selectedEntityInstances the updated set of {@link ItemModel}
	 * @see #setSelectedEntity(IntegrationObjectModel)
	 */
	public void setSelectedEntityInstances(final Set<ItemModel> selectedEntityInstances)
	{
		if (selectedEntity != null)
		{
			final String selectedEntityCode = selectedEntity.getCode();
			final Set<ItemModel> entityInstances = selectedEntityInstances != null
					? new HashSet<>(selectedEntityInstances)
					: new HashSet<>();
			entityInstancesMap.put(selectedEntityCode, entityInstances);
		}
		else
		{
			notificationService.notifyUser(NOTIFICATION_EVENT_SOURCE, NOTIFICATION_EVENT_TYPE, NOTIFICATION_EVENT_LEVEL,
					new ExportConfigurationEntityNotSelectedException());
		}
	}

	/**
	 * Clear the encapsulated map of entity instances and set selected entity to {@code null}.
	 */
	public void clearSelection()
	{
		entityInstancesMap.clear();
		selectedEntity = null;
	}

	/**
	 * Check if map has any entries with a non-empty set of selected instances.
	 *
	 * @return if any instance selected
	 */
	public boolean isAnyInstanceSelected()
	{
		return !entityInstancesMap.isEmpty() && entityInstancesMap.values().stream().anyMatch(set -> !set.isEmpty());
	}

	/**
	 * Copy the map of selected entity instances, remove entries with no selected instances, and generate a
	 * {@link ConfigurationBundleEntity}.
	 *
	 * @return a {@link ConfigurationBundleEntity} ready for export
	 */
	public ConfigurationBundleEntity generateConfigurationBundleEntity()
	{
		final Map<String, Set<ItemModel>> mapCopy =
				entityInstancesMap.entrySet()
				                  .stream()
				                  .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue())));
		mapCopy.entrySet().removeIf(entry -> entry.getValue().isEmpty());

		final ConfigurationBundleEntity configurationBundleEntity = new ConfigurationBundleEntity();
		final Set<IntegrationObjectBundleEntity> integrationObjectBundles = generateIntegrationObjectBundleEntities(mapCopy);
		configurationBundleEntity.setIntegrationObjectBundles(integrationObjectBundles);
		return configurationBundleEntity;
	}

	/**
	 * Retrieves the current number of selected instances for a given entity type. Returns 0 if entry not present in map.
	 * The code lookup is based off of the {@link IntegrationObjectModel}'s code.
	 *
	 * @param entityType Entity type to retrieve count on
	 * @return Number of selected instances
	 */
	public int getSelectedInstancesCountForEntity(final String entityType)
	{
		return entityInstancesMap.get(entityType) != null ? entityInstancesMap.get(entityType).size() : 0;
	}

	private Set<IntegrationObjectBundleEntity> generateIntegrationObjectBundleEntities(final Map<String, Set<ItemModel>> mapCopy)
	{
		return mapCopy.entrySet()
		              .stream()
		              .map(this::generateIntegrationObjectBundleEntity)
		              .collect(Collectors.toSet());
	}

	private IntegrationObjectBundleEntity generateIntegrationObjectBundleEntity(final Map.Entry<String, Set<ItemModel>> entry)
	{
		final Set<String> instancePks = entry.getValue()
		                                     .stream()
		                                     .map(itemModel -> itemModel.getPk().toString())
		                                     .collect(Collectors.toSet());
		final IntegrationObjectBundleEntity bundle = new IntegrationObjectBundleEntity();
		bundle.setIntegrationObjectCode(entry.getKey());
		bundle.setRootItemInstancePks(instancePks);
		return bundle;
	}
}
