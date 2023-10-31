/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Preconditions;


/**
 * Webhook data transfer object of a deleted item for all applicable webhook configurations.
 */
public class WebhookItemPayload implements Serializable
{
	private static final long serialVersionUID = 2906110693380455588L;

	private final Set<WebhookItemConversion> itemConversions;

	/**
	 * Instantiates the {@link WebhookItemPayload}
	 *
	 * @param itemConversions deleted item webhook payload {@link Set<WebhookItemConversion>}
	 */
	public WebhookItemPayload(@NotNull final Set<WebhookItemConversion> itemConversions)
	{
		Preconditions.checkArgument(itemConversions != null, "Set<WebhookItemConversion> cannot be null");
		this.itemConversions = Collections.unmodifiableSet(itemConversions);
	}

	/**
	 * Get the deleted item webhook payload
	 *
	 * @return deleted item webhook payload {@link Set<WebhookItemConversion>}
	 */
	public Set<WebhookItemConversion> getItemConversions()
	{
		return itemConversions;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final WebhookItemPayload other = (WebhookItemPayload) o;
		return new EqualsBuilder()
				.append(itemConversions, other.itemConversions)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(itemConversions)
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("itemConversions", itemConversions)
				.toString();
	}

}


