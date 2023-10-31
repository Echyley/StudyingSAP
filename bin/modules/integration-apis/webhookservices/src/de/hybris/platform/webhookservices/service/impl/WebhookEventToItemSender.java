/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.filter.WebhookFilterService;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;
import de.hybris.platform.webhookservices.util.ToStringUtil;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;


/**
 * This class extends {@link AbstractWebhookEventSender}, this class sends payload of WebhookEvent to destination.
 */
public final class WebhookEventToItemSender extends AbstractWebhookEventSender
{
	private static final Logger LOGGER = Log.getLogger(WebhookEventToItemSender.class);
	private static final EventType DEFAULT_EVENT_TYPE = DefaultEventType.UNKNOWN;

	private final WebhookConfigurationService webhookConfigurationService;

	@NotNull
	private WebhookFilterService filterService = nullFilterService();

	/**
	 * Instantiates the WebhookEventToItemSender
	 *
	 * @param outboundServiceFacade       OutboundServiceFacade used to send the event out to the destination
	 * @param webhookConfigurationService WebhookConfigurationService used to look up {@link WebhookConfigurationModel}
	 * @param itemModelSearchService      ItemModelSearchService used to look up {@link ItemModel}
	 */
	public WebhookEventToItemSender(@NotNull final OutboundServiceFacade outboundServiceFacade,
			@NotNull final WebhookConfigurationService webhookConfigurationService,
			@NotNull final ItemModelSearchService itemModelSearchService)
	{
		super(itemModelSearchService, outboundServiceFacade);
		Preconditions.checkArgument(webhookConfigurationService != null, "WebhookConfigurationService cannot be null");
		this.webhookConfigurationService = webhookConfigurationService;
	}

	@Override
	public void send(final EventSourceData eventSourceData)
	{
		Preconditions.checkArgument(eventSourceData != null, "EventSourceData cannot be null");
		final var item = findItem(eventSourceData.getEvent());
		item.ifPresent(i -> sendItem(i, eventSourceData));
	}

	private Optional<ItemModel> findItem(final AbstractEvent event)
	{
		if (isWebhookEvent(event))
		{
			return getItemModelSearchService().nonCachingFindByPk(((WebhookEvent) event).getPk());
		}
		return Optional.empty();
	}

	private boolean isWebhookEvent(final AbstractEvent event)
	{
		return event instanceof WebhookEvent;
	}

	private void sendItem(final ItemModel item, final EventSourceData eventSourceData)
	{
		final var event = eventSourceData.getEvent();
		final var webhookConfigs = webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(event, item);
		final var eventType = isWebhookEvent(event) ? ((WebhookEvent) event).getEventType() : DEFAULT_EVENT_TYPE;
		webhookConfigs.stream().filter(config -> isMatchingDestinationTarget(config, eventSourceData))
				.forEach(config -> filterAndSend(config, item, eventType));
	}

	private void filterAndSend(final WebhookConfigurationModel config, final ItemModel item, final EventType eventType)
	{
		filterService.filter(item, config.getFilterLocation()).ifPresentOrElse(it -> sendToWebhook(config, it, eventType),
				() -> LOGGER.trace("Item {} was filtered out from being sent to {}", item, ToStringUtil.toString(config)));
	}

	private WebhookFilterService nullFilterService()
	{
		return new WebhookFilterService()
		{
			@Override
			public <T extends ItemModel> Optional<T> filter(final T item, final String scriptUri)
			{
				return Optional.of(item);
			}
		};
	}

	/**
	 * Injects an optional filter service to be used by this item sender. If the webhook filter service is not injected, no item
	 * filtering will be performed.
	 *
	 * @param service an implementation of the {@code WebhookFilterService} that will execute filtering logic for the items being
	 *                sent by this item sender.
	 */
	public void setFilterService(final WebhookFilterService service)
	{
		filterService = service != null ? service : nullFilterService();
	}

}

