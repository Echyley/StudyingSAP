/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;


/**
 * Takes care of copying data that is related to product configuration. Will do nothing in case a pure product
 * configuration scenario is deployed, will e.g. take care of rule artifacts.
 */
public interface ConfigurationDependencyHandler
{
	/**
	 * Copies dependencies from the source ProductConfiguration with given Id to the target one ProductConfiguration
	 *
	 * @param sourceConfigId
	 *           source configuration Id
	 * @param targetConfigId
	 *           configuration Id
	 */
	void copyProductConfigurationDependency(String sourceConfigId, String targetConfigId);

}
