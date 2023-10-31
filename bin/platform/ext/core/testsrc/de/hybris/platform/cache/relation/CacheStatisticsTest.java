/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheStatistics;

import org.junit.Test;

@UnitTest
public class CacheStatisticsTest
{
	@Test
	public void testShouldStatisticsBeEmpty()
	{
		final CacheStatistics stats = new CacheStatistics();
		assertThat(stats.getHitCount()).isZero();
		assertThat(stats.getFetches()).isZero();
		assertThat(stats.getMissCount()).isZero();
		assertThat(stats.getEvictions()).isZero();
		assertThat(stats.getInvalidations()).isZero();
	}
}
