/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.order.hook.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4ccpiquote.events.SapC4CCpiQuoteBuyerOrderPlacedEvent;
import com.sap.hybris.c4ccpiquote.service.C4CConsumedDestinationService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.EventService;


/**
 *
 */
@UnitTest
public class C4CCpiPlaceQuoteOrderMethodHookTest
{
	@InjectMocks
	private final C4CCpiPlaceQuoteOrderMethodHook placeQuoteOrderMethodHook = new C4CCpiPlaceQuoteOrderMethodHook();

	@Mock
	private EventService eventService;
	
	@Mock
	private C4CConsumedDestinationService c4CConsumedDestinationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAfterPlaceOrder()
	{
		doNothing().when(eventService).publishEvent(Mockito.anyObject());
		Mockito.lenient().when(c4CConsumedDestinationService.checkIfDestinationExists(Mockito.anyString())).thenReturn(Boolean.TRUE);

		final QuoteModel quote = new QuoteModel();
		quote.setCode("123456");
		final OrderModel order = new OrderModel();
		order.setCode("12345");
		order.setQuoteReference(quote);

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

		placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

		verify(eventService, times(1)).publishEvent(Mockito.any(SapC4CCpiQuoteBuyerOrderPlacedEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAfterPlaceOrderWithOrderNull()
	{

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		orderResult.setOrder(null);
		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

		placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

		verify(eventService, times(0)).publishEvent(Mockito.any(SapC4CCpiQuoteBuyerOrderPlacedEvent.class));
	}


}
