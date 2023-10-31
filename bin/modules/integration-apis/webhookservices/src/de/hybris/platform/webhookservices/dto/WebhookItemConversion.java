/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.dto;

import de.hybris.platform.core.PK;
import de.hybris.platform.webhookservices.model.WebhookPayloadModel;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Preconditions;


/**
 * Webhook data transfer object of a deleted item for applicable webhook configuration.
 */
public class WebhookItemConversion implements Serializable
{
	private static final long serialVersionUID = 4363996336903551407L;

	private final WebhookPayloadModel webhookPayload;
	private final PK webhookConfigurationPk;

	/**
	 * Instantiates the {@link WebhookItemConversion}
	 *
	 * @param webhookConfigurationPk webhook configuration primary key
	 * @param webhookPayload         webhook payload
	 */
	public WebhookItemConversion(@NotNull final PK webhookConfigurationPk, @NotNull final WebhookPayloadModel webhookPayload)
	{
		Preconditions.checkArgument(webhookConfigurationPk != null, "PK cannot be null");
		Preconditions.checkArgument(webhookPayload != null, "WebhookPayloadModel cannot be null");

		this.webhookConfigurationPk = webhookConfigurationPk;
		this.webhookPayload = webhookPayload;
	}

	/**
	 * Get the deleted item webhook payload
	 *
	 * @return deleted item webhook payload {@link WebhookPayloadModel}
	 */
	public WebhookPayloadModel getWebhookPayload()
	{
		return webhookPayload;
	}

	/**
	 * Get the webhook configuration primary key
	 *
	 * @return the {@link PK}
	 */
	public PK getWebhookConfigurationPk()
	{
		return webhookConfigurationPk;
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
		final WebhookItemConversion other = (WebhookItemConversion) o;
		return new EqualsBuilder()
				.append(webhookPayload.getItemtype(), other.webhookPayload.getItemtype())
				.append(webhookPayload.getData(), other.webhookPayload.getData())
				.append(webhookConfigurationPk, other.webhookConfigurationPk)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(webhookPayload.getItemtype())
				.append(webhookPayload.getData())
				.append(webhookConfigurationPk)
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("webhookPayload.data", webhookPayload.getData())
				.append("webhookConfigurationPk", webhookConfigurationPk)
				.toString();
	}

}
