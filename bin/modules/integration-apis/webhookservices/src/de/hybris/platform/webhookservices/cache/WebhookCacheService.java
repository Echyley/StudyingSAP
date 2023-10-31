/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.webhookservices.dto.WebhookItemPayload;

import java.util.Optional;


/**
 * A service to cache deleted {@link ItemModel} data representation for Webhook.
 */
public interface WebhookCacheService
{
	/**
	 * Find {@link WebhookItemPayload} converted from deleted {@link ItemModel} in converted cache
	 *
	 * @param pk PK of deleted {@link ItemModel} to find
	 * @return return {@link WebhookItemPayload} if found; otherwise returns {@code Optional.empty()}
	 */
	Optional<WebhookItemPayload> findItemPayloads(final PK pk);

	/**
	 * Find {@link ItemModel} in deleted cache
	 *
	 * @param pk  PK of deleted {@link ItemModel} to find
	 * @param <T> expected type of the item found
	 * @return return {@link ItemModel} if found; otherwise returns {@code Optional.empty()}
	 */
	<T extends ItemModel> Optional<T> findItem(final PK pk);

	/**
	 * Cache deleted {@link ItemModel}
	 *
	 * @param item PK of deleted {@link ItemModel} to cache
	 * @param ctx  the {@link InterceptorContext}
	 */
	void cacheDeletedItem(final ItemModel item, final InterceptorContext ctx);
}
