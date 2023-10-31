/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;


import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingConfigurationParameter;

import java.util.Collection;


/**
 * Default implementation of {@link PricingConfigurationParameterCPS}
 */
public class DefaultPricingConfigurationParameterCPS extends DefaultPricingConfigurationParameter
		implements PricingConfigurationParameterCPS
{

	@Override
	public String getTargetForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_baseprice_cps();
	}

	@Override
	public String getTargetForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_selectedoptions_cps();
	}

	@Override
	public String getPricingProcedure()
	{
		return getSAPConfiguration().getSapproductconfig_pricingprocedure_cps();
	}

	@Override
	public SAPProductConfigPricingDetailsMode getPricingDetailsMode()
	{
		return getSAPConfiguration().getSapproductconfig_pricingdetailsmode();
	}

	@Override
	public String getSubTotalForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_subtotal_baseprice_cps();
	}

	@Override
	public String getSubTotalForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_subtotal_selectedoptions_cps();
	}

	@Override
	public Collection<String> getConditionTypesForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_conditiontypes_baseprice_cps();
	}

	@Override
	public Collection<String> getConditionTypesForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_conditiontypes_selectedoptions_cps();
	}

}
