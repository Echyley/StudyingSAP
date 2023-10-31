/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.processing.model.AbstractRetentionRuleModel;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.ClassRule;
import org.junit.Test;

@IntegrationTest
public class RetentionRulesIntegrationTest extends ServicelayerTest
{
	private static final Set<String> TYPES_TO_CLEANUP = Set.of("IntegrationApiMedia");
	private static final String RETENTION_RULE = "integrationApiMediaCleanupRule";
	private static final long ONE_WEEK_IN_SECONDS = 60 * 60 * 24 * 7;
	private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * ?";

	@ClassRule
	public static ModuleEssentialData essentialData = IntegrationServicesEssentialData.integrationServicesEssentialData();
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testMediaRetentionCleanupRuleHasNoFilterForRetentionCleanupRuleType()
	{
		final AbstractRetentionRuleModel cleanupRuleExample = new FlexibleSearchRetentionRuleModel();
		cleanupRuleExample.setCode(RETENTION_RULE);

		final AbstractRetentionRuleModel mediaCleanupRule = assertModelExists(cleanupRuleExample);

		assertThat(mediaCleanupRule.getItemtype()).isEqualTo("FlexibleSearchRetentionRule");
	}

	@Test
	public void testRetentionRulesCleanOnlyTypedDataOlderThanAWeek()
	{
		final AbstractRetentionRuleModel cleanupRuleExample = new AbstractRetentionRuleModel();
		cleanupRuleExample.setActionReference("basicRemoveCleanupAction");

		final List<AbstractRetentionRuleModel> cleanupRules = flexibleSearchService.getModelsByExample(cleanupRuleExample);

		assertThat(cleanupRules).hasSize(TYPES_TO_CLEANUP.size());
		cleanupRules.forEach(rule ->
			assertThat(rule).hasFieldOrPropertyWithValue("retentionTimeSeconds", ONE_WEEK_IN_SECONDS)
		);
	}

	@Test
	public void testRetentionCronJobsTriggersEveryDayAtMidnight()
	{
		final SearchResult<CronJobModel> cronJobResult = flexibleSearchService.search("SELECT {pk} FROM {CronJob}");
		final List<CronJobModel> cleanupCronJobs = cronJobResult.getResult().stream()
				.filter(j -> j.getCode().contains("Cleanup"))
				.collect(Collectors.toList());

		assertThat(cleanupCronJobs).hasSize(TYPES_TO_CLEANUP.size());
		cleanupCronJobs.forEach(cronJob -> assertThat(cronJob.getTriggers().get(0).getCronExpression()).isEqualTo(EVERY_DAY_AT_MIDNIGHT));
	}
}
