/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.events;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;


/**
 *
 */
public class SapC4CCpiQuoteBuyerOrderPlacedEventListenerTest
{

	private static final Logger LOG = Logger.getLogger(SapC4CCpiQuoteBuyerOrderPlacedEventListenerTest.class);
	
	@InjectMocks
	private final SapC4CCpiQuoteBuyerOrderPlacedEventListener listner = new SapC4CCpiQuoteBuyerOrderPlacedEventListener();
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceQuoteService commerceQuoteService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOnEvent()
	{
		final SapC4CCpiQuoteBuyerOrderPlacedEvent event = mock(SapC4CCpiQuoteBuyerOrderPlacedEvent.class);

		final QuoteUserType quoteUserType = mock(QuoteUserType.class);

		final QuoteModel quote = mock(QuoteModel.class);
		when(event.getQuote()).thenReturn(quote);
		when(quote.getCode()).thenReturn("DummyCode");

		final OrderModel orderModel = mock(OrderModel.class);
		when(event.getOrder()).thenReturn(orderModel);

		final QuoteModel quoteSnapShot = mock(QuoteModel.class);

		when(commerceQuoteService.createQuoteSnapshotWithState(quote, QuoteState.BUYER_ORDERED))
				.thenReturn(quoteSnapShot);
		final QuoteProcessModel quoteBuyerProcessModel = mock(QuoteProcessModel.class);
		when(businessProcessService.createProcess(anyString(), anyString(), any(Map.class))).thenReturn(quoteBuyerProcessModel);

		final BaseStoreModel store = mock(BaseStoreModel.class);
		when(quote.getStore()).thenReturn(store);
		when(store.getUid()).thenReturn("DummyStoreUID");
		try
		{
			listner.onEvent(event);
		}
		catch (final Exception e)
		{
			LOG.info(e);
			Assert.fail("Exception Occured");
		}
	}


}
