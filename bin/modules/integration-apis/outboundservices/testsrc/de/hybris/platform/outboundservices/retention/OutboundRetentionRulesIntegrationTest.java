/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.outboundservices.util.OutboundServicesEssentialData;
import de.hybris.platform.processing.model.AfterRetentionCleanupRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.ClassRule;
import org.junit.Test;

@IntegrationTest
public class OutboundRetentionRulesIntegrationTest extends ServicelayerTest
{
	private static final Set<String> TYPES_TO_CLEANUP = Set.of("OutboundRequest", "OutboundRequestMedia");
	private static final long ONE_WEEK_IN_SECONDS = 60 * 60 * 24 * 7;
	private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * ?";

	@ClassRule
	public static OutboundServicesEssentialData essentialData = OutboundServicesEssentialData.outboundServicesEssentialData();
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testRetentionRulesCleanOnlyTypedDataOlderThanAWeek()
	{
		final AfterRetentionCleanupRuleModel cleanupRuleExample = new AfterRetentionCleanupRuleModel();
		cleanupRuleExample.setActionReference("basicRemoveCleanupAction");

		final List<AfterRetentionCleanupRuleModel> cleanupRules = flexibleSearchService.getModelsByExample(cleanupRuleExample);

		assertThat(cleanupRules).hasSize(TYPES_TO_CLEANUP.size());
		cleanupRules.forEach(rule -> {
			assertThat(rule).hasFieldOrPropertyWithValue("retentionTimeSeconds", ONE_WEEK_IN_SECONDS);
			assertThat(TYPES_TO_CLEANUP).contains(rule.getRetirementItemType().getCode());
		});
	}

	@Test
	public void testMediaRetentionCleanupRuleHasNoFilterForRetentionCleanupRuleType()
	{
		final AfterRetentionCleanupRuleModel cleanupRuleExample = new AfterRetentionCleanupRuleModel();
		cleanupRuleExample.setRetirementItemType(mediaType());

		final AfterRetentionCleanupRuleModel mediaCleanupRule = assertModelExists(cleanupRuleExample);

		assertThat(mediaCleanupRule.getItemtype()).isEqualTo("AfterRetentionCleanupRule");
		assertThat(mediaCleanupRule.getItemFilterExpression()).isNullOrEmpty();
	}

	private ComposedTypeModel mediaType()
	{
		final ComposedTypeModel composedTypeModel = new ComposedTypeModel();
		composedTypeModel.setCode("OutboundRequestMedia");
		return assertModelExists(composedTypeModel);
	}

	@Test
	public void testRetentionCronJobsTriggersEveryDayAtMidnight()
	{
		final FlexibleSearchQuery cronJobsQuery = new FlexibleSearchQuery("SELECT {pk} FROM {CronJob} WHERE {code} IN (?job1, ?job2)");
		cronJobsQuery.addQueryParameter("job1", "outboundRequestCleanupCronJob");
		cronJobsQuery.addQueryParameter("job2", "outboundRequestMediaCleanupCronJob");
		final SearchResult<CronJobModel> cronJobResult = flexibleSearchService.search(cronJobsQuery);
		final List<CronJobModel> cronJobs = cronJobResult.getResult();

		assertThat(cronJobs).hasSize(TYPES_TO_CLEANUP.size());
		cronJobs.forEach(cronJob -> assertThat(cronJob.getTriggers().get(0).getCronExpression()).isEqualTo(EVERY_DAY_AT_MIDNIGHT));
	}
}
