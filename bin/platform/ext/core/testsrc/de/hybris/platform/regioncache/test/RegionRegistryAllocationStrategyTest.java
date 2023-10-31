/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.regioncache.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.RegionRegistryAllocationStrategy;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.regioncache.region.RegionType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;


/**
 * Test for RegionRegistryAllocationStrategy
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class RegionRegistryAllocationStrategyTest
{

	@Mock
	private CacheRegion region;

	private final RegionRegistryAllocationStrategy obj = new RegionRegistryAllocationStrategy();

	/**
	 * For region configuration with empty types RegionRegistryAllocationStrategy should "create" registry. Not to create
	 * registry RegionType.NON_REGISTRABLE (only) should be provided as a type.
	 */
	@Test
	public void shouldUseRegistryForManualRegion()
	{
		Mockito.when(region.getHandledTypes()).thenReturn(new String[]{});
		Assert.assertTrue(obj.isRegionRequiresRegistry(region));
	}

	@Test
	public void shouldNotUseRegistryForManualRegion()
	{
		Mockito.when(region.getHandledTypes()).thenReturn(new String[]
				{ RegionType.NON_REGISTRABLE.value() });

		Assert.assertFalse(obj.isRegionRequiresRegistry(region));
	}

	@Test
	public void testNullValue()
	{
		Assert.assertFalse(obj.isRegionRequiresRegistry(null));
	}

}
