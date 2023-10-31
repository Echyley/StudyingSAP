/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.regioncache.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.cache.impl.RegionCacheAdapter;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloTypeCacheUnit;
import de.hybris.platform.regioncache.DefaultCacheController;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.config.ConfigIntf;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.ehcache.CacheManager;


@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
		{ "/test/OneRegionTestEHCacheRegion-context.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RegionCacheAdapterTest
{
	@Resource(name = "CacheController")
	private DefaultCacheController controller;

	@Resource
	private CacheRegion region;

	MockitoSession mockito;

	@Mock
	private Tenant tenant;

	@Mock
	private Tenant tenant2;

	@Mock
	private ConfigIntf config;

	@After
	public void clean()
	{
		mockito.finishMocking();
		// This clears any potentially assigned non-tx global cache adapter which would
		// be pointing to the 'real' cache and not the one in scope here !!!
		Transaction.current().enableTxCache(false);
		Transaction.current().enableTxCache(true);
		Transaction.current().enableTxCache(false);

		final Collection<CacheRegion> regions = controller.getRegions();
		for (final CacheRegion region : regions)
		{
			controller.clearCache(region);
		}
		System.gc();
	}

	@AfterClass
	public static void cleanEHCache()
	{
		CacheManager.getInstance().clearAll();
	}

	@Before
	public void resetLocalTxCacheAdapter()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
		// This clears any potentially assigned non-tx global cache adapter which would
		// be pointing to the 'real' cache and not the one in scope here !!!
		Transaction.current().enableTxCache(false);
		Transaction.current().enableTxCache(true);
		Transaction.current().enableTxCache(false);
	}

	/**
	 * No matter how many RegionCacheAdapter-s created only one registers callback
	 */
	@Test
	public void controllerCallbackCntTest()
	{
		assertNotNull(config);

		when(tenant.getTenantID()).thenReturn("test");
		when(tenant.getConfig()).thenReturn(config);
		when(tenant2.getConfig()).thenReturn(config);
		when(config.getBoolean(Cache.CONFIG_FORCE_EXCLUSIVE_CALCULATION, false))
				.thenReturn(Boolean.FALSE);

		RegionCacheAdapter adapter = new RegionCacheAdapter(tenant, controller);

		adapter.clear();

		assertEquals(1, controller.getLifecycleCallbackCnt());

		when(tenant2.getTenantID()).thenReturn("test2");
		adapter = new RegionCacheAdapter(tenant2, controller);
		adapter.clear();

		assertEquals(1, controller.getLifecycleCallbackCnt());

		adapter = new RegionCacheAdapter(tenant, controller);
		assertEquals(1, controller.getLifecycleCallbackCnt());
		adapter.clear();
		assertEquals(1, controller.getLifecycleCallbackCnt());
	}


	@Test
	public void jaloTypeCacheKeyTest()
	{
		when(tenant.getTenantID()).thenReturn("test");
		when(tenant.getConfig()).thenReturn(config);
		when(config.getBoolean(Cache.CONFIG_FORCE_EXCLUSIVE_CALCULATION, false))
				.thenReturn(Boolean.FALSE);

		final RegionCacheAdapter adapter = new RegionCacheAdapter(tenant, controller);
		adapter.clear();

		assertEquals(0, region.getCacheRegionStatistics().getMissCount());
		assertEquals(0, region.getCacheRegionStatistics().getHitCount());
		assertEquals(0, region.getCacheRegionStatistics().getInvalidations());

		JaloTypeCacheUnit cunit = new JaloTypeCacheUnit(adapter, 1, "key1", 1)
		{
			@Override
			public Object compute()
			{
				return "key1_value";
			}


		};
		cunit.getCached();

		assertEquals(0, region.getCacheRegionStatistics().getHitCount());
		assertNotNull(region.get(cunit.getKey()));

		assertEquals(1, region.getCacheRegionStatistics().getHitCount());
		assertEquals(1, region.getCacheRegionStatistics().getMissCount());
		assertEquals(0, region.getCacheRegionStatistics().getInvalidations());


		cunit = new JaloTypeCacheUnit(adapter, 1, "key2", 1)
		{
			@Override
			public Object compute()
			{
				return "key2_value";
			}

		};
		cunit.getCached();

		assertEquals(1, region.getCacheRegionStatistics().getHitCount());
		assertNotNull(region.get(cunit.getKey()));
		assertEquals(2, region.getCacheRegionStatistics().getMissCount());
		assertEquals(2, region.getCacheRegionStatistics().getHitCount());
		assertEquals(0, region.getCacheRegionStatistics().getInvalidations());

		adapter.invalidate(new Object[]
				{ Cache.CACHEKEY_JALOTYPE, "1", "1" }, 1);

		assertEquals(2, region.getCacheRegionStatistics().getMissCount());
		assertEquals(2, region.getCacheRegionStatistics().getHitCount());
		assertEquals(0, region.getCacheRegionStatistics().getInvalidations());
	}
}
