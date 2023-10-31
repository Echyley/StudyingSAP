/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link WebhookConfigurationService}
 */
public class DefaultWebhookConfigurationService implements WebhookConfigurationService
{
	private static final String FIND_BY_EVENT_QUERY = " SELECT DISTINCT {" + ItemModel.PK + "}" +
			" FROM {" + WebhookConfigurationModel._TYPECODE + "}" +
			" WHERE {" + WebhookConfigurationModel.EVENTTYPE + "}=?eventClass";

	private static final String FIND_WEBHOOK_CONFIGURATION_BY_PK_QUERY = "SELECT DISTINCT {" + ItemModel.PK + "}" +
			" FROM {" + WebhookConfigurationModel._TYPECODE + "}" +
			" WHERE {" + ItemModel.PK + "}=?" + ItemModel.PK;

	private final FlexibleSearchService flexibleSearchService;
	private final DescriptorFactory descriptorFactory;
	private final ItemModelSearchService itemModelSearchService;

	/**
	 * Instantiates the {@link DefaultWebhookConfigurationService}
	 *
	 * @param flexibleSearchService Service for searching {@link ItemModel}s
	 * @param descriptorFactory     DescriptorFactory to create descriptors
	 * @deprecated use new constructor instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultWebhookConfigurationService(@NotNull final FlexibleSearchService flexibleSearchService,
	                                          @NotNull final DescriptorFactory descriptorFactory)
	{
		this(flexibleSearchService, descriptorFactory, Registry.getApplicationContext().getBean("itemModelSearchService", ItemModelSearchService.class));
	}

	/**
	 * Instantiates the {@link DefaultWebhookConfigurationService}
	 *
	 * @param flexibleSearchService Service for searching {@link ItemModel}s
	 * @param descriptorFactory     DescriptorFactory to create descriptors
	 * @param itemSearchService     ItemModelSearchService to search for webhookConfigurations without caching.
	 */
	public DefaultWebhookConfigurationService(@NotNull final FlexibleSearchService flexibleSearchService,
	                                          @NotNull final DescriptorFactory descriptorFactory,
	                                          @NotNull final ItemModelSearchService itemSearchService)
	{
		Preconditions.checkArgument(flexibleSearchService != null, "FlexibleSearchService cannot be null");
		Preconditions.checkArgument(descriptorFactory != null, "DescriptorFactory cannot be null");
		Preconditions.checkArgument(itemSearchService != null, "ItemModelSearchService cannot be null");

		this.flexibleSearchService = flexibleSearchService;
		this.descriptorFactory = descriptorFactory;
		this.itemModelSearchService = itemSearchService;
	}

	@Override
	public Collection<WebhookConfigurationModel> getWebhookConfigurationsByEventAndItemModel(final AbstractEvent event,
	                                                                                         final ItemModel item)
	{
		return findByEventAndItem(event, item, true);
	}

	@Override
	public Collection<WebhookConfigurationModel> findByEventAndItemMatchingRootItem(final WebhookEvent event,
	                                                                                final ItemModel item)
	{
		return findByEventAndItem(event, item, true);
	}

	@Override
	public Collection<WebhookConfigurationModel> findByEventAndItemMatchingAnyItem(final WebhookEvent event,
	                                                                               final ItemModel item)
	{
		return findByEventAndItem(event, item, false);
	}

	@Override
	public Optional<WebhookConfigurationModel> findWebhookConfigurationByPk(final PK pk)
	{
		final var query = new FlexibleSearchQuery(FIND_WEBHOOK_CONFIGURATION_BY_PK_QUERY);
		query.addQueryParameter(ItemModel.PK, pk);
		final SearchResult<WebhookConfigurationModel> result = flexibleSearchService.search(query);
		return result.getResult().stream().findFirst();
	}

	private <T> Collection<WebhookConfigurationModel> findByEventAndItem(final T event, final ItemModel item,
	                                                                     final boolean onlyMatchRootItem)
	{
		if (event == null || item == null)
		{
			return Collections.emptyList();
		}

		final var configs = findWebhookConfigurations(
				Map.of("eventClass", event.getClass().getCanonicalName()));
		final Collection<WebhookConfigurationModel> webhookConfigurationModels = filterWebhookConfigurationsByItemModel(configs,
				item, onlyMatchRootItem);

		return findWebhookConfigurationsWithoutCaching(webhookConfigurationModels);
	}

	private Collection<WebhookConfigurationModel> findWebhookConfigurationsWithoutCaching(
			final Collection<WebhookConfigurationModel> webhookConfigurationModels)
	{
		return webhookConfigurationModels.stream()
		                                 .map(AbstractItemModel::getPk)
		                                 .map(itemModelSearchService::nonCachingFindByPk)
		                                 .filter(Optional::isPresent)
		                                 .map(optionalConfig -> (WebhookConfigurationModel) optionalConfig.get())
		                                 .collect(Collectors.toList());

	}

	private Collection<WebhookConfigurationModel> findWebhookConfigurations(final Map<String, Object> params)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BY_EVENT_QUERY);
		query.addQueryParameters(params);
		final SearchResult<WebhookConfigurationModel> result = flexibleSearchService.search(query);
		return result.getResult();
	}

	private Collection<WebhookConfigurationModel> filterWebhookConfigurationsByItemModel(
			final Collection<WebhookConfigurationModel> webhookConfigurations, final ItemModel item,
			final boolean onlyMatchRootItem)
	{
		return webhookConfigurations.stream()
		                            .filter(webhookConfiguration ->
				                            isMatchIntegrationObjectItemType(webhookConfiguration.getIntegrationObject(),
						                            item, onlyMatchRootItem))
		                            .collect(Collectors.toList());
	}

	private boolean isMatchIntegrationObjectItemType(final IntegrationObjectModel integrationObject, final ItemModel item,
	                                                 final boolean onlyMatchRootItem)
	{
		return onlyMatchRootItem ?
				isItemInTypeHierarchy(item, integrationObject.getRootItem()) :
				isItemInAnyTypeHierarchy(item, integrationObject);
	}

	private boolean isItemInTypeHierarchy(final ItemModel item, final IntegrationObjectItemModel intObjItem)
	{
		if (intObjItem != null)
		{
			final var typeDescriptor = descriptorFactory.createItemTypeDescriptor(intObjItem);
			return typeDescriptor.isInstance(item);
		}
		return false;
	}

	private boolean isItemInAnyTypeHierarchy(final ItemModel item, final IntegrationObjectModel integrationObject)
	{
		return integrationObject.getItems()
		                        .stream()
		                        .anyMatch(intObjItem ->
				                        isItemInTypeHierarchy(item, intObjItem));
	}
}
