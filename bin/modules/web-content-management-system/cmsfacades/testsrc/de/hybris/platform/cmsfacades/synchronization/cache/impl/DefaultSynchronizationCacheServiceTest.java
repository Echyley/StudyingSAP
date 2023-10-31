/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.cache.impl;

import static de.hybris.platform.cmsfacades.synchronization.cache.impl.DefaultSynchronizationCacheService.SYNCHRONIZATION_ITEM_CACHE;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.service.SessionCachedContextProvider;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultSynchronizationCacheServiceTest
{
	@InjectMocks
	private DefaultSynchronizationCacheService synchronizationCacheService;

	@Mock
	private SessionCachedContextProvider sessionCachedContextProvider;

	@Mock
	private ItemModel itemModel1;
	private final PK pk1 = PK.fromLong(1234);
	@Mock
	private ItemModel itemModel2;
	private final PK pk2 = PK.fromLong(1235);

	private final String CACHE_PREFIX = "CACHE_PREFIX";

	@Mock
	private ItemModel value1;
	@Mock
	private ItemModel value2;

	private List<ItemModel> values;

	@Before
	public void setUp()
	{
		when(itemModel1.getPk()).thenReturn(pk1);
		when(itemModel2.getPk()).thenReturn(pk2);
		values = Arrays.asList(value1, value2);
	}

	@Test
	public void shouldAddNewValuesToCache()
	{
		// WHEN
		synchronizationCacheService.getOrSetItemListCache(() -> values, CACHE_PREFIX, itemModel1, itemModel2);

		// THEN
		verify(sessionCachedContextProvider).addItemToMapCache(SYNCHRONIZATION_ITEM_CACHE, CACHE_PREFIX + "_" + itemModel1.getPk().getLongValueAsString() + itemModel2.getPk().getLongValueAsString(), values);
	}

	@Test
	public void shouldRetrieveValuesFromCache()
	{
		// GIVEN
		final Map<Object, Object> cacheValue = new HashMap<>();
		cacheValue.put(CACHE_PREFIX + "_" + itemModel1.getPk().getLongValueAsString() + itemModel2.getPk().getLongValueAsString(), values);
		when(sessionCachedContextProvider.getAllItemsFromMapCache(SYNCHRONIZATION_ITEM_CACHE)).thenReturn(cacheValue);

		// WHEN
		final List<ItemModel> fromCache = synchronizationCacheService.getOrSetItemListCache(() -> values, CACHE_PREFIX, itemModel1, itemModel2);

		// THEN
		verify(sessionCachedContextProvider, never()).addItemToMapCache(any(), any(), any());
		assertThat(fromCache, Matchers.equalTo(values));
	}
}
