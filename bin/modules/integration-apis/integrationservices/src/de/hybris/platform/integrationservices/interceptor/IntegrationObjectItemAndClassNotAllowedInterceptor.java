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
 * An interceptor that validates one {@link IntegrationObjectModel} assigned {@link IntegrationObjectItemModel} can not have
 * {@link IntegrationObjectClassModel}
 */
public class IntegrationObjectItemAndClassNotAllowedInterceptor implements ValidateInterceptor<IntegrationObjectItemModel>
{
	private static final String ERROR = "Integration object [%s] has integration object class(s) %s assigned. " +
			"Mixing IntegrationObjectClass and IntegrationObjectItem models in an Integration Object is not allowed.";

	@Override
	public void onValidate(final IntegrationObjectItemModel item, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (hasClasses(item))
		{
			throw new InterceptorException(String.format(ERROR, item.getIntegrationObject().getCode(), getClassNames(item)),
					this);
		}
	}

	private boolean hasClasses(final IntegrationObjectItemModel ioClass)
	{
		return CollectionUtils.isNotEmpty(ioClass.getIntegrationObject().getClasses());
	}

	private static List<String> getClassNames(final IntegrationObjectItemModel item)
	{
		return item.getIntegrationObject().getClasses().stream()
		           .map(IntegrationObjectClassModel::getCode)
		           .toList();
	}
}
