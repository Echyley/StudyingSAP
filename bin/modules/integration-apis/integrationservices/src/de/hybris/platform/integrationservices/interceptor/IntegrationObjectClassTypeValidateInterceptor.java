/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

/**
 * Validates the {@link IntegrationObjectClassModel} type is not a primitive type.
 */
public class IntegrationObjectClassTypeValidateInterceptor
		implements ValidateInterceptor<IntegrationObjectClassModel>
{
	private static final String CLASS_CANNOT_BE_PRIMITIVE =
			"Class [%s] for IntegrationObject [%s] cannot be set to type [%s]. IntegrationObjectClasses cannot be a primitive type.";
	private static final Set<Class<?>> ADDITIONAL_PRIMITIVE_TYPES = new HashSet<>(
			Arrays.asList(BigDecimal.class, BigInteger.class, String.class,
					Date.class, Calendar.class, Object.class, Class.class));

	/**
	 * Validates the context class model and throws exceptions when the type is a primitive type.
	 *
	 * @throws InterceptorException when configuration problems found
	 */
	@Override
	public void onValidate(final IntegrationObjectClassModel ioClass, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (ioClass.getType() != null)
		{
			validateTypeIsNotAPrimitiveType(ioClass);
		}
	}

	private void validateTypeIsNotAPrimitiveType(final IntegrationObjectClassModel ioClass)
			throws InterceptorException
	{
		if (isAPrimitiveType(ioClass))
		{
			throw interceptorException(ioClass);
		}
	}

	private static boolean isAPrimitiveType(final IntegrationObjectClassModel ioClass)
	{
		return ClassUtils.isPrimitiveOrWrapper(ioClass.getType()) || ADDITIONAL_PRIMITIVE_TYPES.contains(ioClass.getType());
	}

	private InterceptorException interceptorException(final IntegrationObjectClassModel contextClass)
	{
		return new InterceptorException(CLASS_CANNOT_BE_PRIMITIVE.formatted(contextClass.getCode(),
				contextClass.getIntegrationObject().getCode(),
				contextClass.getType()), this);
	}
}
