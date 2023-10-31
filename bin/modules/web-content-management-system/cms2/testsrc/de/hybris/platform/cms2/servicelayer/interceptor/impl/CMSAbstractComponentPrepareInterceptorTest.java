/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.interceptor.service.ItemModelPrepareInterceptorService;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.cms2.relateditems.RelatedItemsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSAbstractComponentPrepareInterceptorTest
{
	@InjectMocks
	private CMSAbstractComponentPrepareInterceptor interceptor;
	@Mock
	private ModelService modelService;
	@Mock
	private ItemModelPrepareInterceptorService itemModelPrepareInterceptorService;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private AbstractCMSComponentModel componentModel;
	@Mock
	private InterceptorContext interceptorContext;
	@Mock
	private ContentSlotModel slotModel;
	@Mock
	private RelatedItemsService relatedItemsService;
	@Mock
	private AbstractPageModel page;
	@Mock
	private Predicate<ItemModel> pageTypePredicate;

	@Before
	public void setUp()
	{
		when(itemModelPrepareInterceptorService.isEnabled()).thenReturn(Boolean.TRUE);
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(componentModel)).thenReturn(false);
		when(itemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(componentModel, interceptorContext)).thenReturn(false);
		final List<ContentSlotModel> slots = new ArrayList<>();
		slots.add(slotModel);
		when(modelService.isNew(componentModel)).thenReturn(true);
		when(componentModel.getSlots()).thenReturn(slots);
		when(modelService.isNew(slotModel)).thenReturn(true);
		interceptor.setRelatedItemsService(relatedItemsService);
		interceptor.setPageTypePredicate(pageTypePredicate);
	}

	@Test
	public void NewComponentInNewSlot() throws InterceptorException
	{
		interceptor.onPrepare(componentModel, interceptorContext);
		verify(componentModel, never()).setSynchronizationBlocked(false);
		verify(componentModel, never()).setSynchronizationBlocked(true);
	}

	@Test
	public void NewComponentInSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(slotModel)).thenReturn(false);
		when(cmsComponentService.inSharedSlots(componentModel)).thenReturn(true);

		interceptor.onPrepare(componentModel, interceptorContext);

		verify(componentModel, times(1)).setSynchronizationBlocked(false);

	}

	@Test
	public void NewComponentInNonSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(slotModel)).thenReturn(false);
		when(cmsComponentService.inSharedSlots(componentModel)).thenReturn(false);
		when(relatedItemsService.getRelatedItems((componentModel))).thenReturn(Arrays.asList(page));
		when(page.getPk()).thenReturn(PK.createCounterPK(123));
		when(pageTypePredicate.test(page)).thenReturn(true);

		interceptor.onPrepare(componentModel, interceptorContext);

		verify(componentModel, times(1)).setSynchronizationBlocked(true);

	}

	@Test
	public void OldComponentInSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(componentModel)).thenReturn(false);
		when(modelService.isNew(slotModel)).thenReturn(false);
		when(cmsComponentService.inSharedSlots(componentModel)).thenReturn(true);

		interceptor.onPrepare(componentModel, interceptorContext);

		verify(componentModel, times(1)).setSynchronizationBlocked(false);
	}

	@Test
	public void OldComponentInNonSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(componentModel)).thenReturn(false);
		when(modelService.isNew(slotModel)).thenReturn(false);
		when(cmsComponentService.inSharedSlots(componentModel)).thenReturn(false);
		when(relatedItemsService.getRelatedItems((componentModel))).thenReturn(Arrays.asList(page));
		when(page.getPk()).thenReturn(PK.createCounterPK(123));
		when(pageTypePredicate.test(page)).thenReturn(true);

		interceptor.onPrepare(componentModel, interceptorContext);

		verify(componentModel, times(1)).setSynchronizationBlocked(true);
	}

	@Test
	public void OldComponentHasNoRelatedPage() throws InterceptorException
	{
		when(modelService.isNew(componentModel)).thenReturn(false);
		when(cmsComponentService.inSharedSlots(componentModel)).thenReturn(false);

		when(relatedItemsService.getRelatedItems((componentModel))).thenReturn(Arrays.asList());

		interceptor.onPrepare(componentModel, interceptorContext);

		verify(componentModel, times(1)).setSynchronizationBlocked(false);
	}
}
