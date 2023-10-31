/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.utils;

import javax.servlet.ServletRequest;

/**
 * @deprecated Since 2205 Use {@link de.hybris.platform.acceleratorservices.util.SpringHelper} instead
 *
 * Static class used to lookup a spring bean by name and type.
 */
@Deprecated(since = "2205", forRemoval = true)
public final class SpringHelper
{
	private SpringHelper()
	{
		// Empty private constructor
	}

	/**
	 * @deprecated Since 2205 Use {@link de.hybris.platform.acceleratorservices.util.SpringHelper#getSpringBean(ServletRequest, String, Class, boolean)} instead
	 *
	 * Returns the Spring bean with name <code>beanName</code> and of type <code>beanClass</code>. If the cacheInRequest
	 * flag is set to true then the bean is cached in the request attributes.
	 * 
	 * @param <T>
	 *           type of the bean
	 * @param request
	 *           the http request
	 * @param beanName
	 *           name of the bean
	 * @param beanClass
	 *           expected type of the bean
	 * @param cacheInRequest
	 *           flag, set to true to use the request attributes to cache the spring bean
	 * @return the bean matching the given arguments or <code>null</code> if no bean could be resolved
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public static <T> T getSpringBean(final ServletRequest request, final String beanName, final Class<T> beanClass,
			final boolean cacheInRequest)
	{
		return de.hybris.platform.acceleratorservices.util.SpringHelper.getSpringBean(request, beanName, beanClass, cacheInRequest);
	}
}
