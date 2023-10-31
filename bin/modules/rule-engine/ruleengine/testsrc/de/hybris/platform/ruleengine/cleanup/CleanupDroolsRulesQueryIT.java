/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengine.cleanup;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.cronjob.CleanupDroolsRulesStrategy;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.internal.model.MaintenanceCleanupJobModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;


import static org.junit.Assert.assertEquals;


/**
 * Simple sanity test that the clean up drools rule query search works
 * (for database compatibility tests to ensure the flexible search queries work properly)
 */
@IntegrationTest
public class CleanupDroolsRulesQueryIT extends ServicelayerTest
{


	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CleanupDroolsRulesStrategy cleanupDroolsRulesStrategy;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/ruleengine/test/cronjob/cleanUpdroolsrule.impex", "utf-8");

	}


	@Test
	public void testQueryOfSelectDroolsRulesTobeRemoved()
	{

		final CronJobModel cjm = new CronJobModel();
		final MaintenanceCleanupJobModel cleanupJob = new MaintenanceCleanupJobModel();
		cjm.setJob(cleanupJob);
		final SearchResult<DroolsRuleModel> search = flexibleSearchService.search(cleanupDroolsRulesStrategy
				.createFetchQuery(cjm));
		final List<DroolsRuleModel> list = search.getResult();
		assertEquals(3,list.size());


	}

}
