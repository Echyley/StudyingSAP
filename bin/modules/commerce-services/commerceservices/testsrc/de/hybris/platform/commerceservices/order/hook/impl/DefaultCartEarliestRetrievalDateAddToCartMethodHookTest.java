/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CartRetrievalDateService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartEarliestRetrievalDateAddToCartMethodHookTest
{

	@Spy
	@InjectMocks
	DefaultCartEarliestRetrievalDateAddToCartMethodHook cartEarliestRetrievalDateAddToCartMethodHook;

	@Mock
	CartRetrievalDateService cartRetrievalDateService;

	@Test
	public void testAfterAddToCart() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameters = spy(CommerceCartParameter.class);
		final CommerceCartModification result = spy(CommerceCartModification.class);

		when(cartRetrievalDateService.getCartEarliestRetrievalDate()).thenReturn("9999-12-31");
		cartEarliestRetrievalDateAddToCartMethodHook.afterAddToCart(parameters, result);

		verify(cartRetrievalDateService, times(1)).getCartEarliestRetrievalDate();
	}

}
