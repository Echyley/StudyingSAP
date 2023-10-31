/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Retrieves an instance of the provider according to the hybris application configuration.
 */
public interface ProviderFactory
{

	/**
	 * Retrieves an instance of the configuration provider according to the hybris application configuration.
	 *
	 * @return instance of the configuration provider
	 */
	ConfigurationProvider getConfigurationProvider();

	/**
	 * Retrieves an instance of the pricing provider according to the hybris application configuration.
	 *
	 * @return instance of the pricing provider
	 */
	PricingProvider getPricingProvider();

	/**
	 * Retrieves an instance of the analytics provider
	 *
	 * @return instance of the analytics provider
	 */
	AnalyticsProvider getAnalyticsProvider();

	/**
	 * Retrieve an instance of the cstic value provider, needed for the backoffice rule editor
	 *
	 * @return instance of the cstic value parameter provider
	 */
	ProductCsticAndValueParameterProvider getProductCsticAndValueParameterProvider();

	/**
	 * Retrieve an instance of the pricing parameters
	 *
	 * @return instance of the pricing parameters
	 */
	PricingConfigurationParameter getPricingParameter();
}
