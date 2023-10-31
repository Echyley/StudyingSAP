/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;

import java.util.Map;


/**
 * Encapsulates logic of removing of a characteristic assignable value as rule action.
 */
public class RemoveAssignableValueRAOAction extends ProductConfigAbstractRAOAction
{

	@Override
	public boolean performActionInternal(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();
		logRuleData(context, parameters, CSTIC_NAME, CSTIC_VALUE);
		boolean processed = false;

		if (validateProcessStep(context, parameters, ProcessStep.RETRIEVE_CONFIGURATION))
		{
			final CsticRAO csticRao = createCsticRAO(parameters);
			final CsticValueRAO valueRao = createCsticValueRAO(parameters);

			final RemoveAssignableValueRAO removeAssignableValueRAO = new RemoveAssignableValueRAO();
			removeAssignableValueRAO.setAppliedToObject(csticRao);
			removeAssignableValueRAO.setValueNameToRemoveFromAssignable(valueRao);

			updateContext(context, removeAssignableValueRAO);
			processed = true;
		}
		return processed;
	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		final String csticValue = (String) parameters.get(CSTIC_VALUE);
		return "Hence skipping removal of assignable value " + csticValue + " for cstic " + csticName + ".";
	}
}
