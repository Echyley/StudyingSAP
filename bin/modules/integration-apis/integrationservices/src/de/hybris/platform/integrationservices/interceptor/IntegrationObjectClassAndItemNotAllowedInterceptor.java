/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * An interceptor that validates one {@link IntegrationObjectModel} assigned {@link IntegrationObjectClassModel} can not have
 * {@link IntegrationObjectItemModel}
 */
public class IntegrationObjectClassAndItemNotAllowedInterceptor implements ValidateInterceptor<IntegrationObjectClassModel>
{
	private static final String ERROR = "Integration object [%s] has integration object item(s) %s assigned. " +
			"Mixing IntegrationObjectClass and IntegrationObjectItem models in an Integration Object is not allowed.";

	@Override
	public void onValidate(final IntegrationObjectClassModel ioClass, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (hasItems(ioClass))
		{
			throw new InterceptorException(String.format(ERROR, ioClass.getIntegrationObject().getCode(), getItemNames(ioClass)),
					this);
		}
	}

	private boolean hasItems(final IntegrationObjectClassModel ioClass)
	{
		return CollectionUtils.isNotEmpty(ioClass.getIntegrationObject().getItems());
	}

	private static List<String> getItemNames(final IntegrationObjectClassModel ioClass)
	{
		return ioClass.getIntegrationObject().getItems().stream()
		              .map(IntegrationObjectItemModel::getCode)
		              .toList();
	}
}
