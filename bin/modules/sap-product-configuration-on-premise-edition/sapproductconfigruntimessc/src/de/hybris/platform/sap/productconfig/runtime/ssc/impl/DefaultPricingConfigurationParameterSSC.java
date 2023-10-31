/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.ssc.PricingConfigurationParameterSSC;


/**
 * Default implementation of {@link PricingConfigurationParameterSSC}
 */
public class DefaultPricingConfigurationParameterSSC extends DefaultPricingConfigurationParameter
		implements PricingConfigurationParameterSSC
{

	@Override
	public String getTargetForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_baseprice();
	}

	@Override
	public String getTargetForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_selectedoptions();
	}

	@Override
	public String getPricingProcedure()
	{
		return getSAPConfiguration().getSapproductconfig_pricingprocedure();
	}
}
