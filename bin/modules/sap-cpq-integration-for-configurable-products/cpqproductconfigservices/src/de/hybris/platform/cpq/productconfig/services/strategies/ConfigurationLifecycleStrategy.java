/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies;

import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;


/**
 * Responsible for creating, cloning, deleting configurations
 */
public interface ConfigurationLifecycleStrategy
{

	/**
	 * @param configId
	 *           Identifies the CPQ configuration
	 * @return Configuration Summary
	 */
	ConfigurationSummaryData getConfigurationSummary(String configId);

	/**
	 * Creates a default configuration
	 *
	 * @param productCode
	 *           product code
	 * @param ownerId
	 *           owner of the configuration, business context of this owner will be applied to the configuration
	 * @return Configuration ID
	 */
	String createConfiguration(String productCode, String ownerId);

	/**
	 * Clones a configuration
	 *
	 * @param configId
	 *           Configuration ID
	 * @param isPermanent
	 *           specifies whether cloned config should be permanent
	 * @return Configuration ID belonging to new configuration
	 */
	String cloneConfiguration(String configId, boolean isPermanent);

	/**
	 * Deletes a configuration
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Has deletion been successful?
	 */
	boolean deleteConfiguration(String configId);

	/**
	 * Marks a persisted configuration as permanent.<br>
	 * Should be called when the UI is 'done' configuring. Afterwards admin/server is required to do any changes.
	 * UI/Client scope is not sufficient anymore to do changes.
	 *
	 * @param configId
	 *           config id
	 */
	void makeConfigurationPermanent(String configId);

}
