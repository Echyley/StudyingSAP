/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.cache.IntegrationCacheKey;
import de.hybris.platform.regioncache.key.AbstractCacheKey;

import java.util.Objects;

import com.google.common.base.Preconditions;


/**
 * A cache key for a deleted item to be sent to a webhook.
 */
public final class WebhookDeletedItemCacheKey extends AbstractCacheKey implements IntegrationCacheKey<PK>
{
	private static final String WEBHOOK_REMOVED_ITEM = "__DELETED_ITEMS_CACHE__";

	private final PK pk;

	private WebhookDeletedItemCacheKey(final String typeCode, final String tenantId, final PK pk)
	{
		super(typeCode, tenantId);
		Preconditions.checkArgument(pk != null, "pk cannot be null");
		this.pk = pk;
	}

	/**
	 * Creates an instance of {@link WebhookDeletedItemCacheKey}.
	 *
	 * @param pk the {@link PK} of the deleted item.
	 * @return an instance of {@link WebhookDeletedItemCacheKey}.
	 */
	public static WebhookDeletedItemCacheKey from(final PK pk)
	{
		return new WebhookDeletedItemCacheKey(WEBHOOK_REMOVED_ITEM, WebhookTenantUtil.getCurrentTenantId(), pk);
	}

	@Override
	public PK getId()
	{
		return pk;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || this.getClass() != o.getClass() || !super.equals(o))
		{
			return false;
		}
		final WebhookDeletedItemCacheKey otherKey = (WebhookDeletedItemCacheKey) o;
		return Objects.equals(pk, otherKey.pk);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), pk);
	}
}
