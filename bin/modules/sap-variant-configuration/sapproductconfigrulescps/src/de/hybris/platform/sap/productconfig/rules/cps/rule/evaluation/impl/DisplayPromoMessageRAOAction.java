/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.ProductConfigPromoMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl.ProductConfigAbstractRAOAction;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import java.util.Map;


/**
 * Encapsulates display promo message logic as rule action.
 */
public class DisplayPromoMessageRAOAction extends ProductConfigAbstractRAOAction
{
	/**
	 * Extended message text used in the DisplayPromoMessage action definition.
	 */
	protected static final String EXTENDED_MESSAGE = "extended_message";

	private static final String LOG_TEXT_PROMO_MESSAGE = "Hence skipping display promo message";

	@Override
	public boolean performActionInternal(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();
		logRuleData(context, parameters, CSTIC_NAME, CSTIC_VALUE, MESSAGE, EXTENDED_MESSAGE);
		boolean processed = false;

		if (validateProcessStep(context, parameters, ProcessStep.RETRIEVE_CONFIGURATION))
		{
			final ProductConfigPromoMessageRAO promoMessageRao = new ProductConfigPromoMessageRAO();
			prepareMessageData(parameters, promoMessageRao);
			updateContext(context, promoMessageRao);
			processed = true;
		}
		return processed;
	}

	protected void prepareMessageData(final Map<String, Object> parameters, final ProductConfigPromoMessageRAO promoMessageRao)
	{
		final CsticRAO csticRao = createCsticRAO(parameters);
		final CsticValueRAO valueRao = createCsticValueRAO(parameters);

		final String messageText = (String) parameters.get(MESSAGE);
		final String extendedMessageText = (String) parameters.get(EXTENDED_MESSAGE);

		promoMessageRao.setAppliedToObject(csticRao);
		promoMessageRao.setMessage(messageText);
		promoMessageRao.setExtendedMessage(extendedMessageText);
		promoMessageRao.setValueName(valueRao);
		promoMessageRao.setPromoType(getPromoType());
	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		final String csticValue = (String) parameters.get(CSTIC_VALUE);
		final String messageText = (String) parameters.get(MESSAGE);
		final String extendedMessageText = (String) parameters.get(EXTENDED_MESSAGE);

		return getLogText() + " for characteristic " + csticName + " , value " + csticValue + " , message \"" + messageText
				+ "\" , extended message text \"" + extendedMessageText + "\" , promo type " + getPromoType().toString();
	}

	protected ProductConfigMessagePromoType getPromoType()
	{
		return ProductConfigMessagePromoType.PROMO_APPLIED;
	}

	protected String getLogText()
	{
		return LOG_TEXT_PROMO_MESSAGE;
	}

}
