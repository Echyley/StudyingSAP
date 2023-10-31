/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.service.DeleteRequestSender;
import de.hybris.platform.outboundsync.activator.DeleteOutboundSyncService;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJobRegister;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the {@link DeleteOutboundSyncService}
 */
public class DefaultDeleteOutboundSyncService extends BaseOutboundSyncService implements DeleteOutboundSyncService
{
	private static final Logger LOG = Log.getLogger(DefaultDeleteOutboundSyncService.class);

	private DeleteRequestSender deleteRequestSender;

	/**
	 * Instantiates this service
	 *
	 * @param deleteRequestSender    Sender that sends the delete request
	 * @param itemModelSearchService Finder that retrieves an item model
	 * @param eventService           Service to send events to update cronjob status
	 * @param outboundItemConsumer   Consumer that consumes the delta detect change
	 * @see BaseOutboundSyncService#setEventService(EventService)
	 * @see BaseOutboundSyncService#setOutboundItemConsumer(OutboundItemConsumer)
	 * @deprecated use {@link #DefaultDeleteOutboundSyncService(ItemModelSearchService, OutboundItemFactory, OutboundSyncJobRegister, DeleteRequestSender)}
	 * This constructor does not set the {@link EventService} and the {@link OutboundItemConsumer} anymore. If this constructor is
	 * used, the dependencies must be wired through the corresponding setters now.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultDeleteOutboundSyncService(@NotNull final DeleteRequestSender deleteRequestSender,
	                                        @NotNull final ItemModelSearchService itemModelSearchService,
	                                        @NotNull final EventService eventService,
	                                        @NotNull final OutboundItemConsumer outboundItemConsumer)
	{
		this(itemModelSearchService,
				BaseOutboundSyncService.getService("outboundItemFactory", OutboundItemFactory.class),
				BaseOutboundSyncService.getService("outboundSyncJobRegister", OutboundSyncJobRegister.class),
				deleteRequestSender);
	}

	/**
	 * Instantiates this services with its required dependencies
	 *
	 * @param searchService a service to use for the cron job and changed item model searches
	 * @param factory       A factory implementation to create instances of {@link de.hybris.platform.outboundsync.dto.OutboundItem}
	 * @param register      a register to inquire about currently running outbound sync jobs
	 * @param sender        a sender to use for sending the delete changes out
	 */
	public DefaultDeleteOutboundSyncService(@NotNull final ItemModelSearchService searchService,
	                                        @NotNull final OutboundItemFactory factory,
	                                        @NotNull final OutboundSyncJobRegister register,
	                                        @NotNull final DeleteRequestSender sender)
	{
		super(searchService, factory, register);
		Preconditions.checkArgument(sender != null, "DeleteRequestSender cannot be null");
		deleteRequestSender = sender;
	}

	@Override
	public void sync(final OutboundItemDTO deletedItem)
	{
		syncInternal(deletedItem.getCronJobPK(), asItemGroup(deletedItem), () -> synchronizeDelete(deletedItem));
		consumeChange(deletedItem);
	}

	private void synchronizeDelete(final OutboundItemDTO deletedItem)
	{
		LOG.debug("Attempting to sync delete item {}", deletedItem);
		final Optional<OutboundChannelConfigurationModel> occ = getOutboundChannelConfiguration(deletedItem);
		occ.ifPresent(config -> sendRequestAndPublishCompletionEvent(deletedItem, config));
	}

	private void sendRequestAndPublishCompletionEvent(final OutboundItemDTO deletedItem,
	                                                  final OutboundChannelConfigurationModel outboundChannelConfig)
	{
		try
		{
			sendDeleteRequest(deletedItem, outboundChannelConfig);
			publishSuccessfulCompletedEvent(asItemGroup(deletedItem));
		}
		catch (final RuntimeException e)
		{
			LOG.error("An exception occurred when sending delete request with message", e);
			publishUnSuccessfulCompletedEvent(asItemGroup(deletedItem));
		}
	}

	private void sendDeleteRequest(final OutboundItemDTO deletedItem,
	                               final OutboundChannelConfigurationModel outboundChannelConfig)
	{
		final SyncParameters params = SyncParameters.syncParametersBuilder()
		                                            .withIntegrationKey(deletedItem.getIntegrationKey())
		                                            .withIntegrationObject(outboundChannelConfig.getIntegrationObject())
		                                            .withDestination(outboundChannelConfig.getDestination())
		                                            .withSource(OutboundSource.OUTBOUNDSYNC)
		                                            .build();
		LOG.debug("Sending delete request with params {}", params);
		deleteRequestSender.send(params);
	}

	private Optional<OutboundChannelConfigurationModel> getOutboundChannelConfiguration(final OutboundItemDTO item)
	{
		final var outboundChannelPk = PK.fromLong(item.getChannelConfigurationPK());
		return findItemByPk(outboundChannelPk);
	}

	DeleteRequestSender getDeleteRequestSender()
	{
		return deleteRequestSender;
	}

	void setDeleteRequestSender(final DeleteRequestSender sender)
	{
		deleteRequestSender = sender;
	}
}
