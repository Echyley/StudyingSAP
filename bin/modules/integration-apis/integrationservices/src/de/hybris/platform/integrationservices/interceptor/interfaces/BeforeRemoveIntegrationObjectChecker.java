/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.interceptor.interfaces;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectRemoveInterceptor;

/**
 * This interface is used with the {@link IntegrationObjectRemoveInterceptor}.
 * Each type that needs to be checked before the Integration Object is removed needs to implement this interface in its extension.
 * This is to prevent circular dependency between this extension (integrationservices) and the extension where the type is defined.
 */
public interface BeforeRemoveIntegrationObjectChecker
{
	/**
	 * This method will be called to check if there are any items that reference the given integrationObject. If there are any, an
	 * {@link InterceptorException} will be thrown.
	 * @param integrationObject The integrationObject to be checked that if any items reference it.
	 * @throws InterceptorException when the Integration Object is referenced
	 */
	void checkIfIntegrationObjectInUse(final IntegrationObjectModel integrationObject) throws InterceptorException;
}
