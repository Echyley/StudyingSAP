/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.dto;

import de.hybris.deltadetection.ItemChangeDTO;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an info associated with a delta detect change, that is produced by evaluating the info expression on the
 * {@link de.hybris.deltadetection.StreamConfiguration}.
 * <p>This is structured presentation of the {@link ItemChangeDTO#getInfo()}</p> that is used by the outbound sync.
 */
public class ChangeInfo
{
	private final String integrationKey;
	private final String rootItemType;
	private final String itemType;

	@JsonCreator
	public ChangeInfo(
			@JsonProperty("key") final String key,
			@JsonProperty("type") final String type,
			@JsonProperty("rootType") final String rootType)
	{
		integrationKey = key;
		itemType = type;
		rootItemType = rootType;
	}

	/**
	 * Retrieves value of the integration key derived by the info expression.
	 *
	 * @return value of the integration key calculated for the changed item.
	 */
	public String getIntegrationKey()
	{
		return integrationKey;
	}

	/**
	 * Retrieves type of the root item derived by the info expression.
	 *
	 * @return type of the item that is associated with the changed item and that corresponds to the root item of the
	 * integration object used for the outbound sync. If the integration object does not have a root item or the root item cannot
	 * be navigated from the changed item, then the value is {@code null}.
	 */
	public String getRootItemType()
	{
		return rootItemType;
	}

	/**
	 * Retrieves item type derived by the info expression.
	 *
	 * @return type of the changed item.
	 */
	public String getItemType()
	{
		return itemType;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final ChangeInfo that = (ChangeInfo) o;
			return Objects.equals(integrationKey, that.integrationKey)
					&& Objects.equals(rootItemType, that.rootItemType)
					&& Objects.equals(itemType, that.itemType);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(integrationKey, rootItemType, itemType);
	}

	@Override
	public String toString()
	{
		return "ChangeInfo{" +
				"integrationKey=" + valueOf(integrationKey) +
				", itemType=" + valueOf(itemType) +
				", rootItemType=" + valueOf(rootItemType) +
				'}';
	}

	private static String valueOf(final String str)
	{
		return str != null ? ("'" + str + "'") : "null";
	}
}
