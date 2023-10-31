/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import org.apache.commons.lang.NotImplementedException;


/**
 * Facade for Product Configuration.
 */
public interface ConfigurationFacade
{

	/**
	 * Get the default configuration for the given Knowledge Base. In case the product identified by the productCode of
	 * the KBkey is a variant, the runtime configuration of the corresponding base product is instantiated.
	 *
	 * @param kbKey
	 *           key of the Knowledge Base
	 * @return default configuration
	 */
	ConfigurationData getConfiguration(KBKeyData kbKey);

	/**
	 * Get the default configuration for the given Knowledge Base. In case the product identified by the productCode of
	 * the KBkey is a variant, the runtime configuration of the corresponding base product is instantiated. If parameter
	 * forceReset is true it will always return a new configuration independent of an existing configuration for the
	 * given product code.
	 *
	 * @param kbKey
	 *           key of the Knowledge Base
	 * @param forceReset
	 *           If true it returns a new configuration
	 * @return default configuration
	 */
	default ConfigurationData getConfiguration(final KBKeyData kbKey, final boolean forceReset)
	{
		throw new NotImplementedException(
				"Method readDefaultConfiguration(final String productCode, final boolean forceReset) not implemented");
	}

	/**
	 * Update the configuration with the values provided
	 *
	 * @param configuration
	 *           actual configuration
	 */
	void updateConfiguration(ConfigurationData configuration);

	/**
	 * Read the actual configuration from the Backend. Current values in the model will be overwritten. The result is
	 * expected to contain domain values, as it's used for the interactive configuration
	 *
	 * @param configuration
	 *           Configuration to be refreshed. Should contain the current group for display in
	 *           {@link ConfigurationData#getGroupIdToDisplay()}. If this is null, the first group will become the
	 *           current one
	 * @return actual configuration
	 */
	ConfigurationData getConfiguration(ConfigurationData configuration);

	/**
	 * Get the number of errors (conflict, not filled mandatory fields), as it is set at the cart item
	 *
	 * @param configId
	 *           ID of the configuration
	 * @return Total number of errors
	 */
	int getNumberOfErrors(String configId);

	/**
	 * Get the number of incomplete characteristics (not filled mandatory fields)
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Number of incomplete characteristics
	 */
	int getNumberOfIncompleteCstics(String configId);

	/**
	 * Get the number of solvable conflicts
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Number of solvable conflicts
	 */
	int getNumberOfSolvableConflicts(String configId);

	/**
	 * Check if a given configId is known by the configuration engine.
	 *
	 * @param configId
	 *           ID of the configuration
	 * @return TRUE if the configuration exist, otherwise FALSE
	 */
	default boolean isConfigurationAvailable(final String configId)
	{
		throw new IllegalStateException("Not supported");
	}

	/**
	 * Copies the values of another configuration that is already existing into a new configuration that is managed by
	 * commerce. This API does not support product variants
	 *
	 * @param kbKey
	 *           Key of the knowledge base
	 * @param configIdTemplate
	 *           ID of configuration that we use as template
	 * @return Configuration with existing values applied
	 */
	default ConfigurationData getConfigurationFromTemplate(final KBKeyData kbKey, final String configIdTemplate)
	{
		throw new IllegalStateException("Not supported");
	}
}
