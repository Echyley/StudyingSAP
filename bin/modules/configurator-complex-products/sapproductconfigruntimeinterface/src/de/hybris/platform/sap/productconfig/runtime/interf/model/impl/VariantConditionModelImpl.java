/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;

import java.math.BigDecimal;


/**
 * Default implementation of the {@link VariantConditionModel}
 */
public class VariantConditionModelImpl extends BaseModelImpl implements VariantConditionModel
{
	private String key;
	private BigDecimal factor;

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setKey(final String key)
	{
		this.key = key;
	}

	@Override
	public BigDecimal getFactor()
	{
		return factor;
	}

	@Override
	public void setFactor(final BigDecimal factor)
	{
		this.factor = factor;
	}

	@Override
	public String toString()
	{
		return "VariantConditionModelImpl [key=" + key + ", factor=" + factor + "]";
	}

	@Override
	public VariantConditionModel copy()
	{
		final VariantConditionModel copy = new VariantConditionModelImpl();
		copy.setKey(this.getKey());
		copy.setFactor(this.getFactor());
		return copy;
	}

}
