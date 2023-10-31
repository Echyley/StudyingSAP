/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A service that provides convenience methods to interact with {@link WebhookConfigurationModel}s.
 */
public interface WebhookConfigurationService
{
	/**
	 * Find WebhookConfigurations that are associated to the given abstract event, and have the Integration Object root item
	 * type the same as the given item type
	 *
	 * @param event An AbstractEvent to search for
	 * @param item  Search Integration Object root items matching the item type
	 * @return A collection of WebhookConfigurationModels if found, otherwise an empty collection
	 */
	Collection<WebhookConfigurationModel> getWebhookConfigurationsByEventAndItemModel(AbstractEvent event, ItemModel item);


	/**
	 * Find WebhookConfigurations that are associated to the given webhook event, and have the Integration Object root item
	 * with the same type as the given item type.
	 *
	 * @param event A WebhookEvent to search for
	 * @param item  Search Integration Object root items matching the item type
	 * @return A collection of WebhookConfigurationModels if found, otherwise an empty collection
	 */
	default Collection<WebhookConfigurationModel> findByEventAndItemMatchingRootItem(final WebhookEvent event,
	                                                                                 final ItemModel item)
	{
		return Collections.emptyList();
	}

	/**
	 * Find WebhookConfigurations that are associated to the given webhook event, and have an Integration Object Item
	 * with the same type as the given item type
	 *
	 * @param event A WebhookEvent to search for
	 * @param item  Search Integration Object root items matching the item type
	 * @return A collection of WebhookConfigurationModels if found, otherwise an empty collection
	 */
	default Collection<WebhookConfigurationModel> findByEventAndItemMatchingAnyItem(final WebhookEvent event,
	                                                                                final ItemModel item)
	{
		return Collections.emptyList();
	}

	/**
	 * Find WebhookConfiguration by a primary key
	 *
	 * @param pk the primary key of the WebhookConfiguration
	 * @return {@link WebhookConfigurationModel} if found otherwise {@link Optional#empty()}
	 */
	default Optional<WebhookConfigurationModel> findWebhookConfigurationByPk(final PK pk)
	{
		return Optional.empty();
	}
}
