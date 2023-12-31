/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link ProviderFactory} which returns providers based on bean names
 */
public class DefaultRuntimeProviderFactoryImpl implements ProviderFactory
{
	private ConfigurationProvider configurationProvider;
	private PricingProvider pricingProvider;
	private AnalyticsProvider analyticsProvider;
	private ProductCsticAndValueParameterProvider productCsticAndValueParameterProvider;
	private PricingConfigurationParameter pricingParameter;

	@Required
	public void setConfigurationProvider(final ConfigurationProvider configurationProvider)
	{
		this.configurationProvider = configurationProvider;
	}

	@Required
	public void setPricingProvider(final PricingProvider pricingProvider)
	{
		this.pricingProvider = pricingProvider;
	}

	@Required
	public void setAnalyticsProvider(final AnalyticsProvider analyticsProvider)
	{
		this.analyticsProvider = analyticsProvider;
	}

	@Required
	public void setProductCsticAndValueParameterProvider(
			final ProductCsticAndValueParameterProvider productCsticAndValueParameterProvider)
	{
		this.productCsticAndValueParameterProvider = productCsticAndValueParameterProvider;
	}

	@Required
	public void setPricingParameter(final PricingConfigurationParameter pricingParameter)
	{
		this.pricingParameter = pricingParameter;
	}

	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		return configurationProvider;
	}

	@Override
	public PricingProvider getPricingProvider()
	{
		return pricingProvider;
	}

	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		return analyticsProvider;
	}

	@Override
	public ProductCsticAndValueParameterProvider getProductCsticAndValueParameterProvider()
	{
		return productCsticAndValueParameterProvider;
	}

	@Override
	public PricingConfigurationParameter getPricingParameter()
	{
		return pricingParameter;
	}

}
