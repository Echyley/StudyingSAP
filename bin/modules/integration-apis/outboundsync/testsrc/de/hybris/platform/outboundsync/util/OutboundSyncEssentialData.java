/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.util;

import static de.hybris.platform.outboundservices.util.OutboundServicesEssentialData.outboundServicesEssentialData;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.impex.EssentialDataFile;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel;
import de.hybris.platform.processing.model.AfterRetentionCleanupRuleModel;
import de.hybris.platform.servicelayer.internal.model.RetentionJobModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essentialdata impex files inside outboundsync module.
 */
public class OutboundSyncEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(outboundServicesEssentialData());
	private static final Set<String> ALL_INTEGRATION_OBJECT_CODES = new HashSet<>();
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(
			new OutboundSyncImpex(), new OutboundSyncRetryCleanupJobsImpex(), new OutboundSyncSetupImpex());
	private static final String OUTBOUND_JOB = "outboundSyncCronJob";
	private static final OutboundSyncEssentialData instance = new OutboundSyncEssentialData();
	private static OutboundSyncCronJobModel contextCronJob;

	private OutboundSyncEssentialData()
	{
		super(IMPEX_FILES, ALL_INTEGRATION_OBJECT_CODES);
	}

	public static OutboundSyncEssentialData outboundSyncEssentialData()
	{
		return instance;
	}

	public OutboundSyncEssentialData withDependencies()
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

	private static class OutboundSyncSetupImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-outboundsync-setup.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES,
				"OutboundChannelConfig");

		private OutboundSyncSetupImpex()
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

	private static class OutboundSyncImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-outboundsync.impex";
		private static final Set<String> CRON_JOBS_IN_FILE = Set.of(OUTBOUND_JOB);
		private static final Set<String> JOBS_IN_FILE = Set.of("odataOutboundSyncJob");
		private static final Set<String> CONTAINERS_IN_FILE = Set.of("outboundSyncDataStreams");

		private OutboundSyncImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(OutboundSyncCronJobModel.class, j -> CRON_JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(OutboundSyncJobModel.class, j -> JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(OutboundSyncStreamConfigurationContainerModel.class,
					c -> CONTAINERS_IN_FILE.contains(c.getId()));
		}
	}

	private static class OutboundSyncRetryCleanupJobsImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-outboundsync-retry-cleanup-jobs.impex";
		private static final Set<String> CRON_JOBS_IN_FILE = Set.of("outboundRetryCleanupCronJob");
		private static final Set<String> RETENTION_JOBS_IN_FILE = Set.of("outboundRetryCleanupJob");
		private static final Set<String> RETENTION_RULES_IN_FILE = Set.of("outboundRetryCleanupRule");

		private OutboundSyncRetryCleanupJobsImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			contextCronJob = null;
			IntegrationTestUtil.remove(RetentionJobModel.class, j -> RETENTION_JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(CronJobModel.class, j -> CRON_JOBS_IN_FILE.contains(j.getCode()));
			IntegrationTestUtil.remove(AfterRetentionCleanupRuleModel.class, r -> RETENTION_RULES_IN_FILE.contains(r.getCode()));
		}
	}

	/**
	 * Retrieves the outbound sync job configured in this essential data.
	 *
	 * @return the job model or {@code null}, if there are no outbound sync jobs yet exist in the persistent storage.
	 */
	public static CronJobModel outboundCronJob()
	{
		if (contextCronJob == null)
		{
			contextCronJob = IntegrationTestUtil.findAny(OutboundSyncCronJobModel.class, m -> OUTBOUND_JOB.equals(m.getCode()))
			                                    .orElse(null);
		}
		return contextCronJob;
	}
}
