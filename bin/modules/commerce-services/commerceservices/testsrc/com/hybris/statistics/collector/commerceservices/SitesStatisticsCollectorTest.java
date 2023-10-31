/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.statistics.collector.commerceservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


@IntegrationTest
public class SitesStatisticsCollectorTest extends ServicelayerTransactionalBaseTest
{
	private SitesStatisticsCollector collector;
	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		collector = new SitesStatisticsCollector();
		createTestSites();
	}

	private void createTestSites()
	{
		final BaseSiteModel baseSite = modelService.create(BaseSiteModel.class);
		baseSite.setUid("testSite1");
		baseSite.setDataIsolationEnabled(true);
		baseSite.setName("testSite1");
		modelService.save(baseSite);
	}

	@Test
	public void testCollectStatistics() throws Exception
	{
		// when
		final Map<String, Map<String, Object>> result = collector.collectStatistics();

		// then
		assertThat(result).isNotNull().isNotEmpty();
		assertThat(result.get("sites")).isNotNull().isNotEmpty();
		assertThat(result.get("sites")).containsEntry("numIsolatedSites", Integer.valueOf(1));
	}
}
