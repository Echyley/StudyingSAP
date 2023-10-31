/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.exception.ClassAttributeExecutionException;
import de.hybris.platform.integrationservices.model.AttributeValueGetter;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation for POJO attributes and uses Java reflection to retrieve the attribute value
 */
public class ClassAttributeValueGetter implements AttributeValueGetter
{
	private static final Object[] NO_PARAMS = {};
	private static final Object[] LOCALE_PARAMS = { Locale.class };

	private final IntegrationObjectClassAttributeModel attribute;
	private final PojoIntrospector introspector;

	/**
	 * Instantiates the getter
	 *
	 * @param a an IOClassAttributeModel used to gather the information needed to retrieve the attribute value
	 */
	public ClassAttributeValueGetter(final IntegrationObjectClassAttributeModel a)
	{
		attribute = a;
		introspector = new PojoIntrospector(a);
	}

	@Override
	public Object getValue(final Object pojo)
	{
		final Method method = findMethod(NO_PARAMS);
		return executeMethod(method, pojo, null);
	}

	@Override
	public Object getValue(final Object model, final Locale locale)
	{
		final Method method = findMethod(LOCALE_PARAMS);
		return executeMethod(method, model, locale);
	}

	@Override
	public Map<Locale, Object> getValues(final Object model, final Locale... locales)
	{
		return Arrays.stream(locales)
		             .collect(Collectors.toMap(l -> l, l -> getValue(model, l)));
	}

	private Object executeMethod(final Method method, final Object model, final Locale locale)
	{
		try
		{
			final Object[] params = locale == null
					? new Object[]{}
					: new Object[]{ locale };
			return method.invoke(model, params);
		}
		catch (final IllegalAccessException | InvocationTargetException e)
		{
			throw new ClassAttributeExecutionException(attribute, e);
		}
	}

	private Method findMethod(final Object[] paramTypes)
	{
		return introspector.findMethod(paramTypes)
		                   .orElseThrow(() -> new ClassAttributeExecutionException(attribute));
	}
}
