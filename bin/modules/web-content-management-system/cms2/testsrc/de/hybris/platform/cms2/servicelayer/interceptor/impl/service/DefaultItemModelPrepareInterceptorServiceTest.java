/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl.service;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.interceptor.service.impl.DefaultItemModelPrepareInterceptorService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultItemModelPrepareInterceptorServiceTest
{
	@InjectMocks
	DefaultItemModelPrepareInterceptorService defaultItemModelPrepareInterceptorService;
	@Mock
	private Predicate<ItemModel> cmsItemTypePredicate;
	@Mock
	private Predicate<ItemModel> contentSlotForPageModelPredicate;

	@Mock
	private CatalogModel catalog;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionModel activeCatalogVersion;
	@Mock
	private CMSItemModel item;
	@Mock
	private ContentSlotForPageModel contentSlotForPage;
	@Mock
	private InterceptorContext interceptorContext;

	@Before
	public void setup()
	{
		when(item.getCatalogVersion()).thenReturn(activeCatalogVersion);
		when(contentSlotForPage.getCatalogVersion()).thenReturn(activeCatalogVersion);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(activeCatalogVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getActiveCatalogVersion()).thenReturn(activeCatalogVersion);
		ReflectionTestUtils.setField(defaultItemModelPrepareInterceptorService, "cmsItemTypePredicate", cmsItemTypePredicate);
		ReflectionTestUtils.setField(defaultItemModelPrepareInterceptorService, "contentSlotForPageModelPredicate", contentSlotForPageModelPredicate);

	}

	@Test
	public void ItemIsFromActiveCatalogVersion()
	{
		when(cmsItemTypePredicate.test(item)).thenReturn(Boolean.TRUE);

		boolean active = defaultItemModelPrepareInterceptorService.isFromActiveCatalogVersion(item);

		assertTrue(active);
	}

	@Test
	public void contentSlotForPageIsFromActiveCatalogVersion()
	{
		when(cmsItemTypePredicate.test(contentSlotForPage)).thenReturn(Boolean.FALSE);
		when(contentSlotForPageModelPredicate.test(contentSlotForPage)).thenReturn(Boolean.TRUE);
		when(catalog.getActiveCatalogVersion()).thenReturn(new CatalogVersionModel());

		boolean active = defaultItemModelPrepareInterceptorService.isFromActiveCatalogVersion(contentSlotForPage);

		assertFalse(active);
	}

	@Test
	public void contentSlotForPageIsNotFromActiveCatalogVersion()
	{
		when(cmsItemTypePredicate.test(contentSlotForPage)).thenReturn(Boolean.FALSE);
		when(contentSlotForPageModelPredicate.test(contentSlotForPage)).thenReturn(Boolean.TRUE);

		boolean active = defaultItemModelPrepareInterceptorService.isFromActiveCatalogVersion(contentSlotForPage);

		assertTrue(active);
	}

	@Test
	public void ItemIsNotFromActiveCatalogVersion()
	{
		when(cmsItemTypePredicate.test(item)).thenReturn(Boolean.FALSE);
		when(contentSlotForPageModelPredicate.test(item)).thenReturn(Boolean.FALSE);

		boolean active = defaultItemModelPrepareInterceptorService.isFromActiveCatalogVersion(item);

		assertFalse(active);
	}

	@Test
	public void isOnlyChangeSynchronizationBlocked()
	{
		final Map<String, Set<Locale>> dirtyAttributes = new HashMap<>();
		dirtyAttributes.put(CMSItemModel.SYNCHRONIZATIONBLOCKED, new HashSet<>());
		when(interceptorContext.getDirtyAttributes(item)).thenReturn(dirtyAttributes);

		assertTrue(defaultItemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(item, interceptorContext));
	}

	@Test
	public void isNotOnlyChangeSynchronizationBlockedOne()
	{
		final Map<String, Set<Locale>> dirtyAttributes = new HashMap<>();
		dirtyAttributes.put(CMSItemModel.SYNCHRONIZATIONBLOCKED, new HashSet<>());
		dirtyAttributes.put("itemattribute", new HashSet<>());
		when(interceptorContext.getDirtyAttributes(item)).thenReturn(dirtyAttributes);

		assertFalse(defaultItemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(item, interceptorContext));
	}

	@Test
	public void isNotOnlyChangeSynchronizationBlockedTwo()
	{
		final Map<String, Set<Locale>> dirtyAttributes = new HashMap<>();
		dirtyAttributes.put("itemattribute", new HashSet<>());
		when(interceptorContext.getDirtyAttributes(item)).thenReturn(dirtyAttributes);

		assertFalse(defaultItemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(item, interceptorContext));
	}
}
