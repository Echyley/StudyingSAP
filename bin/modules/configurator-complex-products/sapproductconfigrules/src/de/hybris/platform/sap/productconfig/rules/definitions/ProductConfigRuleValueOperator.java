/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.definitions;

import de.hybris.platform.ruleengineservices.definitions.RuleParameterEnum;


/**
 * Enum representing possible operators used for product config rule condition definition. A typical rule condition may
 * looks like:<br>
 * Cstic 'XY' <code>[HAS/DOES_NOT_HAVE]</code> value 'xyz'.
 */
public enum ProductConfigRuleValueOperator implements RuleParameterEnum
{
	/**
	 * Example: Cstic 'Color' <code>HAS</code> value 'blue'.
	 */
	HAS,
	/**
	 * Example: Cstic 'Color' <code>DOES_NOT_HAVE</code> value 'red'.
	 */
	DOES_NOT_HAVE
}
