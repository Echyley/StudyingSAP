/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;


/**
 * Compares build number of knowledgebases to those of the returned runtime configurations. Thereby it can detect
 * outdated data on the hybris side.
 */
public interface KnowledgebaseKeyComparator
{
	/**
	 * Compares build number of the provided runtime configuration with those of the associated knowledgebase.
	 * 
	 * @param runtimeConfiguration
	 *           runtime configuration
	 * @return status of the build number synchronization
	 */
	KnowledgebaseBuildSyncStatus retrieveKnowledgebaseBuildSyncStatus(final CPSConfiguration runtimeConfiguration);
}
