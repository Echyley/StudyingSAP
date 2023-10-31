/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseBuildSyncStatus;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseKeyComparator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



public class KnowledgebaseKeyComparatorImpl implements KnowledgebaseKeyComparator
{
	private static final Logger LOG = Logger.getLogger(KnowledgebaseKeyComparatorImpl.class);
	private ConfigurationMasterDataService masterDataService;

	@Override
	public KnowledgebaseBuildSyncStatus retrieveKnowledgebaseBuildSyncStatus(final CPSConfiguration runtimeConfiguration)
	{
		final String kbId = runtimeConfiguration.getKbId();
		final Integer buildNumberRuntime = runtimeConfiguration.getKbBuild();
		final Integer buildNumber = getMasterDataService().getKbBuildNumber(kbId);

		final int compare = buildNumber.compareTo(buildNumberRuntime);
		if (compare == 0)
		{
			return logAndReturn(KnowledgebaseBuildSyncStatus.IN_SYNC, runtimeConfiguration);
		}
		if (compare < 0)
		{
			getMasterDataService().removeCachedKb(kbId);
			return logAndReturn(KnowledgebaseBuildSyncStatus.OUTDATED_MASTER_DATA, runtimeConfiguration);
		}
		return logAndReturn(KnowledgebaseBuildSyncStatus.OUTDATED_RUNTIME, runtimeConfiguration);
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	protected KnowledgebaseBuildSyncStatus logAndReturn(final KnowledgebaseBuildSyncStatus syncStatus,
			final CPSConfiguration configuration)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Knowledgebase build number sync status: " + syncStatus.toString() + " for configId " + configuration.getId());
		}
		return syncStatus;
	}

	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

}
