/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * An interceptor that validates the {@link IntegrationObjectItemAttributeModel}'s attribute cannot be private
 */
public class IntegrationObjectItemPrivateAttributeValidateInterceptor implements ValidateInterceptor<IntegrationObjectItemAttributeModel>
{
	private static final String ERROR_MSG_TEMPLATE = "Private attribute [%s.%s] can not be used in integration object item.";

	@Override
	public void onValidate(final IntegrationObjectItemAttributeModel attributeModel, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (Boolean.TRUE.equals(attributeModel.getAttributeDescriptor().getPrivate()))
		{
			final String typeCode = attributeModel.getAttributeDescriptor().getItemtype();
			final String attributeName = attributeModel.getAttributeName();
			throw new InterceptorException(String.format(ERROR_MSG_TEMPLATE, typeCode, attributeName), this);
		}
	}
}
