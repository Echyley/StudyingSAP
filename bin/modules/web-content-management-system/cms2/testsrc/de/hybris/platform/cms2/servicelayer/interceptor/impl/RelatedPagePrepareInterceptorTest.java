/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.relatedpages.service.RelatedPageRejectionService;
import de.hybris.platform.cms2.servicelayer.interceptor.service.ItemModelPrepareInterceptorService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class RelatedPagePrepareInterceptorTest
{

	@InjectMocks
	private RelatedPagePrepareInterceptor interceptor;
	@Mock
	private RelatedPageRejectionService relatedPageRejectionService;
	@Mock
	private InterceptorContext interceptorContext;
	@Mock
	private ItemModel item;
	@Mock
	private ItemModelPrepareInterceptorService itemModelPrepareInterceptorService;

	@Before
	public void setUp()
	{
		when(itemModelPrepareInterceptorService.isEnabled()).thenReturn(Boolean.TRUE);
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(item)).thenReturn(false);
		when(itemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(item, interceptorContext)).thenReturn(false);
	}

	@Test
	public void testRejectRelatedPagesForItemModel() throws InterceptorException
	{
		interceptor.onPrepare(item, interceptorContext);

		verify(relatedPageRejectionService).rejectAllRelatedPages(item, interceptorContext);
	}

	@Test
	public void testSkipRejectRelatedPagesForDisabledInterceptor() throws InterceptorException
	{
		when(itemModelPrepareInterceptorService.isEnabled()).thenReturn(Boolean.FALSE);

		interceptor.onPrepare(item, interceptorContext);

		verifyZeroInteractions(relatedPageRejectionService);
	}

	@Test
	public void testSkipRejectRelatedPagesForOnlineCMSItem() throws InterceptorException
	{
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(item)).thenReturn(true);

		interceptor.onPrepare(item, interceptorContext);

		verifyZeroInteractions(relatedPageRejectionService);
	}

	@Test
	public void testSkipRejectRelatedPagesForOnlyChangeSynchronizationBlocked() throws InterceptorException
	{
		// GIVEN
		when(itemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(item, interceptorContext)).thenReturn(true);

		// WHEN
		interceptor.onPrepare(item, interceptorContext);

		// THEN
		verifyZeroInteractions(relatedPageRejectionService);
	}

}
