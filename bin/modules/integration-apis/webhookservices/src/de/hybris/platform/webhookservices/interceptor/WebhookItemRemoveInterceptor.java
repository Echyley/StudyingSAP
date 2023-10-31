/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.interceptor;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.webhookservices.cache.WebhookCacheService;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * The interceptor will be called before an {@link ItemModel} is removed
 */
public class WebhookItemRemoveInterceptor implements RemoveInterceptor<ItemModel>
{
	private final WebhookCacheService cacheService;

	/**
	 * Instantiates a new {@link WebhookItemRemoveInterceptor}
	 *
	 * @param cacheService to cache deleted {@link ItemModel}
	 */
	public WebhookItemRemoveInterceptor(@NotNull final WebhookCacheService cacheService)
	{
		Preconditions.checkArgument(cacheService != null, "WebhookCacheService cannot be null");
		this.cacheService = cacheService;
	}

	@Override
	public void onRemove(final ItemModel item, final InterceptorContext interceptorContext)
	{
		cacheService.cacheDeletedItem(item, interceptorContext);
	}
}
