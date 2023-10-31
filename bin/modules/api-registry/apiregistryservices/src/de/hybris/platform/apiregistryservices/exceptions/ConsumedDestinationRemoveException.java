/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import javax.validation.constraints.NotNull;

/**
 * An exception indicating a Consumed Destination {@link de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel}
 * cannot be deleted if it was assigned to any item, such as webhookConfiguration {@link de.hybris.platform.webhookservices.model.WebhookConfigurationModel},
 * or outboundChannelConfiguration {@link de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel}
 */
public class ConsumedDestinationRemoveException extends InterceptorException
{
	private static final String ERROR_MESSAGE_TEMPLATE = "ConsumedDestinationModel [%s] cannot be deleted because it is used in one or more [%s]";

	/**
	 * Constructor
	 *
	 * @param consumedDestinationId consumed destination id
	 * @param itemDetail            item detail
	 */
	public ConsumedDestinationRemoveException(@NotNull final String consumedDestinationId, @NotNull final String itemDetail)
	{
		super(String.format(ERROR_MESSAGE_TEMPLATE, consumedDestinationId, itemDetail));
	}
}
