/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.compiler.processors;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRuleCPSIrProcessorTest
{
	private ProductConfigRuleCPSIrProcessor classUnderTest;
	private RuleIr ruleIr;
	private ProductConfigSourceRuleModel productConfigSourceRule;

	@Mock
	private RuleCompilerContext context;
	
	private static final String CONFIGURABLE_PRODUCT_IN_CART_CONDITION = "... y_configurable_product_in_cart ...";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigRuleCPSIrProcessor();

		ruleIr = new RuleIr();
		productConfigSourceRule = new ProductConfigSourceRuleModel();

		given(context.getRule()).willReturn(productConfigSourceRule);
	}
	
	private void processConditionsAndActions(final String condition, final String action)
	{
		productConfigSourceRule.setConditions(condition);
		productConfigSourceRule.setActions(action);
		// RuleCompilerException expected
		classUnderTest.process(context, ruleIr);
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndDiscountAction()
	{
		processConditionsAndActions(CONFIGURABLE_PRODUCT_IN_CART_CONDITION,
				"... y_configurable_product_percentage_discount_for_option ...");
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndPromoMessageAction()
	{
		processConditionsAndActions(CONFIGURABLE_PRODUCT_IN_CART_CONDITION, "... y_configurable_product_display_promo_message ...");
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndPromoOpportunityMessageAction()
	{
		processConditionsAndActions(CONFIGURABLE_PRODUCT_IN_CART_CONDITION,
				"... y_configurable_product_display_promo_opportunity_message ...");
	}

	@Test(expected = None.class)
	public void testProcessNoProductInCartNoPromoActions()
	{
		processConditionsAndActions("No ProductInCart Condition", "No Promo Actions");
	}

	@Test(expected = None.class)
	public void testProcessPromoActionAndNoProductInCart()
	{
		processConditionsAndActions("No ProductInCart Condition", "... y_configurable_product_percentage_discount_for_option ...");
	}

	@Test(expected = None.class)
	public void testProcessProductInCartAndNoPromoAction()
	{
		processConditionsAndActions(CONFIGURABLE_PRODUCT_IN_CART_CONDITION, "No Promo Actions");
	}

	@Test(expected = None.class)
	public void testProcessProductInCartAndDiscountActionNotProductConfigRule()
	{
		final SourceRuleModel sourceRule = new SourceRuleModel();
		sourceRule.setConditions(CONFIGURABLE_PRODUCT_IN_CART_CONDITION);
		sourceRule.setActions("... y_configurable_product_percentage_discount_for_option ...");
		given(context.getRule()).willReturn(sourceRule);
		// No exception expected
		classUnderTest.process(context, ruleIr);
	}
}
