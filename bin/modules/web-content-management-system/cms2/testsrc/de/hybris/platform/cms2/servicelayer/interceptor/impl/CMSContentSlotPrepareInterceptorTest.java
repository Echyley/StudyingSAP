/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.interceptor.service.ItemModelPrepareInterceptorService;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSContentSlotPrepareInterceptorTest
{
	@InjectMocks
	private CMSContentSlotPrepareInterceptor interceptor;
	@Mock
	private CMSContentSlotService cmsContentSlotService;
	@Mock
	private ModelService modelService;
	@Mock
	private ItemModelPrepareInterceptorService itemModelPrepareInterceptorService;
	@Mock
	private ContentSlotModel contentSlotModel;
	@Mock
	private InterceptorContext interceptorContext;

	@Before
	public void setUp()
	{
		when(itemModelPrepareInterceptorService.isEnabled()).thenReturn(Boolean.TRUE);
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(contentSlotModel)).thenReturn(false);
		when(itemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(contentSlotModel, interceptorContext)).thenReturn(false);
	}

	@Test
	public void newSlot() throws InterceptorException
	{
		when(modelService.isNew(contentSlotModel)).thenReturn(true);

		interceptor.onPrepare(contentSlotModel, interceptorContext);

		verify(contentSlotModel, never()).setSynchronizationBlocked(false);
		verify(contentSlotModel, never()).setSynchronizationBlocked(true);
	}

	@Test
	public void oldSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(contentSlotModel)).thenReturn(false);
		when(cmsContentSlotService.isSharedSlot(contentSlotModel)).thenReturn(true);

		interceptor.onPrepare(contentSlotModel, interceptorContext);

		verify(contentSlotModel, times(1)).setSynchronizationBlocked(false);
	}

	@Test
	public void oldNonSharedSlot() throws InterceptorException
	{
		when(modelService.isNew(contentSlotModel)).thenReturn(false);
		when(cmsContentSlotService.isSharedSlot(contentSlotModel)).thenReturn(false);

		interceptor.onPrepare(contentSlotModel, interceptorContext);

		verify(contentSlotModel, times(1)).setSynchronizationBlocked(true);
	}
}
