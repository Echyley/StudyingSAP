/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;


/**
 * Encapsulates display promo opportunity message logic as rule action.
 */
public class DisplayPromoOpportunityMessageRAOAction extends DisplayPromoMessageRAOAction
{
	@Override
	protected ProductConfigMessagePromoType getPromoType()
	{
		return ProductConfigMessagePromoType.PROMO_OPPORTUNITY;
	}
}
