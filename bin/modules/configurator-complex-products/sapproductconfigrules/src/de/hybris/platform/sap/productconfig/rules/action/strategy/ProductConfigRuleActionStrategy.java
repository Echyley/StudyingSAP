/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.action.strategy;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Defines a strategy that encapsulates the logic of a rule action.
 */
public interface ProductConfigRuleActionStrategy
{
	/**
	 * Applies the action described by the given {@link AbstractRuleActionRAO}.
	 *
	 * @param model
	 *           product configuration model to be adjusted
	 * @param action
	 *           the action to apply
	 * @return true if model is adjusted
	 */
	boolean apply(ConfigModel model, AbstractRuleActionRAO action);
}
