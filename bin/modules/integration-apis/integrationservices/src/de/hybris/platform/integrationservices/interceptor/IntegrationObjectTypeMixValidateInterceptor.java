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
 * An interceptor that validates one {@link IntegrationObjectModel} can not have
 * {@link IntegrationObjectItemModel} and {@link IntegrationObjectClassModel}
 */
public class IntegrationObjectTypeMixValidateInterceptor implements ValidateInterceptor<IntegrationObjectModel>
{
	private static final String ERROR = "Integration object [%s] has integration object item(s) %s and class(es) %s assigned. " +
			"Mixing IntegrationObjectClass and IntegrationObjectItem models in an Integration Object is not allowed.";

	@Override
	public void onValidate(final IntegrationObjectModel integrationObject, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (hasItemsAndClasses(integrationObject))
		{
			throw new InterceptorException(String.format(ERROR, integrationObject.getCode(), getItemNames(integrationObject),
					getClassNames(integrationObject)), this);
		}
	}

	private boolean hasItemsAndClasses(final IntegrationObjectModel integrationObject)
	{
		return CollectionUtils.isNotEmpty(integrationObject.getItems()) &&
				CollectionUtils.isNotEmpty(integrationObject.getClasses());
	}

	private static List<String> getClassNames(final IntegrationObjectModel integrationObject)
	{
		return integrationObject.getClasses().stream()
		                        .map(IntegrationObjectClassModel::getCode)
		                        .toList();
	}

	private static List<String> getItemNames(final IntegrationObjectModel integrationObject)
	{
		return integrationObject.getItems().stream()
		                        .map(IntegrationObjectItemModel::getCode)
		                        .toList();
	}
}
