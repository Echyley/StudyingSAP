/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.DisplayCsticReadonlyRAO;

import java.util.Map;


/**
 * Encapsulates logic of displaying characteristic as read-only as rule action.
 */
public class DisplayCsticReadonlyRAOAction extends ProductConfigAbstractRAOAction
{

	@Override
	public boolean performActionInternal(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();
		logRuleData(context, parameters, CSTIC_NAME);
		boolean processed = false;

		if (validateProcessStep(context, parameters, ProcessStep.RETRIEVE_CONFIGURATION))
		{
			final CsticRAO csticRao = createCsticRAO(parameters);

			final DisplayCsticReadonlyRAO displayCsticReadonlyRAO = new DisplayCsticReadonlyRAO();
			displayCsticReadonlyRAO.setAppliedToObject(csticRao);

			updateContext(context, displayCsticReadonlyRAO);
			processed = true;
		}
		return processed;
	}


	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		return "Hence skipping display characteristic read-only for cstic " + csticName + ".";
	}
}
