/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.AbstractRuleExecutableSupport;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOAction;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Abstract base class for product configuration related RAOAction classes. Provides some common base functionality.
 * such as:<br>
 * <ul>
 * <li>{@link ProcessStep} validation, to ensure that the rule is only applied at correct process step</li>
 * <li>logging</li>
 * <li>rule framework related helper methods</li>
 * </ul>
 */
public abstract class ProductConfigAbstractRAOAction extends AbstractRuleExecutableSupport implements RAOAction
{

	private static final String EMPTY_VALUE = "";

	/**
	 * Characteristic parameter name used in the related action definition.
	 */
	protected static final String CSTIC_NAME = "cstic";

	/**
	 * Characteristic Value parameter name used in the related action definition.
	 */
	protected static final String CSTIC_VALUE = "cstic_value";

	/**
	 * Message text used in the DisplayMessage action definition.
	 */
	protected static final String MESSAGE = "message";

	private static final Logger LOGGER = Logger.getLogger(ProductConfigAbstractRAOAction.class);

	protected boolean validateProcessStep(final RuleActionContext context, final Map<String, Object> parameters,
			final ProcessStep... expectedProcessSteps)
	{

		final ProductConfigProcessStepRAO processStepRAO = context.getValue(ProductConfigProcessStepRAO.class);
		final ProcessStep processStep = processStepRAO.getProcessStep();

		boolean processStepValid = false;
		for (final ProcessStep expectedProcessStep : expectedProcessSteps)
		{
			processStepValid = processStepValid || expectedProcessStep.equals(processStep);
		}

		if (!processStepValid && LOGGER.isDebugEnabled())
		{
			final String actionSpecificLogText = prepareActionLogText(context, parameters);
			LOGGER.debug("ProcessStep is " + processStep + ". Action only valid for " + Arrays.toString(expectedProcessSteps) + ". "
					+ actionSpecificLogText);
		}

		return processStepValid;
	}

	protected abstract String prepareActionLogText(RuleActionContext context, Map<String, Object> parameters);

	protected void updateContext(final RuleActionContext context, final AbstractRuleActionRAO actionRAO)
	{
		context.getRuleEngineResultRao().getActions().add(actionRAO);
		setRAOMetaData(context, actionRAO);
	}

	protected void logRuleData(final RuleActionContext context, final Map<String, Object> parameters,
			final String... parameterNames)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("RAOAction: " + this.getClass());
			LOGGER.debug("ProcessStep: " + context.getValue(ProductConfigProcessStepRAO.class).getProcessStep());
			for (final String parameterName : parameterNames)
			{
				LOGGER.debug(parameterName + ": " + parameters.get(parameterName));
			}
		}
	}

	protected CsticRAO createCsticRAO(final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName(csticName);
		return csticRao;
	}

	protected CsticValueRAO createCsticValueRAO(final Map<String, Object> parameters)
	{
		String csticValue = (String) parameters.get(CSTIC_VALUE);
		if (csticValue == null)
		{
			csticValue = EMPTY_VALUE;
		}
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(csticValue);
		return valueRao;
	}

	@Override
	public void performAction(final RuleActionContext context)
	{
		validateParameters(context.getParameters());
		validateRule(context);

		if (performActionInternal(context))
		{
			trackActionExecution(context);
			context.updateScheduledFacts();
		}
	}

}
