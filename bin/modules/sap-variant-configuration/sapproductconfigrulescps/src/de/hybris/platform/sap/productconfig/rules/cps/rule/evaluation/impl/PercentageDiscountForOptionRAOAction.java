/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.PercentageDiscountForOptionWithMessageRAO;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Encapsulates percentage discount for option logic as rule action.
 */
public class PercentageDiscountForOptionRAOAction extends DisplayPromoMessageRAOAction
{

	/**
	 * Percentage Discount used in the PercentageDiscountForOption action definition.
	 */
	protected static final String DISCOUNT_VALUE = "discount_value";

	private static final String LOG_TEXT_DISCOUNT = "Hence skipping percentage discount for option";


	@Override
	public boolean performActionInternal(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();
		logRuleData(context, parameters, CSTIC_NAME, CSTIC_VALUE, MESSAGE, EXTENDED_MESSAGE, DISCOUNT_VALUE);
		boolean processed = false;

		if (validateProcessStep(context, parameters, ProcessStep.RETRIEVE_CONFIGURATION))
		{
			final PercentageDiscountForOptionWithMessageRAO percentageDiscountForOptionRAO = new PercentageDiscountForOptionWithMessageRAO();
			prepareMessageData(parameters, percentageDiscountForOptionRAO);

			final BigDecimal discountValue = (BigDecimal) parameters.get(DISCOUNT_VALUE);
			percentageDiscountForOptionRAO.setDiscountValue(discountValue);

			updateContext(context, percentageDiscountForOptionRAO);
			processed = true;
		}
		return processed;
	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final BigDecimal discountValue = (BigDecimal) parameters.get(DISCOUNT_VALUE);
		return super.prepareActionLogText(context, parameters) + ", " + discountValue + "%";
	}

	@Override
	protected String getLogText()
	{
		return LOG_TEXT_DISCOUNT;
	}

}
