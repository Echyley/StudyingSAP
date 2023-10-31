/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import java.util.List;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;


/**
 * Configuration service.
 */
public interface ConfigurationService
{
	/**
	 * Creates a new configuration, based on a root product code
	 *
	 * @param productCode
	 *           Root product code
	 * @return Configuration Identifier
	 */
	String createConfiguration(String productCode);

	/**
	 * Retrieves the CPQ configuration summary
	 *
	 * @param configId
	 *           CPQ configuration Id
	 *
	 * @return CPQ configuration summary
	 */
	ConfigurationSummaryData getConfigurationSummary(String configId);

	/**
	 * Delete the configuration for given configId
	 *
	 * @param configId
	 *           CPQ configuration Id
	 * @return true if deletion was successful
	 */
	boolean deleteConfiguration(String configId);

	/**
	 * Clones the configuration for given configId
	 *
	 * @param configId
	 *           CPQ configuration Id
	 * @param permanent
	 *           indicates whether clone should be permanent
	 * @return CPQ configuration Id of cloned configuration
	 */
	String cloneConfiguration(String configId, boolean permanent);

	/**
	 * Removes a cached configuration summary if present. Tolerates summary not being present
	 *
	 * @param configId
	 *           Configuration identifier
	 */
	void removeCachedConfigurationSummary(String configId);

	/**
	 * Checks if a configuration has issues
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Issues?
	 */
	boolean hasConfigurationIssues(String configId);

	/**
	 * get's the total number of configuration issues
	 *
	 * @param configId
	 *           Identifies the CPQ configuration
	 * @return number of configuration issues
	 */
	int getNumberOfConfigurationIssues(String configId);

	/**
	 * Marks a persisted configuration as permanent.<br>
	 * Should be called when the UI is 'done' configuring. Afterwards admin/server is required to do any changes.
	 * UI/Client scope is not sufficient anymore to do changes.
	 *
	 * @param configId
	 *           config id
	 */
	void makeConfigurationPermanent(String configId);


	/**
	 * get's the list of line items
	 *
	 * @param configId
	 *           Identifies the CPQ configuration
	 * @return line items
	 */
	List<ConfigurationSummaryLineItemData> getLineItems(final String configId);


	/**
	 * Retrieves the CPQ configuration summary. As a rule, CPQ configuration can be retrieved only for the origin user or a
	 * user from the same company. However, there are some processes in commerce which run asynchronous with the anonymous
	 * user and need to retrieve the CPQ configuration. In these cases, the required authorization is already verified by
	 * the parent process. This method allows such processes to retrieve the CPQ configuration summary even if the process
	 * is running with anonymous user.
	 *
	 * @param configId
	 *                    CPQ configuration Id
	 * @param entry
	 *                    entry containing this CPQ configurable product
	 * @return CPQ configuration summary
	 */
	default ConfigurationSummaryData getConfigurationSummary(final String configId, final AbstractOrderEntryModel entry)
	{
		return getConfigurationSummary(configId);
	}

	/**
	 * Get's the total number of configuration issues. As a rule, CPQ configuration can be retrieved only for the origin
	 * user or a user from the same company. However, there are some processes in commerce which run asynchronous with the
	 * anonymous user and need to retrieve the CPQ configuration. In these cases, the required authorization is already
	 * verified by the parent process. This method allows such processes to retrieve the number of issues in the CPQ
	 * configuration even if the process is running with anonymous user.
	 *
	 * @param configId
	 *                    Identifies the CPQ configuration
	 * @param entry
	 *                    entry containing this CPQ configurable product
	 * @return number of configuration issues
	 */
	default int getNumberOfConfigurationIssues(final String configId, final AbstractOrderEntryModel entry)
	{
		return 0;
	}

	/**
	 * Checks if a configuration has issues. As a rule, CPQ configuration can be retrieved only for the origin user or a
	 * user from the same company. However, there are some processes in commerce which run asynchronous with the anonymous
	 * user and need to retrieve the CPQ configuration. In these cases, the required authorization is already verified by
	 * the parent process. This method allows such processes to verify whether the CPQ configuration has issues even if the
	 * process is running with anonymous user.
	 *
	 * @param configId
	 *                    Configuration ID
	 * @param entry
	 *                    entry containing this CPQ configurable product
	 * @return Issues?
	 */
	default boolean hasConfigurationIssues(final String configId, final AbstractOrderEntryModel entry)
	{
		return false;
	}

}
