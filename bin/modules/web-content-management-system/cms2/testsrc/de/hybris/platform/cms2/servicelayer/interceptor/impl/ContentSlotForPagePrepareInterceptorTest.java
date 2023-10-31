/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.interceptor.service.ItemModelPrepareInterceptorService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ContentSlotForPagePrepareInterceptorTest
{
	@InjectMocks
	private ContentSlotForPagePrepareInterceptor interceptor;

	@Mock
	private ModelService modelService;
	@Mock
	private ItemModelPrepareInterceptorService itemModelPrepareInterceptorService;
	@Mock
	private ContentSlotForPageModel slotForPage;
	@Mock
	private ContentSlotModel slot;
	@Mock
	private AbstractCMSComponentModel componentModel;
	@Mock
	private InterceptorContext interceptorContext;

	@Before
	public void setUp()
	{
		when(itemModelPrepareInterceptorService.isEnabled()).thenReturn(Boolean.TRUE);
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(slotForPage)).thenReturn(false);
		when(slotForPage.getContentSlot()).thenReturn(slot);
		final List<AbstractCMSComponentModel> componentModels = new ArrayList<>();
		componentModels.add(componentModel);
		when(slot.getCmsComponents()).thenReturn(componentModels);
	}

	@Test
	public void newSlotHasNoComponent() throws InterceptorException
	{
		when(modelService.isNew(slot)).thenReturn(true);
		when(slot.getCmsComponents()).thenReturn(new ArrayList<>());

		interceptor.onPrepare(slotForPage, interceptorContext);

		verify(slot, times(1)).setSynchronizationBlocked(true);
		verify(componentModel, never()).setSynchronizationBlocked(true);
		verify(componentModel, never()).setSynchronizationBlocked(false);
	}

	@Test
	public void newSlotHasNewComponent() throws InterceptorException
	{
		when(modelService.isNew(slot)).thenReturn(true);
		when(modelService.isNew(componentModel)).thenReturn(true);

		interceptor.onPrepare(slotForPage, interceptorContext);

		verify(slot, times(1)).setSynchronizationBlocked(true);
		verify(componentModel, times(1)).setSynchronizationBlocked(true);
	}

	@Test
	public void newSlotHasOldComponent() throws InterceptorException
	{
		when(modelService.isNew(slot)).thenReturn(true);
		when(modelService.isNew(componentModel)).thenReturn(false);

		interceptor.onPrepare(slotForPage, interceptorContext);

		verify(slot, times(1)).setSynchronizationBlocked(true);
		verify(componentModel, never()).setSynchronizationBlocked(false);
		verify(componentModel, never()).setSynchronizationBlocked(true);
	}

	@Test
	public void oldSlot() throws InterceptorException
	{
		when(modelService.isNew(slot)).thenReturn(false);

		interceptor.onPrepare(slotForPage, interceptorContext);

		verify(slot, never()).setSynchronizationBlocked(false);
		verify(slot, never()).setSynchronizationBlocked(true);
	}
}
