/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.custdev.projects.fbs.slc.hybris.util.cache.DefaultRegionConfigurationInfo;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.RegionConfigurationInfo;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SSCCacheInvalidationListenerTest
{
	private static final String REGION_NAME = "regionName";
	private static final String EXISTING_KEY = "aKey";

	@InjectMocks
	private SSCCacheInvalidationListener classUnderTest;

	@Mock
	private CacheRegion region;
	@Mock
	private CacheFacade cacheFacade;

	private SSCCacheInvalidationEvent event;
	private final List<String> invalidKeys = new ArrayList();
	private final RegionConfigurationInfo cacheRegionInfo = new DefaultRegionConfigurationInfo(REGION_NAME, 0);
	private final Object cacheEntry = "cacheEntryAsString";

	@Before
	public void setUp()
	{
		given(region.getCacheFacade()).willReturn(cacheFacade);
		given(region.getRegionConfigurationInfo()).willReturn(cacheRegionInfo);
		given(cacheFacade.get(EXISTING_KEY)).willReturn(cacheEntry);

	}

	@Test
	public void testInvalidateExistingKey()
	{
		invalidKeys.add(EXISTING_KEY);
		event = new SSCCacheInvalidationEvent(invalidKeys, REGION_NAME);
		classUnderTest.onApplicationEvent(event);

		verify(cacheFacade).remove(EXISTING_KEY);
		verify(cacheFacade).remove(cacheEntry.toString());
		verify(cacheFacade, atLeastOnce()).get(any());
		verifyNoMoreInteractions(cacheFacade);
	}

	@Test
	public void testInvalidateNonExistingKey()
	{
		invalidKeys.add("anotherKey");
		event = new SSCCacheInvalidationEvent(invalidKeys, REGION_NAME);
		classUnderTest.onApplicationEvent(event);

		verify(cacheFacade).remove("anotherKey");
		verify(cacheFacade, atLeastOnce()).get(any());
		verifyNoMoreInteractions(cacheFacade);
	}
}
