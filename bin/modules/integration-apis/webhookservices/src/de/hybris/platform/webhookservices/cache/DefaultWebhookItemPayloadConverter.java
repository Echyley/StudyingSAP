/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.webhookservices.dto.WebhookItemConversion;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.filter.WebhookFilterService;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.model.WebhookPayloadModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default {@link WebhookItemConversion} converter.
 */
public class DefaultWebhookItemPayloadConverter implements Converter<ItemModel, Set<WebhookItemConversion>>
{
	private final WebhookFilterService webhookFilterService;
	private final WebhookConfigurationService webhookConfigurationService;
	private final IntegrationObjectConversionService conversionService;

	/**
	 * Instantiates a new Webhook item payload conversion service.
	 *
	 * @param webhookConfigurationService to look up {@link WebhookConfigurationModel}
	 * @param conversionService           to convert an item model to the integration object payload
	 * @param webhookFilterService        to filter {@link ItemModel} being sent to a webhook
	 */
	public DefaultWebhookItemPayloadConverter(@NotNull final WebhookConfigurationService webhookConfigurationService,
	                                          @NotNull final IntegrationObjectConversionService conversionService,
	                                          @NotNull final WebhookFilterService webhookFilterService)
	{
		Preconditions.checkArgument(webhookConfigurationService != null, "WebhookConfigurationService cannot be null");
		Preconditions.checkArgument(conversionService != null, "IntegrationObjectConversionService cannot be null");
		Preconditions.checkArgument(webhookFilterService != null, "WebhookFilterService cannot be null");

		this.webhookConfigurationService = webhookConfigurationService;
		this.conversionService = conversionService;
		this.webhookFilterService = webhookFilterService;
	}

	/**
	 * Converts {@link ItemModel} to {@link WebhookItemConversion}.
	 *
	 * @param item the deleted {@link ItemModel} to convert
	 * @return the set of {@link WebhookItemConversion} if item can be converted; otherwise returns {@code Collections.emptySet()}
	 */
	@Override
	public Set<WebhookItemConversion> convert(@Nonnull final ItemModel item)
	{
		final var event = convertToItemDeletedEvent(item);
		final var webhookConfigs = webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(event, item);

		return webhookConfigs.stream()
		                     .map(config -> convertToWebhookItem(config, item))
		                     .filter(Optional::isPresent)
		                     .map(Optional::get)
		                     .collect(Collectors.toSet());
	}

	private Optional<WebhookItemConversion> convertToWebhookItem(final WebhookConfigurationModel config,
	                                                          final ItemModel item)
	{
		return filterDeletedItem(config, item).map(it -> generateItemPayload(config, it));
	}

	private WebhookItemConversion generateItemPayload(final WebhookConfigurationModel config, final ItemModel item)
	{
		return new WebhookItemConversion(config.getPk(), generateWebhookPayload(config, item));
	}

	private Optional<ItemModel> filterDeletedItem(final WebhookConfigurationModel config, final ItemModel item)
	{
		return webhookFilterService.filter(item, config.getFilterLocation());
	}

	private ItemDeletedEvent convertToItemDeletedEvent(final ItemModel item)
	{
		final var saveEvent = new AfterSaveEvent(item.getPk(), AfterSaveEvent.REMOVE);
		return new ItemDeletedEvent(saveEvent);
	}

	private WebhookPayloadModel generateWebhookPayload(final WebhookConfigurationModel config, final ItemModel item)
	{
		final var convertedMap = conversionService.convert(item, config.getIntegrationObject().getCode());
		final var webhookPayload = new WebhookPayloadModel();
		webhookPayload.setData(convertedMap);

		return webhookPayload;
	}
}
