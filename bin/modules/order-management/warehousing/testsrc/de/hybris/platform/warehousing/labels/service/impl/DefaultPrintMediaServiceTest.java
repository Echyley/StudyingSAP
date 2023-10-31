/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */

package de.hybris.platform.warehousing.labels.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPrintMediaServiceTest
{
	private final static String CONS_TEMPLATE_NAME = "Cons_Temp_Name";

	@InjectMocks
	private DefaultPrintMediaService printMediaService;
	@Mock
	private ImpersonationService impersonationService;
	@Mock
	private ModelService modelService;
	@Mock
	private ConsignmentModel consignment;
	@Mock
	private OrderModel order;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private ConsignmentProcessModel consignmentProcess;
	@Mock
	private MediaModel consMediaModel;
	@Mock
	private MediaService mediaService;

	@Before
	public void setUp()
	{
		printMediaService.setImpersonationService(impersonationService);

		Mockito.lenient().when(consignment.getCode()).thenReturn("Cons_Code");
		Mockito.lenient().when(consignment.getOrder()).thenReturn(order);
		Mockito.lenient().when(consignmentProcess.getCode()).thenReturn("Cons_Code_Ordermanagement");
		Mockito.lenient().when(consignmentProcess.getConsignment()).thenReturn(consignment);
		Mockito.lenient().when(consignment.getConsignmentProcesses()).thenReturn(Collections.singleton(consignmentProcess));

		Mockito.lenient().when(order.getSite()).thenReturn(baseSite);
		Mockito.lenient().when(order.getStore()).thenReturn(baseStore);
		Mockito.lenient().when(baseStore.getCreateReturnProcessCode()).thenReturn("Return_Code_Ordermanagement");
		Mockito.lenient().when(printMediaService.getMediaForTemplate(CONS_TEMPLATE_NAME, consignmentProcess)).thenReturn(consMediaModel);

		Mockito.lenient().doNothing().when(modelService).save(any());
		Mockito.lenient().doReturn(consMediaModel).when(impersonationService).executeInContext(nullable(ImpersonationContext.class), any());
	}

	@Test
	public void testGetConsignmentMediaForTemplate()
	{
		//When
		final MediaModel mediaModel = printMediaService.getMediaForTemplate(CONS_TEMPLATE_NAME, consignmentProcess);

		//Then
		verify(impersonationService).executeInContext(any(ImpersonationContext.class), any());
		assertEquals(mediaModel, consMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateHtmlMediaTemplateWhenMediaModelNull()
	{
		//When
		printMediaService.generateHtmlMediaTemplate(null);
	}

	@Test
	public void testGenerateHtmlMediaTemplateSuccess()
	{
		//Given
		final MediaModel mediaModel = printMediaService.getMediaForTemplate(CONS_TEMPLATE_NAME, consignmentProcess);
		when(printMediaService.getMediaService()).thenReturn(mediaService);
		when(mediaService.getDataFromMedia(mediaModel)).thenReturn(new byte[1]);

		//When
		printMediaService.generateHtmlMediaTemplate(mediaModel);

		//Then
		verify(mediaService).getDataFromMedia(mediaModel);
	}

}
