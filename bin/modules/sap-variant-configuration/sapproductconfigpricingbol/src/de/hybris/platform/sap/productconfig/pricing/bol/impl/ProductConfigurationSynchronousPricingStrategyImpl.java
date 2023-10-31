/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPricingStrategyImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;


/**
 * Configuration order integration for synchronous pricing
 */
public class ProductConfigurationSynchronousPricingStrategyImpl extends ProductConfigurationPricingStrategyImpl
		implements ProductConfigurationPricingStrategy
{
	private static final String EXCEPTION_MESSAGE_CALCULATE_BASE_PRICE = "Synchronous Pricing does not support price calculation via configuration service. Prices are obtained from the SAP Backend.";
	private SapPricingEnablementService sapPricingEnablementService;
	private static final Logger LOG = Logger.getLogger(ProductConfigurationSynchronousPricingStrategyImpl.class);

	@Override
	public PriceValue calculateBasePriceForConfiguration(final AbstractOrderEntryModel entry)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE_CALCULATE_BASE_PRICE);
		}
		return super.calculateBasePriceForConfiguration(entry);
	}

	@Override
	public boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			return updateCartEntryPricesSynchronous(entry, calculateCart, passedParameter);
		}
		return super.updateCartEntryPrices(entry, calculateCart, passedParameter);
	}

	protected boolean updateCartEntryPricesSynchronous(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (calculateCart)
		{
			try
			{
				if (passedParameter == null)
				{
					final CommerceCartParameter parameter = getParametersForCartUpdate(entry);
					getCommerceCartService().recalculateCart(parameter);
				}
				else
				{
					getCommerceCartService().recalculateCart(passedParameter);
				}
				return true;
			}
			catch (final CalculationException e)
			{
				LOG.error("Price could not be updated", e);
			}

		}
		return false;
	}

	@Override
	public boolean isCartPricingErrorPresent(final ConfigModel configModel)
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			return false;
		}
		return super.isCartPricingErrorPresent(configModel);
	}

	protected SapPricingEnablementService getSapPricingEnablementService()
	{
		return sapPricingEnablementService;
	}


	public void setSapPricingEnablementService(final SapPricingEnablementService sapPricingEnablementService)
	{
		this.sapPricingEnablementService = sapPricingEnablementService;
	}
}
