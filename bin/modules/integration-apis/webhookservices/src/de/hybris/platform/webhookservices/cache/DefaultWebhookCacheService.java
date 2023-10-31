/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.cache.IntegrationCache;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.webhookservices.dto.WebhookItemConversion;
import de.hybris.platform.webhookservices.dto.WebhookItemPayload;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link WebhookCacheService}
 */
public class DefaultWebhookCacheService implements WebhookCacheService
{
	private static final String DELIMITER = "-";

	private final IntegrationCache<WebhookDeletedItemCacheKey, WebhookItemPayload> itemPayloadsCache;
	private final IntegrationCache<WebhookDeletedItemCacheKey, ItemModel> deletedItemCache;
	private final WebhookConfigurationService configurationService;
	private final Converter<ItemModel, Set<WebhookItemConversion>> itemPayloadConverter;

	/**
	 * Instantiates a new {@link WebhookCacheService}
	 *
	 * @param itemPayloadsCache    cache for {@link WebhookItemPayload}
	 * @param deletedItemCache     cache for deleted {@link ItemModel}
	 * @param configurationService to look up {@link WebhookConfigurationModel}
	 * @param itemPayloadConverter to convert {@link ItemModel} to set of {@link WebhookItemConversion}
	 */
	public DefaultWebhookCacheService(
			@NotNull final IntegrationCache<WebhookDeletedItemCacheKey, WebhookItemPayload> itemPayloadsCache,
			@NotNull final IntegrationCache<WebhookDeletedItemCacheKey, ItemModel> deletedItemCache,
			@NotNull final WebhookConfigurationService configurationService,
			@NotNull final Converter<ItemModel, Set<WebhookItemConversion>> itemPayloadConverter)
	{
		Preconditions.checkArgument(itemPayloadsCache != null, "itemPayloadCache cannot be null");
		Preconditions.checkArgument(deletedItemCache != null, "deletedItemCache cannot be null");
		Preconditions.checkArgument(configurationService != null, "WebhookConfigurationService cannot be null");
		Preconditions.checkArgument(itemPayloadConverter != null, "WebhookItemPayloadConverter cannot be null");

		this.itemPayloadsCache = itemPayloadsCache;
		this.deletedItemCache = deletedItemCache;
		this.configurationService = configurationService;
		this.itemPayloadConverter = itemPayloadConverter;
	}

	@Override
	public Optional<WebhookItemPayload> findItemPayloads(final PK pk)
	{
		return findInCache(pk, itemPayloadsCache);
	}

	@Override
	public <T extends ItemModel> Optional<T> findItem(final PK pk)
	{
		return (Optional<T>) findInCache(pk, deletedItemCache);
	}

	@Override
	public void cacheDeletedItem(final ItemModel item, final InterceptorContext ctx)
	{
		if (isCachingItemPayloadsRequired(item))
		{
			cacheItem(item, ctx);
			cacheItemPayloads(item);
		}
		else if (isCachingItemRequired(item))
		{
			cacheItem(item, ctx);
		}
	}

	private void cacheItem(final ItemModel item, final InterceptorContext ctx)
	{
		final var key = WebhookDeletedItemCacheKey.from(item.getPk());
		final var clonedItem = ctx.getModelService().clone(item);

		deletedItemCache.put(key, clonedItem);
		registerOnRollback(key, deletedItemCache);
	}

	private void cacheItemPayloads(final ItemModel item)
	{
		final var key = WebhookDeletedItemCacheKey.from(item.getPk());
		final var payload = convert(item);

		itemPayloadsCache.put(key, payload);
		registerOnRollback(key, itemPayloadsCache);
	}

	private WebhookItemPayload convert(final ItemModel item)
	{
		final var conversions = itemPayloadConverter.convert(item);
		Preconditions.checkArgument(conversions != null, "Set<WebhookItemConversion> cannot be null");
		return new WebhookItemPayload(conversions);
	}

	private boolean isCachingItemRequired(final ItemModel item)
	{
		final var event = new AfterSaveEvent(item.getPk(), AfterSaveEvent.UPDATE);
		return isEventConfigured(new ItemUpdatedEvent(event), item) || isEventConfigured(new ItemSavedEvent(event), item);
	}

	private boolean isEventConfigured(final WebhookEvent event, final ItemModel item)
	{
		return !configurationService.findByEventAndItemMatchingAnyItem(event, item).isEmpty();
	}

	private boolean isCachingItemPayloadsRequired(final ItemModel item)
	{
		final var event = new AfterSaveEvent(item.getPk(), AfterSaveEvent.REMOVE);
		final var deletedEvent = new ItemDeletedEvent(event);
		return !configurationService.getWebhookConfigurationsByEventAndItemModel(deletedEvent, item).isEmpty();
	}

	private <T> Optional<T> findInCache(final PK pk, final IntegrationCache<WebhookDeletedItemCacheKey, T> cache)
	{
		if (Objects.nonNull(pk))
		{
			final var cacheKey = WebhookDeletedItemCacheKey.from(pk);
			final var cacheValue = cache.get(cacheKey);

			if (Objects.nonNull(cacheValue))
			{
				cache.remove(cacheKey);
				return Optional.of(cacheValue);
			}
		}
		return Optional.empty();
	}

	private <T> void registerOnRollback(final WebhookDeletedItemCacheKey key,
			final IntegrationCache<WebhookDeletedItemCacheKey, T> cache)
	{
		getCurrentTransaction().executeOnRollback(new Transaction.TransactionAwareExecution()
		{
			@Override
			public void execute(final Transaction tx)
			{
				cache.remove(key);
			}

			@Override
			public Object getId()
			{
				final var value = cache.get(key);
				StringBuilder stringBuilder = new StringBuilder();
				return stringBuilder
						.append(key.getId())
						.append(DELIMITER)
						.append(Objects.nonNull(value) ? value.getClass().getSimpleName() : "")
						.append(DELIMITER)
						.toString();
			}
		});
	}

	Transaction getCurrentTransaction()
	{
		return Transaction.current();
	}
}
