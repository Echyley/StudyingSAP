/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.relateditems.RelatedItemsService;
import de.hybris.platform.cms2.servicelayer.interceptor.service.ItemModelPrepareInterceptorService;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSAbstractRestrictionPrepareInterceptorTest
{
	@InjectMocks
	private CMSAbstractRestrictionPrepareInterceptor interceptor;

	@Mock
	private ItemModelPrepareInterceptorService itemModelPrepareInterceptorService;
	@Mock
	private CMSRestrictionService cmsRestrictionService;
	@Mock
	private AbstractRestrictionModel restrictionModel;
	@Mock
	private InterceptorContext interceptorContext;
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
		when(itemModelPrepareInterceptorService.isFromActiveCatalogVersion(restrictionModel)).thenReturn(false);
		when(itemModelPrepareInterceptorService.isOnlyChangeSynchronizationBlocked(restrictionModel, interceptorContext)).thenReturn(false);
		interceptor.setRelatedItemsService(relatedItemsService);
		interceptor.setPageTypePredicate(pageTypePredicate);
	}

	@Test
	public void RestrictionRelatedSharedSlots() throws InterceptorException
	{
		when(cmsRestrictionService.relatedSharedSlots(restrictionModel)).thenReturn(true);

		interceptor.onPrepare(restrictionModel, interceptorContext);

		verify(restrictionModel, times(1)).setSynchronizationBlocked(false);
	}


	@Test
	public void RestrictionRelatedToPage() throws InterceptorException
	{
		when(cmsRestrictionService.relatedSharedSlots(restrictionModel)).thenReturn(false);
		when(relatedItemsService.getRelatedItems((restrictionModel))).thenReturn(Arrays.asList(page));
		when(page.getPk()).thenReturn(PK.createCounterPK(123));
		when(pageTypePredicate.test(page)).thenReturn(true);

		interceptor.onPrepare(restrictionModel, interceptorContext);

		verify(restrictionModel, times(1)).setSynchronizationBlocked(true);
	}

	@Test
	public void RestrictionRelatedNoPage() throws InterceptorException
	{
		when(cmsRestrictionService.relatedSharedSlots(restrictionModel)).thenReturn(false);
		when(relatedItemsService.getRelatedItems((restrictionModel))).thenReturn(Arrays.asList());

		interceptor.onPrepare(restrictionModel, interceptorContext);

		verify(restrictionModel, times(1)).setSynchronizationBlocked(false);
	}
}
