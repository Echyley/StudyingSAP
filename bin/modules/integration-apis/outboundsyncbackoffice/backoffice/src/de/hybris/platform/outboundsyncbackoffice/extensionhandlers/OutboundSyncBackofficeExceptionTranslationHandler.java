/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsyncbackoffice.extensionhandlers;

import de.hybris.platform.integrationbackoffice.exceptionhandlers.IntegrationApiExceptionTranslationHandler;
import de.hybris.platform.outboundsync.exceptions.CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException;
import de.hybris.platform.outboundsyncbackoffice.constants.OutboundsyncbackofficeConstants;

import java.util.Collection;
import java.util.Set;

/**
 * A service for translating exceptions into localized messages used by the backoffice.
 */
public class OutboundSyncBackofficeExceptionTranslationHandler extends IntegrationApiExceptionTranslationHandler
{
	private static final Set<Class<? extends Throwable>> KNOWN_EXCEPTIONS = Set.of(
			CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException.class);

	@Override
	protected Collection<Class<? extends Throwable>> getTargetedExceptions()
	{
		return KNOWN_EXCEPTIONS;
	}

	@Override
	protected String getKeyPrefix()
	{
		return OutboundsyncbackofficeConstants.EXTENSIONNAME;
	}
}
