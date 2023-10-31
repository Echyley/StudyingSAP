/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.cache.impl;

import de.hybris.platform.cache.Cache;
import de.hybris.platform.cache.RemoteInvalidationSource;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBackofficeFacetSearchConfigCacheTest
{
	final static String typeCode = "123";
	final static PK facetSearchConfigPk = PK.createFixedCounterPK(123, 112233L);
	final Object facetSearchConfig = new Object();

	static class AbstractBackofficeFacetSearchConfigCacheImpl extends AbstractBackofficeFacetSearchConfigCache
	{
		@Override
		public void putSearchConfigForTypeCode(final String typeCode, final Object facetSearchConfig) {
			final Lock writeLock = this.cacheLock.writeLock();
			writeLock.lock();
			try
			{
				cache.put(typeCode, facetSearchConfigPk);
			}
			finally
			{
				writeLock.unlock();
			}
		}
	}

	@Mock
	private ModelService modelService;

	@InjectMocks
	private AbstractBackofficeFacetSearchConfigCacheImpl abstractBackofficeFacetSearchConfigCacheImpl;

	@Before
	public void setUp() throws Exception
	{
		abstractBackofficeFacetSearchConfigCacheImpl.putSearchConfigForTypeCode(typeCode, facetSearchConfig);
	}

	@Test
	public void shouldReturnTrueWhenCacheContainsKey()
	{
		assertTrue(abstractBackofficeFacetSearchConfigCacheImpl.containsSearchConfigForTypeCode(typeCode));
	}

	@Test
	public void shouldReturnSearchConfigForTypeCodeWhenPKExists()
	{
		doReturn(facetSearchConfig).when(modelService).get(facetSearchConfigPk);
		assertEquals(facetSearchConfig, abstractBackofficeFacetSearchConfigCacheImpl.getSearchConfigForTypeCode(typeCode));
	}

	@Test
	public void shouldReturnNullConfigForTypeCodeWhenPKIsNull()
	{
		assertNull(abstractBackofficeFacetSearchConfigCacheImpl.getSearchConfigForTypeCode(typeCode));
	}

	@Test
	public void ShouldClearCacheWhenHasInvalidateCache()
	{
		abstractBackofficeFacetSearchConfigCacheImpl.invalidateCache();
		assertEquals(new HashMap<>(), abstractBackofficeFacetSearchConfigCacheImpl.cache);
	}

	@Test
	public void ShouldInvokeInvalidateCacheWhenIsOperationInvalidating()
	{
		final Object[] key = new Object[3];
		key[2] = "123";
		final Set<String> invalidatingTypeCodes = new HashSet<>(Arrays.asList("", "123"));
		abstractBackofficeFacetSearchConfigCacheImpl.setInvalidatingTypecodes(invalidatingTypeCodes);
		abstractBackofficeFacetSearchConfigCacheImpl.keyInvalidated(key, 4, mock(Cache.class),
				mock(RemoteInvalidationSource.class));
		assertEquals(new HashMap<>(), abstractBackofficeFacetSearchConfigCacheImpl.cache);
	}
}
