/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Verifies the {@link IntegrationObjectModel} contains a root class. If none exists, exception is thrown.
 */
public class RootClassExistsValidateInterceptor implements ValidateInterceptor<IntegrationObjectModel>
{
	private static final String ERROR = "IntegrationObject [%s] does not have a root class assigned.";

	@Override
	public void onValidate(final IntegrationObjectModel integrationObjectModel, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (hasNoRootClass(integrationObjectModel))
		{
			throw new InterceptorException(String.format(ERROR, integrationObjectModel.getCode()));
		}
	}

	private boolean hasNoRootClass(final IntegrationObjectModel integrationObjectModel)
	{
		return CollectionUtils.isNotEmpty(integrationObjectModel.getClasses()) &&
				integrationObjectModel.getRootClass() == null;
	}
}
