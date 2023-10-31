/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import static de.hybris.platform.integrationservices.model.IntegrationObjectUtils.isEqual;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * An interceptor that validates only has one root {@link IntegrationObjectClassModel}
 * for {@link IntegrationObjectModel}
 */
public class SingleRootClassValidateInterceptor implements ValidateInterceptor<IntegrationObjectClassModel>
{
	private static final String MSG = "Only one Integration Object Class is allowed to be marked as root class on an Integration Object. " +
			"'%s' has root assigned to %s and %s";

	@Override
	public void onValidate(final IntegrationObjectClassModel newClass, final InterceptorContext ctx) throws InterceptorException
	{
		if (Boolean.TRUE.equals(newClass.getRoot()))
		{
			final IntegrationObjectClassModel existingRootClass = newClass.getIntegrationObject().getRootClass();

			if (existingRootClass != null && isNewRootNotTheSameAsExistingRoot(newClass, existingRootClass))
			{
				throw new InterceptorException(
						String.format(MSG, newClass.getIntegrationObject().getCode(), newClass.getCode(), existingRootClass.getCode()));
			}
		}
	}

	private boolean isNewRootNotTheSameAsExistingRoot(final IntegrationObjectClassModel newClass,
	                                                  final IntegrationObjectClassModel rootClass)
	{
		return !isEqual(newClass, rootClass);
	}
}
