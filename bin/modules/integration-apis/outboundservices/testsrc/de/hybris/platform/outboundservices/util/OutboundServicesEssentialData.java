/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.util;

import static de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData.integrationServicesEssentialData;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.impex.EssentialDataFile;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.processing.model.AfterRetentionCleanupRuleModel;
import de.hybris.platform.servicelayer.internal.model.RetentionJobModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essentialdata impex files inside outboundservices module.
 */
public class OutboundServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(integrationServicesEssentialData());
	private static final Set<String> ALL_INTEGRATION_OBJECT_CODES = new HashSet<>();
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(
			new OutboundServicesImpex(), new OutboundItemCleanupJobsImpex());
	private static final OutboundServicesEssentialData instance = new OutboundServicesEssentialData();

	private OutboundServicesEssentialData()
	{
		super(IMPEX_FILES, ALL_INTEGRATION_OBJECT_CODES);
	}

	public static OutboundServicesEssentialData outboundServicesEssentialData()
	{
		return instance;
	}

	public OutboundServicesEssentialData withDependencies()
	{
		enableDependencies();
		return this;
	}

	@Nonnull
	@Override
	protected List<ModuleEssentialData> getDependencies()
	{
		return DEPENDENCIES;
	}

	private static class OutboundServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-outboundservices.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES,
				"OutboundIntegrationMonitoring");

		private OutboundServicesImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			deleteInboundChannelConfigurations(IOs_IN_FILE);
			deleteIntegrationObjects(IOs_IN_FILE);
		}
	}

	private static class OutboundItemCleanupJobsImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-outbound-item-cleanup-jobs.impex";
		private static final Set<String> CRON_JOBS_IN_FILE = Set.of("outboundRequestCleanupCronJob",
				"outboundRequestMediaCleanupCronJob");
		private static final Set<String> RETENTION_JOBS_IN_FILE = Set.of("outboundRequestCleanupJob",
				"outboundRequestMediaCleanupJob");
		private static final Set<String> RETENTION_RULES_IN_FILE = Set.of("outboundRequestCleanupRule",
				"outboundRequestMediaCleanupRule");

		private OutboundItemCleanupJobsImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(RetentionJobModel.class, j -> RETENTION_JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(CronJobModel.class, j -> CRON_JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(AfterRetentionCleanupRuleModel.class, r -> RETENTION_RULES_IN_FILE.contains(r.getCode()));
		}
	}
}
