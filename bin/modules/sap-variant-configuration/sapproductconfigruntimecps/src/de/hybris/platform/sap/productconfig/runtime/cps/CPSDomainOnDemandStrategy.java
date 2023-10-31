/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.List;


/**
 * Handles requesting of domains on demand, when required
 */
public interface CPSDomainOnDemandStrategy
{
	/**
	 * Completes a configuration model with regards to domains that still need to be read. The groups for which domain
	 * values are requested contain the current group + groups that participate in conflicts
	 * 
	 * @param configModel
	 *           Model
	 * @param currentGroup
	 *           The group that should be interactively processed, and for which we need to request the domain values
	 * @throws ConfigurationEngineException
	 *            Something went wrong with the charon call
	 */
	void processRequiredGroups(ConfigModel configModel, String currentGroup) throws ConfigurationEngineException;

	/**
	 * Determines the first group of a configuration model
	 * 
	 * @param configModel
	 *           Model
	 * @return ID of first group
	 */
	String determineFirstGroup(ConfigModel configModel);

	/**
	 * Enriches the configuration model with data for an instance and a list of groups that belong to this instance
	 * 
	 * @param configModel
	 *           Configuration model
	 * @param instanceId
	 *           Instance ID
	 * @param groupNames
	 *           List of UI groups
	 * @throws ConfigurationEngineException
	 *            Something went wrong with the charon call
	 */
	void enrichModelWithGroupForInstance(ConfigModel configModel, String instanceId, List<String> groupNames)
			throws ConfigurationEngineException;

}
