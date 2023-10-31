/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Default implementation for the Pricing Provider for the tests
 *
 */
public class DummyPricingProvider implements PricingProvider
{

	private static final String USE_PRICING_PROVIDER_GET_PRICE_SUMMARY_STRING_CONFIGURATION_RETRIEVAL_OPTIONS = "Use PricingProvider#getPriceSummary(String, ConfigurationRetrievalOptions)";


	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		return null;
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		return;
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel)
	{
		return;
	}
}
