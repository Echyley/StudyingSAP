/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.impl.PojoIntrospector;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.Map;

import org.springframework.beans.BeanUtils;

/**
 * Interceptor to validate that only {@link Map} with primitive key and value type are allowed.
 */
public class ClassMapTypeValidateInterceptor implements ValidateInterceptor<IntegrationObjectClassAttributeModel>
{
	private static final String ERROR_MSG = "Map type attribute [%s] must have primitive key and value type";

	@Override
	public void onValidate(final IntegrationObjectClassAttributeModel attribute,
	                       final InterceptorContext interceptorContext) throws InterceptorException
	{
		final var introspector = new PojoIntrospector(attribute);
		if (!isSupportedType(introspector))
		{
			throw new InterceptorException(String.format(ERROR_MSG, attribute.getAttributeName()), this);
		}
	}

	private boolean isSupportedType(final PojoIntrospector introspector)
	{
		return !introspector.isMap() || isMapOfSimpleTypes(introspector);
	}

	private boolean isMapOfSimpleTypes(final PojoIntrospector introspector)
	{
		return BeanUtils.isSimpleValueType(introspector.getMapKeyType()) &&
				BeanUtils.isSimpleValueType(introspector.getMapValueType());
	}
}
