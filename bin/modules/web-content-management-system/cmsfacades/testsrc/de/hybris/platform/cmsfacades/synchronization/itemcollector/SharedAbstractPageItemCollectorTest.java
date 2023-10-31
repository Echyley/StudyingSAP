/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemcollector;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class SharedAbstractPageItemCollectorTest
{

	private static final String CONTENT_SLOT = "CONTENT_SLOT";
	private static final String SHARED_CONTENT_SLOT = "SHARED_CONTENT_SLOT";
	@Mock
	private CMSAdminContentSlotService contentSlotService;
	@InjectMocks
	private SharedAbstractPageItemCollector itemCollector;
	
	// attributes of this test
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private ContentSlotData contentSlotData;
	@Mock
	private ContentSlotData sharedContentSlotData;
	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private ContentSlotModel sharedContentSlot;
	@Mock
	private SynchronizationCacheService synchronizationCacheService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionModel parentCatalogVersion;

	@Before
	public void setup()
	{
		// sharedContentSlotData mocks
		when(sharedContentSlotData.getUid()).thenReturn(SHARED_CONTENT_SLOT);
		// contentSlotData mocks
		when(contentSlotData.getUid()).thenReturn(CONTENT_SLOT);
		// contentSlotService mocks
		when(contentSlotService.getContentSlotsForPage(pageModel)).thenReturn(Arrays.asList(contentSlotData, sharedContentSlotData));

		when(contentSlot.getCatalogVersion()).thenReturn(catalogVersion);
		when(sharedContentSlot.getCatalogVersion()).thenReturn(catalogVersion);

		when(contentSlotData.isFromMaster()).thenReturn(false);
		when(contentSlotData.getContentSlot()).thenReturn(contentSlot);
		when(sharedContentSlotData.isFromMaster()).thenReturn(true);
		when(sharedContentSlotData.getContentSlot()).thenReturn(sharedContentSlot);

		doAnswer((Answer<Object>) invocation -> {
			final Supplier<List<Object>> supplier = (Supplier) invocation.getArguments()[0];
			return supplier.get();
		}).when(synchronizationCacheService).getOrSetItemListCache(any(), anyString(), any());

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
	}
	
	@Test
	public void testWhenPageHasManyContentSlots_shouldReturnOnlyOneContentSlot()
	{
		final List<ItemModel> collected = itemCollector.collect(pageModel);
		assertThat(collected, Matchers.notNullValue());
		assertThat(collected, Matchers.contains(sharedContentSlot));
	}

	@Test
	public void shouldNotReturnContentSlotFromParentCatalogVersion()
	{
		// GIVEN
		when(sharedContentSlot.getCatalogVersion()).thenReturn(parentCatalogVersion);

		// WHEN
		final List<ItemModel> collected = itemCollector.collect(pageModel);

		// THEN
		assertThat(collected, Matchers.emptyIterable());
	}
}
