/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.util;

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
 * A utility for loading and cleaning data in all essentialdata impex files inside inboundservices module.
 */
public class InboundServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(integrationServicesEssentialData());
	private static final Set<String> ALL_INTEGRATION_OBJECT_CODES = new HashSet<>();
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(
			new InboundServicesImpex(), new InboundItemCleanupJobsImpex());
	private static final InboundServicesEssentialData instance = new InboundServicesEssentialData();

	private InboundServicesEssentialData()
	{
		super(IMPEX_FILES, ALL_INTEGRATION_OBJECT_CODES);
	}

	public static InboundServicesEssentialData inboundServicesEssentialData()
	{
		return instance;
	}

	public InboundServicesEssentialData withDependencies() {
		enableDependencies();
		return this;
	}

	@Nonnull
	@Override
	protected List<ModuleEssentialData> getDependencies()
	{
		return DEPENDENCIES;
	}

	private static class InboundServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-inboundservices.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES, "InboundIntegrationMonitoring");

		private InboundServicesImpex()
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

	private static class InboundItemCleanupJobsImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-inbound-item-cleanup-jobs.impex";
		private static final Set<String> CRON_JOBS_IN_FILE = Set.of("inboundRequestCleanupCronJob", "inboundRequestMediaCleanupCronJob");
		private static final Set<String> RETENTION_JOBS_IN_FILE = Set.of("inboundRequestCleanupJob", "inboundRequestMediaCleanupJob");
		private static final Set<String> RETENTION_RULES_IN_FILE = Set.of("inboundRequestCleanupRule", "inboundRequestMediaCleanupRule");

		private InboundItemCleanupJobsImpex()
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
