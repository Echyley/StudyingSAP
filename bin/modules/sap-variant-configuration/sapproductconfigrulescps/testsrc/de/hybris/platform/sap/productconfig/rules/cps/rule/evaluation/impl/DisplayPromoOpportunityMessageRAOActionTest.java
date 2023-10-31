/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayPromoOpportunityMessageRAOActionTest extends AbstractRuleEngineTest
{
	private DisplayPromoOpportunityMessageRAOAction action;

	@Before
	public void setUp()
	{
		action = new DisplayPromoOpportunityMessageRAOAction();
	}

	@Test
	public void testDisplayPromoOpportunityMessageGetgetPromoType()
	{
		assertEquals(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, action.getPromoType());
	}
}
