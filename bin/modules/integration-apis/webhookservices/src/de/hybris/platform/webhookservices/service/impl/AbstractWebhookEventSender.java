/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookEventSender;
import de.hybris.platform.webhookservices.util.ToStringUtil;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Abstract class implements the {@link WebhookEventSender}. This abstract class contains common methods that used by
 * {@link WebhookEventToItemSender} and {@link WebhookDeleteEventToItemSender} to do the actual posting to the destination because
 * the {@link OutboundServiceFacade} already provides item to JSON conversion and monitoring.
 */
public abstract class AbstractWebhookEventSender implements WebhookEventSender
{
	private static final Logger LOGGER = Log.getLogger(AbstractWebhookEventSender.class);

	private final ItemModelSearchService itemModelSearchService;
	private OutboundServiceFacade outboundServiceFacade;

	/**
	 * Constructor fort the {@link AbstractWebhookEventSender}
	 *
	 * @param itemModelSearchService to look up {@link ItemModel}
	 * @param outboundServiceFacade  to send the event out to the destination
	 */
	protected AbstractWebhookEventSender(@NotNull final ItemModelSearchService itemModelSearchService,
			@NotNull final OutboundServiceFacade outboundServiceFacade)
	{
		Preconditions.checkArgument(Objects.nonNull(itemModelSearchService), "ItemModelSearchService cannot be null");
		Preconditions.checkArgument(Objects.nonNull(outboundServiceFacade), "OutboundServiceFacade cannot be null");
		this.itemModelSearchService = itemModelSearchService;
		this.outboundServiceFacade = outboundServiceFacade;
	}

	protected void sendToWebhook(@NotNull final WebhookConfigurationModel webhookConfiguration, final ItemModel item,
			final EventType eventType)
	{
		Preconditions.checkArgument(Objects.nonNull(webhookConfiguration), "webhookConfiguration cannot be null");
		logDebug(item, webhookConfiguration);
		final var params = SyncParameters.syncParametersBuilder()
				.withPayloadObject(item)
				.withSource(OutboundSource.WEBHOOKSERVICES)
				.withIntegrationObject(webhookConfiguration.getIntegrationObject())
				.withDestination(webhookConfiguration.getDestination()).withEventType(eventType).build();

		final var observable = outboundServiceFacade.send(params);
		observable.subscribe(response -> logInfo(item, webhookConfiguration), error -> logError(item, webhookConfiguration, error));
	}

	protected boolean isMatchingDestinationTarget(final WebhookConfigurationModel webhookConfig,
			final EventSourceData eventSourceData)
	{
		final Optional<EventConfigurationModel> eventConfiguration = loadWithoutCaching(eventSourceData.getEventConfig());
		return eventConfiguration.filter(eventConfig -> isSameDestinationTarget(webhookConfig, eventConfig)).isPresent();
	}

	protected Optional<EventConfigurationModel> loadWithoutCaching(final EventConfigurationModel eventConfig)
	{
		return itemModelSearchService.nonCachingFindByPk(eventConfig.getPk());
	}

	protected ItemModelSearchService getItemModelSearchService()
	{
		return itemModelSearchService;
	}

	private boolean isSameDestinationTarget(final WebhookConfigurationModel webhookConfig,
			final EventConfigurationModel eventConfig)
	{
		return webhookConfig.getDestination().getDestinationTarget().getId()
				.contentEquals(eventConfig.getDestinationTarget().getId());
	}

	private void logInfo(final ItemModel item, final WebhookConfigurationModel webhookConfiguration)
	{
		if (LOGGER.isInfoEnabled())
		{
			LOGGER.info("Sending item of type '{}' to {}", item.getItemtype(), ToStringUtil.toString(webhookConfiguration));
		}
	}

	private void logError(final ItemModel item, final WebhookConfigurationModel webhookConfiguration, final Throwable error)
	{
		if (LOGGER.isErrorEnabled())
		{
			LOGGER.error("Failed to send item of type '{}' to {}", item.getItemtype(), ToStringUtil.toString(webhookConfiguration),
					error);
		}
	}

	protected void logDebug(final ItemModel item, final WebhookConfigurationModel webhookConfiguration)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Sending item '{}' to {}", item.getItemtype(), ToStringUtil.toString(webhookConfiguration));
		}
	}

	void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

	OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

}
