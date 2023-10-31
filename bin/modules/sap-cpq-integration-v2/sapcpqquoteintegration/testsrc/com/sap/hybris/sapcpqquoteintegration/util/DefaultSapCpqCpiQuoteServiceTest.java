/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.util;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapcpqquoteintegration.outbound.service.impl.DefaultSapCpqCpiQuoteService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/*
 * UnitTest for {@link DefaultSapCpqCpiQuoteService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqCpiQuoteServiceTest{
	
	@Mock
	private DefaultSapCpqCpiQuoteService defaultSapCpqCpiQuoteService;
	
	@Mock
	private DefaultCommerceQuoteService defaultCommerceQuoteService;

	@Mock
	private  QuoteModel quoteModel;
	
	@Mock
	private  UserModel userModel;

	@Test
	public void cancelQuoteWithoutConsumedDestination() {
		defaultSapCpqCpiQuoteService.cancelQuote(quoteModel, userModel);
	    Mockito.verify(defaultCommerceQuoteService,Mockito.atMostOnce()).cancelQuote(quoteModel, userModel);
	}
	
	@Test
	public void submitQuoteWithoutConsumedDestination() {
		defaultSapCpqCpiQuoteService.submitQuote(quoteModel, userModel);
	    Mockito.verify(defaultCommerceQuoteService,Mockito.atMostOnce()).submitQuote(quoteModel, userModel);
	}
	
	@Test
	public void getAllowedActionsWithoutConsumedDestination() {
		defaultSapCpqCpiQuoteService.getAllowedActions(quoteModel, userModel);
	    Mockito.verify(defaultCommerceQuoteService,Mockito.atMostOnce()).cancelQuote(quoteModel, userModel);
	}
	
	@Test
	public void requoteWithoutConsumedDestination() {
		defaultSapCpqCpiQuoteService.requote(quoteModel, userModel);
	    Mockito.verify(defaultCommerceQuoteService,Mockito.atMostOnce()).cancelQuote(quoteModel, userModel);
	}
}

