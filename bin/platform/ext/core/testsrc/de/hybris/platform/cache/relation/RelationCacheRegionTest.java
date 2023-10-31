/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.regioncache.CacheStatistics;
import de.hybris.platform.regioncache.key.legacy.LegacyCacheKey;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class RelationCacheRegionTest
{
	public static final LegacyCacheKey LEGACY_KEY = new LegacyCacheKey(new Object[]{ "aaa", "bbb" }, Registry.getCurrentTenant().getTenantID());
	private final RelationCacheRegion region = new RelationCacheRegion(true);
	private final RelationCacheUnit unit = RelationCacheUnit.createRelationCacheUnit(
			PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22324));

	private final RelationCacheUnit unit2 = RelationCacheUnit.createRelationCacheUnit(
			PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22325));

	private final RelationCacheUnit unit3 = RelationCacheUnit.createRelationCacheUnit(
			PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22326));

	private final RelationCacheKey key = new RelationCacheKey(Registry.getCurrentTenant().getTenantID(),PrincipalGroupModel._PRINCIPALGROUPRELATION, null, null);
	private final RelationCacheUnit unit4 = RelationCacheUnit.createRelationCacheUnit(key);

	private final PropertyConfigSwitcher pgrelCapacityProperty = new PropertyConfigSwitcher(String.format("relation.cache.%s.capacity", PrincipalGroupModel._PRINCIPALGROUPRELATION));
	private final PropertyConfigSwitcher defaultCapacityProperty = new PropertyConfigSwitcher("relation.cache.default.capacity");

	@Before
	public void before()
	{
		region.setStatsEnabled(true);
	}

	@After
	public void after()
	{
		pgrelCapacityProperty.switchBackToDefault();
		defaultCapacityProperty.switchBackToDefault();
		region.clearCache();
	}

	@Test
	public void testStatsEnabled()
	{
		assertThat(region.isStatsEnabled()).isTrue();
	}

	@Test
	public void testStatsDisables()
	{
		region.setStatsEnabled(false);
		assertThat(region.isStatsEnabled()).isFalse();
		assertThat(new RelationCacheRegion(false).isStatsEnabled()).isFalse();
	}

	@Test
	public void testShouldCountFetches()
	{
		region.getWithLoader(unit.getKey(), unit);
		assertThat(region.getCacheRegionStatistics().getFetches()).isEqualTo(1);
	}

	@Test
	public void testShouldCountMiss()
	{
		region.get(unit.getKey());
		assertThat(region.getCacheRegionStatistics().getMissCount()).isEqualTo(1);
		region.getWithLoader(unit.getKey(), unit);
		assertThat(region.getCacheRegionStatistics().getMissCount()).isEqualTo(2);
	}

	@Test
	public void testShouldCountHit()
	{
		region.getWithLoader(unit.getKey(), unit);
		assertThat(region.getCacheRegionStatistics().getHits()).isZero();
		region.get(unit.getKey());
		assertThat(region.getCacheRegionStatistics().getHits()).isEqualTo(1);
	}

	@Test
	public void testShouldCountInvalidation()
	{
		region.getWithLoader(unit.getKey(), unit);
		region.remove(unit.getKey(), false);
		assertThat(region.getCacheRegionStatistics().getInvalidations()).isEqualTo(1);
	}

	@Test
	public void testShouldClearStatistics()
	{
		// given
		final CacheStatistics stats = region.getCacheRegionStatistics();
		final Object typeCode = 1;
		stats.hit(typeCode);
		stats.fetched(typeCode);
		stats.missed(typeCode);
		stats.evicted(typeCode);
		stats.invalidated(typeCode);
		assertThatAllRelationCacheStatisticsEqualTo(1);

		// when
		region.clearCache();

		// then
		assertThatAllRelationCacheStatisticsEqualTo(0);
	}

	private void assertThatAllRelationCacheStatisticsEqualTo(final int value)
	{
		assertThat(region.getCacheRegionStatistics().getHitCount()).isEqualTo(value);
		assertThat(region.getCacheRegionStatistics().getFetches()).isEqualTo(value);
		assertThat(region.getCacheRegionStatistics().getMissCount()).isEqualTo(value);
		assertThat(region.getCacheRegionStatistics().getEvictions()).isEqualTo(value);
		assertThat(region.getCacheRegionStatistics().getInvalidations()).isEqualTo(value);
	}

	@Test
	public void testShouldGet()
	{
		assertThat(region.get(unit.getKey())).isNull();
	}

	@Test
	public void testShouldGetWithNullKey()
	{
		assertThat(region.get(null)).isNull();
	}

	@Test
	public void testShouldGetWithLegacyKey()
	{
		assertThat(region.get(new LegacyCacheKey(new Object[]{"aaa","bbb"}, "junit"))).isNull();
	}

	@Test
	public void testShouldGetWithLoader()
	{
		assertThat(region.getWithLoader(null, null)).isNull();
	}


	@Test
	public void testShouldGetWithLoaderWithLegacyKey()
	{
		assertThat(region.getWithLoader(LEGACY_KEY, null)).isNull();
	}

	@Test
	public void testShouldGetWithLoaderWithRelationCacheKey()
	{
		assertThat(region.getWithLoader(unit.getKey(), unit)).isEqualTo(unit);
	}

	@Test
	public void testRemoveFromEmptyCache()
	{
		assertThat(region.remove(unit.getKey(),false)).isNull();
	}

	@Test
	public void testRemoveWithLegacyKey()
	{
		assertThat(region.remove(LEGACY_KEY, false)).isNull();
	}

	@Test
	public void testRemove()
	{
		region.getWithLoader(unit.getKey(), unit);
		assertThat(region.remove(unit.getKey(),false)).isEqualTo(unit);
		assertThat(region.get(unit.getKey())).isNull();
	}

	@Test
	public void testRemovingKeyForRelationRemovesEverythingFromThatRelation(){
		region.getWithLoader(unit.getKey(), unit);
		region.getWithLoader(unit2.getKey(), unit2);
		region.getWithLoader(unit3.getKey(), unit3);

		region.remove(unit4.getKey(), false);

		assertThat(region.getAllKeys()).isEmpty();
	}

	@Test
	public void testGetAllKeys()
	{
		region.getWithLoader(unit.getKey(), unit);
		region.getWithLoader(unit2.getKey(), unit2);
		region.getWithLoader(unit3.getKey(), unit3);
		assertThat(region.getAllKeys()).containsExactlyInAnyOrder(unit.getKey(), unit2.getKey(), unit3.getKey());
	}

	@Test
	public void testContainsKey()
	{
		region.getWithLoader(unit.getKey(), unit);
		assertThat(region.containsKey(unit.getKey())).isTrue();
	}

	@Test
	public void testDoesNotContainKey()
	{
		assertThat(region.containsKey(unit.getKey())).isFalse();
	}

	@Test
	public void testGetStatistics()
	{
		assertThat(region.getCacheRegionStatistics()).isNotNull();
	}

	@Test
	public void testGetSize()
	{
		region.getWithLoader(unit.getKey(), unit);
		region.getWithLoader(unit2.getKey(), unit2);
		region.getWithLoader(unit3.getKey(), unit3);
		assertThat(region.getMaxReachedSize()).isEqualTo(3);
	}

	@Test
	public void testGetSizeWhenRegionIsEmpty()
	{
		assertThat(region.getMaxReachedSize()).isZero();
	}

	@Test
	public void testGetName()
	{
		assertThat(region.getName()).isEqualTo("relationCacheRegion");
	}

	@Test
	public void testGetHandledTypes()
	{
		final String rel = RelationCacheKey.RELATION;
		region.setHandledTypes(new String[] { rel });
		assertThat(region.getHandledTypes()).hasSize(1).contains(rel);
	}

	@Test
	public void testGetCacheMaxEntries()
	{
		pgrelCapacityProperty.switchToValue("500");
		defaultCapacityProperty.switchToValue("100");
		final RelationCacheRegion region = new RelationCacheRegion(true);

		assertThat(region.getCacheMaxEntries()).isZero();

		region.getWithLoader(new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(567311L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(500);

		region.getWithLoader(new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.MEMBERS, PK.fromLong(567312L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(1000);

		region.getWithLoader(new RelationCacheKey("junit", CronJobModel._JOBCRONJOBRELATION, CronJobModel.JOB, PK.fromLong(567313L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(1100);

		region.getWithLoader(new RelationCacheKey("junit", CronJobModel._JOBCRONJOBRELATION, JobModel.CRONJOBS, PK.fromLong(567314L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(1200);

		region.getWithLoader(new RelationCacheKey("junit", CronJobModel._JOBCRONJOBRELATION, JobModel.CRONJOBS, PK.fromLong(567315L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(1200); // unchanged because map for JobCronJobRelation.cronJobs was already created

		region.getWithLoader(new RelationCacheKey("junit-2", CronJobModel._JOBCRONJOBRELATION, JobModel.CRONJOBS, PK.fromLong(567316L)), (k) -> 1);
		assertThat(region.getCacheMaxEntries()).isEqualTo(1300);

		region.clearCache();
		assertThat(region.getCacheMaxEntries()).isZero();
	}
}
