/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.router.impl;

import static java.util.Collections.singletonList;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.RootItemSearchResult;
import de.hybris.platform.integrationservices.search.RootItemSearchService;
import de.hybris.platform.outboundsync.dto.OutboundItem;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder;
import de.hybris.platform.outboundsync.job.ItemPKPopulator;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class RootItemPKPopulator implements ItemPKPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(RootItemPKPopulator.class);

	private OutboundItemFactory itemFactory;
	private RootItemSearchService rootItemSearchService;

	@Override
	public List<OutboundItemDTO> populatePK(final OutboundItemDTO itemDto)
	{
		final OutboundItem outboundItem = itemFactory.createItem(itemDto);
		return outboundItem.getChangedItemModel()
				.map(itemModel -> derivePkFromChangedItem(itemDto, outboundItem, itemModel))
				.orElseGet(() -> handleNoChangedItem(itemDto, outboundItem));
	}

	private List<OutboundItemDTO> handleNoChangedItem(final OutboundItemDTO itemDto, final OutboundItem outboundItem)
	{
		LOG.warn("Cannot find item model for PK {} and change DTO {} for outbound sync. Item may have been deleted already",
				outboundItem.getItem().getPK(), itemDto);
		return singletonList(itemDto);
	}

	private List<OutboundItemDTO> derivePkFromChangedItem(final OutboundItemDTO itemDto, final OutboundItem outboundItem, final ItemModel itemModel)
	{
		if (outboundItem.getIntegrationObject().getRootType().isEmpty())
		{
			return singletonList(populateWithOwnPK(itemDto));
		}
		return outboundItem.getTypeDescriptor()
				.map(type -> populateWithRootPK(itemDto, itemModel, type))
				.orElseGet(() -> handleItemIsNotInIntegrationObject(itemDto, outboundItem, itemModel));
	}

	private List<OutboundItemDTO> handleItemIsNotInIntegrationObject(final OutboundItemDTO itemDto, final OutboundItem outboundItem, final ItemModel itemModel)
	{
		LOG.warn("No IntegrationObjectItem is defined for '{}' on '{}'. Please check your Integration Object is defined correctly.",
				itemModel.getItemtype(), outboundItem.getIntegrationObject().getCode());
		return singletonList(itemDto);
	}

	private OutboundItemDTO populateWithOwnPK(final OutboundItemDTO itemDto)
	{
		LOG.debug("Populating item with own PK in {}", itemDto);
		return OutboundItemDTOBuilder.from(itemDto).withRootItem(itemDto.getItem()).build();
	}

	private List<OutboundItemDTO> populateWithRootPK(final OutboundItemDTO dto, final ItemModel item, final TypeDescriptor typeDescriptor)
	{
		final RootItemSearchResult result = getRootItemSearchService().findRoots(item, typeDescriptor);
		return result.hasAnyObjectInRefPathExecutionResult() ?
				deriveRootItems(dto, item, result.getRootItems()) :
				handleNoParentReferences(dto, item);
	}

	private List<OutboundItemDTO> handleNoParentReferences(final OutboundItemDTO dto, final ItemModel item)
	{
		LOG.debug("No root item reference exists on: {}", item);
		return singletonList(dto);
	}

	private List<OutboundItemDTO> deriveRootItems(final OutboundItemDTO dto, final ItemModel item, final Collection<ItemModel> parentItems)
	{
		return parentItems.isEmpty() ?
				handleNoRootItem(dto, item) :
				parentItems.stream()
				           .map(parentItem -> OutboundItemDTOBuilder.from(dto).withRootItemPK(parentItem.getPk().getLong()).build())
				           .collect(Collectors.toList());
	}

	private List<OutboundItemDTO> handleNoRootItem(final OutboundItemDTO dto, final ItemModel item)
	{
		LOG.debug("Root item is null for: {}", item);
		return Collections.singletonList(OutboundItemDTOBuilder.from(dto).withRootItemPK(item.getPk().getLong()).build());
	}

	protected OutboundItemFactory getItemFactory()
	{
		return itemFactory;
	}

	@Required
	public void setItemFactory(final OutboundItemFactory factory)
	{
		itemFactory = factory;
	}

	public void setRootItemSearchService(final RootItemSearchService rootItemSearchService)
	{
		this.rootItemSearchService = rootItemSearchService;
	}

	private RootItemSearchService getRootItemSearchService()
	{
		if (rootItemSearchService == null)
		{
			rootItemSearchService = Registry.getApplicationContext().getBean("integrationServicesRootItemSearchService", RootItemSearchService.class);
		}
		return rootItemSearchService;
	}
}
