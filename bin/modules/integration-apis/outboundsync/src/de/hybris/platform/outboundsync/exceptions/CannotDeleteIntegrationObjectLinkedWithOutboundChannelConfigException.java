/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.exceptions;

import de.hybris.platform.integrationservices.exception.CannotDeleteIntegrationObjectException;

/**
 * An exception indicating an Integration Object {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 * cannot be deleted if it was assigned to an OutboundChannelConfiguration
 * {@link de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel}
 */
public class CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException extends CannotDeleteIntegrationObjectException
{
	private static final long serialVersionUID = 2129858395060534444L;
	private static final String ERROR_MESSAGE_TEMPLATE = "The [%s] cannot be deleted because it is in use with " +
			"OutboundChannelConfiguration: %s . Please delete the related OutboundChannelConfiguration and try again.";

	/**
	 * Instantiates this exception
	 *
	 * @param ioCode  code of the integration object that was attempted to be deleted.
	 * @param occCode code of the outbound channel configuration that still uses the integration object and prevents it from being
	 *                deleted
	 */
	public CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(final String ioCode, final String occCode)
	{
		super(String.format(ERROR_MESSAGE_TEMPLATE, ioCode, occCode), occCode);
	}
}

