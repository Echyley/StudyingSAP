/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search;

import de.hybris.platform.integrationservices.item.IntegrationItem;

import javax.annotation.Nullable;

/**
 * Exception thrown when a single item search does not find an item.
 */
public class ItemNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 4641952683391643712L;
	private static final String ITEM_NOT_FOUND_MESSAGE = "[%s] with integration key [%s] was not found.";

	private final transient IntegrationItem requestedItem;
	private final String integrationKey;

	/**
	 * Instantiates this exception for the specified item search request
	 *
	 * @param item sample of the item that was searched and was not found in the persistence storage.
	 */
	public ItemNotFoundException(@Nullable final IntegrationItem item)
	{
		super(deriveMessage(item));
		requestedItem = item;
		integrationKey = deriveIntegrationKey(item);
	}

	private static String deriveMessage(@Nullable final IntegrationItem item)
	{
		final var entityType = item == null ? null : item.getItemType().getItemCode();
		final var integrationKey = deriveIntegrationKey(item);
		return String.format(ITEM_NOT_FOUND_MESSAGE, entityType, integrationKey);
	}

	private static String deriveIntegrationKey(final IntegrationItem item)
	{
		return item != null
				? item.getIntegrationKey()
				: null;
	}

	/**
	 * Retrieves info about the item searched.
	 *
	 * @return presentation of the item being searched or {@code null}, if this information is not available.
	 */
	@Nullable
	public IntegrationItem getRequestedItem()
	{
		return requestedItem;
	}

	/**
	 * Retrieves integration key of the item that was searched.
	 * @return integration key value for the item to find.
	 */
	public String getIntegrationKey()
	{
		return integrationKey;
	}
}
