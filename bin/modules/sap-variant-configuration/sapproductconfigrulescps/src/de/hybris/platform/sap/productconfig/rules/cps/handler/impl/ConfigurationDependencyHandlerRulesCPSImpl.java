/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.handler.impl;

import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDependencyHandler;

import org.springframework.beans.factory.annotation.Required;


public class ConfigurationDependencyHandlerRulesCPSImpl implements ConfigurationDependencyHandler
{

	CharacteristicValueRulesResultHandler rulesResultHandler;

	@Override
	public void copyProductConfigurationDependency(final String sourceConfigId, final String targetConfigId)
	{
		getRulesResultHandler().copyAndPersistRuleResults(sourceConfigId, targetConfigId);
	}

	protected CharacteristicValueRulesResultHandler getRulesResultHandler()
	{
		return rulesResultHandler;
	}

	@Required
	public void setRulesResultHandler(final CharacteristicValueRulesResultHandler rulesResultHandler)
	{
		this.rulesResultHandler = rulesResultHandler;
	}

}
