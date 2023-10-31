/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.util.Config;




/**
 * Responsible to populate characteristics
 */
public class PossibleValuePopulator implements Populator<CPSPossibleValue, CsticValueModel>
{

	@Override
	public void populate(final CPSPossibleValue source, final CsticValueModel target)
	{
		populateCoreAttributes(source, target);

	}

	protected String getEmptyValueConstant()
	{
		return Config.getString("sapproductconfigruntimecps.emptyValueReplacement",
				SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE);
	}

	protected void populateCoreAttributes(final CPSPossibleValue source, final CsticValueModel target)
	{
		final String intervalType = source.getIntervalType();
		if (!CPSIntervalType.isInterval(intervalType))
		{
			target.setName(source.getValueLow().isEmpty() ? getEmptyValueConstant() : source.getValueLow());
		}
		else
		{
			populateAttributesForIntervalCstics(source, target);
		}
		target.setSelectable(source.isSelectable());
		target.setDomainValue(source.isSelectable());
	}

	protected void populateAttributesForIntervalCstics(final CPSPossibleValue source, final CsticValueModel target)
	{
		final String valueLow = source.getValueLow();
		final String valueHigh = source.getValueHigh();

		final String interval;

		switch (CPSIntervalType.fromString(source.getIntervalType()))
		{
			case HALF_OPEN_RIGHT_INTERVAL:
			case CLOSED_INTERVAL:
			case HALF_OPEN_LEFT_INTERVAL:
			case OPEN_INTERVAL:
				interval = new StringBuilder().append(valueLow).append(" - ").append(valueHigh).toString();
				break;
			case INFINITY_TO_HIGH_OPEN_INTERVAL:
				interval = new StringBuilder().append("< ").append(valueHigh).toString();
				break;
			case INFINITY_TO_HIGH_CLOSED_INTERVAL:
				interval = new StringBuilder().append("≤ ").append(valueHigh).toString();
				break;
			case LOW_TO_INFINITY_OPEN_INTERVAL:
				interval = new StringBuilder().append("> ").append(valueLow).toString();
				break;
			case LOW_TO_INFINITY_CLOSED_INTERVAL:
				interval = new StringBuilder().append("≥ ").append(valueLow).toString();
				break;
			default:
				throw new IllegalStateException("");
		}

		target.setName(interval);
		target.setLanguageDependentName(interval);
	}
}
