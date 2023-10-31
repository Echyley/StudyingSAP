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
 * Default implementation of the pricing provider
 */
public class DefaultPricingProviderImpl implements PricingProvider
{

	private static final String THE_PRICING_PROVIDER_IS_NOT_SUPPORTED = "The pricing provider is not supported by default but requires specific runtime implementation";

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

}
