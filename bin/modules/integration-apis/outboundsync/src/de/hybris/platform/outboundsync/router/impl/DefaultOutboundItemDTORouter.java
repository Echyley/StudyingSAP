/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.router.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.events.IgnoredOutboundSyncEvent;
import de.hybris.platform.outboundsync.job.ItemChangeSender;
import de.hybris.platform.outboundsync.job.ItemPKPopulator;
import de.hybris.platform.outboundsync.job.RootItemChangeSender;
import de.hybris.platform.outboundsync.router.OutboundItemDTORouter;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DefaultOutboundItemDTORouter implements OutboundItemDTORouter
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundItemDTORouter.class);
	private OutboundItemConsumer changeConsumer;
	private RootItemChangeSender rootItemChangeSender;
	private ItemChangeSender itemDeleteSender;
	private ItemPKPopulator populator;
	private EventService eventService;

	@Override
	public void route(final OutboundItemDTO itemDto)
	{
		if (itemDto.isDeleted())
		{
			routeDeletedItems(itemDto);
		}
		else
		{
			final List<OutboundItemDTO> updatedDtos = getPopulator().populatePK(itemDto);
			updatedDtos.forEach(this::routeDto);
		}
	}

	private void routeDeletedItems(final OutboundItemDTO itemDto)
	{
		if (itemDto.isSynchronizeDelete() && StringUtils.isNotEmpty(itemDto.getIntegrationKey()))
		{
			getItemDeleteSender().send(itemDto);
		}
		else
		{
			LOG.debug("The deleted item cannot be sent for outbound sync: {}", itemDto);
			consumeChange(itemDto);
		}
	}

	private void routeDto(final OutboundItemDTO dto)
	{
		if (dto.getRootItemPK() != null)
		{
			getRootItemChangeSender().sendPopulatedItem(dto);
		}
		else
		{
			consumeChange(dto);
		}
	}

	private void consumeChange(final OutboundItemDTO itemDto)
	{
		LOG.debug("Consuming change for item DTO {}", itemDto);
		getChangeConsumer().consume(itemDto);
		eventService.publishEvent(new IgnoredOutboundSyncEvent(itemDto));
	}

	protected RootItemChangeSender getRootItemChangeSender()
	{
		return rootItemChangeSender;
	}

	@Required
	public void setRootItemChangeSender(final RootItemChangeSender rootItemChangeSender)
	{
		this.rootItemChangeSender = rootItemChangeSender;
	}

	private ItemChangeSender getItemDeleteSender()
	{
		if (itemDeleteSender == null)
		{
			itemDeleteSender = Registry.getApplicationContext().getBean("itemDeleteSender", ItemChangeSender.class);
		}
		return itemDeleteSender;
	}

	public void setItemDeleteSender(final ItemChangeSender itemDeleteSender)
	{
		this.itemDeleteSender = itemDeleteSender;
	}

	protected OutboundItemConsumer getChangeConsumer()
	{
		return changeConsumer;
	}

	@Required
	public void setChangeConsumer(final OutboundItemConsumer changeConsumer)
	{
		this.changeConsumer = changeConsumer;
	}

	protected ItemPKPopulator getPopulator()
	{
		return populator;
	}

	@Required
	public void setPopulator(final ItemPKPopulator populator)
	{
		this.populator = populator;
	}

	@Required
	public void setEventService(final EventService service)
	{
		eventService = service;
	}
}
