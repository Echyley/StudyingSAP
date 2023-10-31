/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.definitions;

import de.hybris.platform.ruleengineservices.definitions.RuleParameterEnum;


/**
 * Enum representing possible severities used for product config rule messages.
 */
public enum ProductConfigRuleDisplayMessageSeverity implements RuleParameterEnum
{
	/**
	 * Related message will be shown as a warning
	 */
	WARNING,
	/**
	 * Related message will be shown as an info message
	 */
	INFO
}
