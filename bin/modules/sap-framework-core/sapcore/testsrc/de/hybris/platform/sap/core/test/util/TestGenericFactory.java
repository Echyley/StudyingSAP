/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.util;

import de.hybris.platform.sap.core.common.util.DefaultGenericFactory;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Generic factory for tests with direct Spring access.
 */
public class TestGenericFactory extends DefaultGenericFactory implements ApplicationContextAware, DisposableBean
{

	/**
	 * Inject the Spring Application Context for testing.
	 * 
	 * @param applicationContext
	 *           Spring {@link ApplicationContext}
	 */
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		GenericFactoryProvider.setApplicationContext(applicationContext);
	}

	/**
	 * Resets the test Spring Application Context.
	 */
	@Override
	public void destroy()
	{
		GenericFactoryProvider.setApplicationContext(null);
	}

}
