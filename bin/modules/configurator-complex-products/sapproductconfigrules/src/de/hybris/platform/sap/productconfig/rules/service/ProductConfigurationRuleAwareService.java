/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.service;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;


/**
 * ProductConfigurationRuleAwareService provides access to the rule based specific functionality of the configuration
 * engine implementation.
 *
 */
public interface ProductConfigurationRuleAwareService extends ProductConfigurationService
{

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format bypassing rule evaluation.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModelBypassRules(String configId);

	/**
	 * Creates the <code>ConfigModel</code> from the given <code>externalConfiguration</code> bypassing rule evaluation.
	 *
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @return The actual configuration
	 */
	ConfigModel createConfigurationFromExternalBypassRules(final KBKey kbKey, String externalConfiguration);
}
