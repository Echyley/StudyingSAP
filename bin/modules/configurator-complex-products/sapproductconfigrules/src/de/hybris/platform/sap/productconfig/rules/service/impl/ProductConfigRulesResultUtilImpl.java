/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ProductConfigRulesResultUtilImpl implements ProductConfigRulesResultUtil
{

	@Override
	public List<ProductConfigurationDiscount> retrieveRulesBasedVariantConditionModifications(final String configId)
	{
		return Collections.emptyList();
	}

	@Override
	public void deleteRulesResultsByConfigId(final String configId)
	{
		return;
	}

	@Override
	public Map<String, Map<String, List<ProductConfigMessage>>> retrieveDiscountMessages(final String configId)
	{
		return Collections.emptyMap();
	}
}
