/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Retrieves providers and allows for switching between different provider factories
 */
public interface SwitchableProviderFactory extends ProviderFactory
{
	/**
	 * Activates the specified provider factory. After calling this method all provider requests will be processed by the
	 * specified factory.
	 *
	 * @param providerFactoryBeanName
	 *           provider factory to activate
	 */
	void switchProviderFactory(final String providerFactoryBeanName);

	/**
	 * @param providerFactoryBeanName
	 *           bean name to check
	 * @return <code>true</code>, only if the given provider factory is available
	 */
	boolean isProviderFactoryAvailable(String providerFactoryBeanName);
}
