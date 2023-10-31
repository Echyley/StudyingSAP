/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.webhookservices.cache.WebhookCacheService;
import de.hybris.platform.webhookservices.dto.WebhookItemConversion;
import de.hybris.platform.webhookservices.dto.WebhookItemPayload;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;


/**
 * This class extends {@link AbstractWebhookEventSender} and sends payloads of deleteWebhookEvent.
 */
public final class WebhookDeleteEventToItemSender extends AbstractWebhookEventSender
{
	private final WebhookCacheService webhookCacheService;
	private final WebhookConfigurationService webhookConfigurationService;

	/**
	 * Instantiate the WebhookDeleteEventToItemSender
	 *
	 * @param webhookCacheService         to retrieve cached item conversions
	 * @param webhookConfigurationService to retrieve webhook configuration
	 * @param itemModelSearchService      to look up {@link ItemModel}
	 * @param outboundServiceFacade       to send the event out to the destination
	 */
	public WebhookDeleteEventToItemSender(@NotNull final WebhookCacheService webhookCacheService,
			@NotNull final WebhookConfigurationService webhookConfigurationService,
			@NotNull final ItemModelSearchService itemModelSearchService,
			@NotNull final OutboundServiceFacade outboundServiceFacade)
	{
		super(itemModelSearchService, outboundServiceFacade);
		Preconditions.checkArgument(Objects.nonNull(webhookCacheService), "WebhookCacheService cannot be null");
		Preconditions.checkArgument(Objects.nonNull(webhookConfigurationService), "WebhookConfigurationService cannot be null");
		this.webhookCacheService = webhookCacheService;
		this.webhookConfigurationService = webhookConfigurationService;
	}

	@Override
	public void send(final EventSourceData eventSourceData)
	{
		if (isDeleteItemEvent(eventSourceData.getEvent()))
		{
			final var itemDeletedEvent = (ItemDeletedEvent) eventSourceData.getEvent();
			processItemDeleteEvent(itemDeletedEvent, eventSourceData);
		}
	}

	private void processItemDeleteEvent(final ItemDeletedEvent itemDeletedEvent, final EventSourceData eventSourceData)
	{
		final Optional<WebhookItemPayload> payload = webhookCacheService.findItemPayloads(itemDeletedEvent.getPk());
		payload.ifPresent(entry -> processCachedPayload(itemDeletedEvent, eventSourceData, entry));
	}

	private void processCachedPayload(final ItemDeletedEvent itemDeletedEvent, final EventSourceData eventSourceData,
			final WebhookItemPayload webhookItemPayload)
	{
		webhookItemPayload.getItemConversions().forEach(
				webhookItemConfigPayload -> processCachedConversion(itemDeletedEvent, eventSourceData, webhookItemConfigPayload));
	}

	private void processCachedConversion(final ItemDeletedEvent itemDeletedEvent, final EventSourceData eventSourceData,
			final WebhookItemConversion webhookItemConversion)
	{
		findWebhookConfiguration(webhookItemConversion.getWebhookConfigurationPk()).filter(
				webhookConfig -> isMatchingDestinationTarget(webhookConfig, eventSourceData)).ifPresent(
				webhookConfig -> sendToWebhook(webhookConfig, webhookItemConversion.getWebhookPayload(),
						itemDeletedEvent.getEventType()));
	}

	private boolean isDeleteItemEvent(final AbstractEvent event)
	{
		return event instanceof ItemDeletedEvent;
	}

	private Optional<WebhookConfigurationModel> findWebhookConfiguration(final PK pk)
	{
		return webhookConfigurationService.findWebhookConfigurationByPk(pk);
	}

}

