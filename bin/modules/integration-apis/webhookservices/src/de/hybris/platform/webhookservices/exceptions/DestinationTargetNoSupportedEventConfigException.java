/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;

/**
 * Indicates a problem with the {@link DestinationTargetModel} when a destinationTarget is used to create webhookConfig.
 */
public class DestinationTargetNoSupportedEventConfigException extends RuntimeException
{
	private static final String ERROR_MSG_UNSUPPORTED = "The DestinationTarget doesn't have a EventConfiguration supported So it can't be used in the creation of a WebhookConfig.";

	public DestinationTargetNoSupportedEventConfigException()
	{
		super(ERROR_MSG_UNSUPPORTED);
	}
}
