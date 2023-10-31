/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.dataimportcommons;

import de.hybris.platform.core.Registry;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * A utility for retrieving Spring beans defined in the application context.
 */
public final class ApplicationBeans
{
	private static ApplicationContext applicationContext;

	private ApplicationBeans()
	{
		// non-instantiable
	}

	/**
	 * Retrieves a bean defined in the application context
	 *
	 * @param id   ID or name of the Spring in the application context
	 * @param type expected type of Spring bean in the application context
	 * @param <T>  expected type of Spring bean in the application context
	 * @return bean instance defined in the Spring application context.
	 * @throws NoSuchBeanDefinitionException if the bean of the specified type and ID does not exist.
	 */
	public static <T> T getBean(final String id, final Class<T> type)
	{
		return getApplicationContext().getBean(id, type);
	}

	static void setApplicationContext(final ApplicationContext appCtx)
	{
		applicationContext = appCtx;
	}

	private static ApplicationContext getApplicationContext()
	{
		if (applicationContext == null)
		{
			applicationContext = Registry.getApplicationContext();
		}
		return applicationContext;
	}
}
