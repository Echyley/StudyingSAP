/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event.impl;


import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.search.RootItemSearchService;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.webhookservices.event.ItemCreatedEvent;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.event.WebhookEventFactory;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import io.vavr.collection.Stream;

/**
 * Default implementation of {@link WebhookEventFactory} to create list of {@link WebhookEvent}
 */
public class DefaultWebhookEventFactory implements WebhookEventFactory
{
	private static final Collection<Integer> SUPPORTED_EVENT_TYPES = List.of(AfterSaveEvent.CREATE, AfterSaveEvent.UPDATE, AfterSaveEvent.REMOVE);
	private WebhookConfigurationService webhookConfigurationService;
	private ItemModelSearchService itemModelSearchService;
	private RootItemSearchService rootItemSearchService;

	@Override
	public List<WebhookEvent> create(final AfterSaveEvent event)
	{
		if (canCreateWebhookEvent(event))
		{
			return itemModelSearchService.nonCachingFindByPk(event.getPk())
			                             .map(it -> deriveAllEventsForItem(event, it))
			                             .orElse(Collections.emptyList());
		}
		return Collections.emptyList();
	}

	private List<WebhookEvent> deriveAllEventsForItem(final AfterSaveEvent event, final ItemModel originalItem)
	{
		return Stream.concat(createEventsForItem(event, originalItem),
				             createEventsForRootItemsOf(originalItem))
		             .collect(Collectors.toList());
	}

	/*
	 * Creates WebhookEvents for parents of the originalItem and filters out created
	 * WebhookEvents based off whether there is a WebhookConfiguration for an IntegrationObject with
	 * the same root item type as the parent's ItemModel type. If there is a matching WebhookConfiguration
	 * then the WebhookEvent is eligible to be sent.
	 */
	private List<WebhookEvent> createEventsForRootItemsOf(final ItemModel originalItem)
	{
		final List<WebhookEvent> eventsToFilterWithWebhookConfigs = convertToUpdatedEvents(
				new AfterSaveEvent(originalItem.getPk(), AfterSaveEvent.UPDATE));
		return eventsToFilterWithWebhookConfigs.stream()
		                                       .map(webhookEvent -> createWebhookEventsWithRootPkIfEventHasWebhookConfig(
				                                       webhookEvent, originalItem))
		                                       .flatMap(Collection::stream)
		                                       .collect(Collectors.toList());
	}

	private Collection<WebhookEvent> createWebhookEventsWithRootPkIfEventHasWebhookConfig(final WebhookEvent event,
	                                                                                      final ItemModel item)
	{
		final List<ItemModel> roots = findRootsForItemWithWebhookConfigs(event, item);
		return roots.stream()
		            .map(rootItem -> createUpdateEventWithRootPkFrom(event, rootItem))
		            .filter(Optional::isPresent)
		            .map(Optional::get)
		            .collect(Collectors.toList());

	}

	private List<ItemModel> findRootsForItemWithWebhookConfigs(final WebhookEvent event, final ItemModel item)
	{
		final Collection<WebhookConfigurationModel> webhookConfigs = webhookConfigurationService.findByEventAndItemMatchingAnyItem(
				event, item);
		if (isWebhookConfigurationExists(webhookConfigs))
		{
			return webhookConfigs.stream()
			                     .flatMap(webhookConfig -> findRootsForItemFromIntegrationObject(item,
					                     webhookConfig.getIntegrationObject()).stream())
			                     .collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private Collection<ItemModel> findRootsForItemFromIntegrationObject(final ItemModel item,
	                                                                    final IntegrationObjectModel integrationObject)
	{
		return rootItemSearchService.findRoots(item, integrationObject)
		                            .getRootItems()
		                            .stream()
		                            .filter(itemModel -> !itemModel.equals(item))
		                            .collect(Collectors.toList());
	}

	private Optional<WebhookEvent> createUpdateEventWithRootPkFrom(final WebhookEvent event, final ItemModel rootItem)
	{
		if (event instanceof ItemSavedEvent)
		{
			return Optional.of(new ItemSavedEvent(new AfterSaveEvent(rootItem.getPk(), AfterSaveEvent.UPDATE), true));
		}
		else if (event instanceof ItemUpdatedEvent)
		{
			return Optional.of(new ItemUpdatedEvent(new AfterSaveEvent(rootItem.getPk(), AfterSaveEvent.UPDATE), true));
		}
		return Optional.empty();
	}

	/*
	 * Creates WebhookEvents for the item that the AfterSaveEvent was captured for. Filters out created
	 * WebhookEvents based off whether there is a WebhookConfiguration for an IntegrationObject with
	 * the same root item type as the ItemModel type.
	 */
	private List<WebhookEvent> createEventsForItem(final AfterSaveEvent event, final ItemModel item)
	{
		return convertToWebhookEvents(event)
				.stream()
				.filter(webhookEvent ->
						isWebhookConfigurationExists(
								webhookConfigurationService.findByEventAndItemMatchingRootItem(webhookEvent, item)))
				.collect(Collectors.toList());
	}

	private boolean isWebhookConfigurationExists(final Collection<WebhookConfigurationModel> webhookConfigurations)
	{
		return CollectionUtils.isNotEmpty(webhookConfigurations);
	}

	private List<WebhookEvent> convertToCreatedEvents(final AfterSaveEvent event)
	{
		return List.of(new ItemCreatedEvent(event), new ItemSavedEvent(event));
	}

	private List<WebhookEvent> convertToUpdatedEvents(final AfterSaveEvent event)
	{
		return List.of(new ItemUpdatedEvent(event), new ItemSavedEvent(event));
	}

	private List<WebhookEvent> convertToDeletedEvents(final AfterSaveEvent event)
	{
		return List.of(new ItemDeletedEvent(event));
	}

	private List<WebhookEvent> convertToWebhookEvents(final AfterSaveEvent event)
	{
		final List<WebhookEvent> eventsToFilter;
		switch (event.getType())
		{
			case AfterSaveEvent.CREATE:
				eventsToFilter = convertToCreatedEvents(event);
				break;
			case AfterSaveEvent.UPDATE:
				eventsToFilter = convertToUpdatedEvents(event);
				break;
			case AfterSaveEvent.REMOVE:
				eventsToFilter = convertToDeletedEvents(event);
				break;
			default:
				eventsToFilter = Collections.emptyList();
		}
		return eventsToFilter;
	}

	private boolean canCreateWebhookEvent(final AfterSaveEvent event)
	{
		return event != null
				&& SUPPORTED_EVENT_TYPES.contains(event.getType())
				&& webhookConfigurationService != null
				&& itemModelSearchService != null
				&& rootItemSearchService != null;
	}

	public void setWebhookConfigurationService(final WebhookConfigurationService webhookConfigurationService)
	{
		this.webhookConfigurationService = webhookConfigurationService;
	}

	public void setItemModelSearchService(final ItemModelSearchService itemModelSearchService)
	{
		this.itemModelSearchService = itemModelSearchService;
	}

	public void setRootItemSearchService(final RootItemSearchService rootItemSearchService)
	{
		this.rootItemSearchService = rootItemSearchService;
	}
}
