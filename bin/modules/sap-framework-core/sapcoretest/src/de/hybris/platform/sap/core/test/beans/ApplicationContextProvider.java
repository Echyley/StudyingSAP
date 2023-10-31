/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Bean which provides the Spring application context.
 */
public class ApplicationContextProvider implements ApplicationContextAware
{

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

}
