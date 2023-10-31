/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.exceptions;

import de.hybris.platform.integrationservices.exception.CannotDeleteIntegrationObjectException;

/**
 * An exception indicating an Integration Object {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 * cannot be deleted if it was assigned to an InboundChannelConfiguration
 * {@link de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel}
 */
public class CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException extends CannotDeleteIntegrationObjectException
{
	private static final long serialVersionUID = -8373750639145133276L;
	private static final String ERROR_MESSAGE_TEMPLATE = "The [%s] cannot be deleted because it is in use with at least one " +
			"InboundChannelConfiguration for Authentication. Please delete the related InboundChannelConfiguration and try again.";

	/**
	 * Constructor
	 *
	 * @param integrationObject integration object code
	 */
	public CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException(final String integrationObject)
	{
		super(String.format(ERROR_MESSAGE_TEMPLATE, integrationObject));
	}
}

