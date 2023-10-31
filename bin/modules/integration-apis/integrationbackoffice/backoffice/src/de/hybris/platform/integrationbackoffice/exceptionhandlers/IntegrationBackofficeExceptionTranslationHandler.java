/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptionhandlers;

import de.hybris.platform.inboundservices.exceptions.CannotCreateIntegrationClientCredentialsDetailWithAdminException;
import de.hybris.platform.inboundservices.exceptions.CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationEntityNotSelectedException;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationModelNotFoundException;
import de.hybris.platform.integrationbackoffice.exceptions.ModelingAbstractAttributeAutoCreateOrPartOfException;

import java.util.Collection;
import java.util.Set;

/**
 * A class for translating target exceptions to localized error messages for integration backoffice.
 */
public class IntegrationBackofficeExceptionTranslationHandler extends IntegrationApiExceptionTranslationHandler
{
	private static final Set<Class<? extends Throwable>> TARGET_EXCEPTIONS = Set.of(
			CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException.class,
			ExportConfigurationModelNotFoundException.class,
			ExportConfigurationEntityNotSelectedException.class,
			CannotCreateIntegrationClientCredentialsDetailWithAdminException.class,
			ModelingAbstractAttributeAutoCreateOrPartOfException.class
	);

	@Override
	protected final Collection<Class<? extends Throwable>> getTargetedExceptions()
	{
		return TARGET_EXCEPTIONS;
	}

	@Override
	protected String getKeyPrefix()
	{
		return IntegrationbackofficeConstants.EXTENSIONNAME;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated increases visibility of the base class method and therefore is deprecated for removal. This method is an
	 * implementation detail and should not be used. Use {@link #toString(Throwable)} instead.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	@Override
	public String convertExceptionToResourceMsg(final Throwable exception)
	{
		return super.convertExceptionToResourceMsg(exception);
	}
}
