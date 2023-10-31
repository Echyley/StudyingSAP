/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;

import java.math.BigDecimal;


/**
 * Populates variant conditions
 */
public class VariantConditionPopulator implements Populator<CPSVariantCondition, VariantConditionModel>
{

	@Override
	public void populate(final CPSVariantCondition source, final VariantConditionModel target)
	{
		target.setKey(source.getKey());
		target.setFactor(new BigDecimal(source.getFactor()));
	}
}
