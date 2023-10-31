/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.inboundservices.util.InboundServicesEssentialData;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.processing.model.AfterRetentionCleanupRuleModel;
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
public class InboundRetentionRulesIntegrationTest extends ServicelayerTest
{
	private static final Set<String> TYPES_TO_CLEANUP = Set.of("InboundRequest", "InboundRequestMedia");
	private static final long ONE_WEEK_IN_SECONDS = 60 * 60 * 24 * 7;
	private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * ?";

	@ClassRule
	public static ModuleEssentialData essentialData = InboundServicesEssentialData.inboundServicesEssentialData();
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testRetentionRulesCleanOnlyTypedDataOlderThanAWeek()
	{
		final AfterRetentionCleanupRuleModel cleanupRuleExample = new AfterRetentionCleanupRuleModel();
		cleanupRuleExample.setActionReference("basicRemoveCleanupAction");

		final Set<AfterRetentionCleanupRuleModel> cleanupRules = flexibleSearchService.getModelsByExample(cleanupRuleExample).stream()
				.filter(r -> TYPES_TO_CLEANUP.contains(r.getRetirementItemType().getCode()))
				.collect(Collectors.toSet());

		assertThat(cleanupRules).hasSize(TYPES_TO_CLEANUP.size());
		cleanupRules.forEach(rule -> assertThat(rule).hasFieldOrPropertyWithValue("retentionTimeSeconds", ONE_WEEK_IN_SECONDS));
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
		composedTypeModel.setCode("InboundRequestMedia");
		return assertModelExists(composedTypeModel);
	}

	@Test
	public void testRetentionCronJobsTriggersEveryDayAtMidnight()
	{
		final SearchResult<CronJobModel> cronJobResult = flexibleSearchService.search("SELECT {pk} FROM {CronJob}");
		final List<CronJobModel> inboundModuleCronJobs = cronJobResult.getResult().stream()
				.filter(j -> j.getCode().startsWith("inbound"))
				.collect(Collectors.toList());

		assertThat(inboundModuleCronJobs).hasSize(TYPES_TO_CLEANUP.size());
		inboundModuleCronJobs.forEach(cronJob -> assertThat(cronJob.getTriggers().get(0).getCronExpression()).isEqualTo(EVERY_DAY_AT_MIDNIGHT));
	}
}
