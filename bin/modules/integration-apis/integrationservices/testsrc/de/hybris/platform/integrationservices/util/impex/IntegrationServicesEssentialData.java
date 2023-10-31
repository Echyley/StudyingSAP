/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.impex;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;
import de.hybris.platform.servicelayer.internal.model.RetentionJobModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essential data impex files inside integrationservices module.
 */
public class IntegrationServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = Collections.emptyList();
	private static final Set<String> ALL_INTEGRATION_OBJECT_CODES = new HashSet<>();
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(new IntegrationServicesImpex(), new ItemCleanupJobsImpex(),
			new ScriptServicesImpex(), new RuntimeAttributesImpex());
	private static final IntegrationServicesEssentialData instance = new IntegrationServicesEssentialData();

	private IntegrationServicesEssentialData()
	{
		super(IMPEX_FILES, ALL_INTEGRATION_OBJECT_CODES);
	}

	/**
	 * Creates instance of this essential data.
	 *
	 * @return new instance if an instance of this essential data was not yet created; or the existing instance otherwise.
	 */
	public static IntegrationServicesEssentialData integrationServicesEssentialData()
	{
		return instance;
	}

	@Nonnull
	@Override
	protected List<ModuleEssentialData> getDependencies()
	{
		return DEPENDENCIES;
	}

	private static class IntegrationServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-integrationservices.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES, "IntegrationService");

		private IntegrationServicesImpex()
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

	private static class ItemCleanupJobsImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-item-cleanup-jobs.impex";

		private ItemCleanupJobsImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(CronJobModel.class, job -> "integrationApiMediaCleanupCronJob".equals(job.getCode()));
			IntegrationTestUtil.remove(RetentionJobModel.class, job -> "integrationApiMediaCleanupJob".equals(job.getCode()));
			IntegrationTestUtil.remove(FlexibleSearchRetentionRuleModel.class,
					r -> "integrationApiMediaCleanupRule".equals(r.getCode()));
		}
	}

	private static class ScriptServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-scriptservices.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES, "ScriptService");

		/**
		 * Instantiates this essential data file
		 */
		private ScriptServicesImpex()
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

	private static class RuntimeAttributesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-runtimeattributes.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES, "RuntimeAttributeService");

		private RuntimeAttributesImpex()
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
}
