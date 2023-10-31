/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartRetrievalDateStrategyTest
{

	@Spy
	@InjectMocks
	DefaultCartRetrievalDateStrategy cartRetrievalDateStrategy;

	@Mock
	ModelService modelService;

	String date = "9999-12-31";

	@Test
	public void testUpdateRequestedRetrievalDate()
	{
		final CartModel cart = spy(CartModel.class);
		Mockito.doNothing().when(modelService).save(cart);
		cartRetrievalDateStrategy.updateRequestedRetrievalDate(cart, date);

		assertNotNull(cart.getRequestedRetrievalDate());
	}

	@Test
	public void testGetCartEarliestRetrievalDate()
	{

		final List<String> dates = Collections.singletonList(date);

		final String cartEarliestRetrievalDate = cartRetrievalDateStrategy.getCartEarliestRetrievalDate(dates);

		assertNotNull(cartEarliestRetrievalDate);

	}

	@Test
	public void testGetCartEarliestRetrievalDateNullOrEmptyList()
	{

		final String cartEarliestRetrievalDateNull = cartRetrievalDateStrategy.getCartEarliestRetrievalDate(null);

		final String cartEarliestRetrievalDateEmptyList = cartRetrievalDateStrategy
				.getCartEarliestRetrievalDate(Collections.emptyList());

		assertNull(cartEarliestRetrievalDateNull);
		assertNull(cartEarliestRetrievalDateEmptyList);

	}

	@Test
	public void testGetCartEarliestRetrievalDateInvalidDate()
	{

		final List<String> dates = Collections.singletonList("31/12/9999");

		final String cartEarliestRetrievalDate = cartRetrievalDateStrategy.getCartEarliestRetrievalDate(dates);

		assertNull(cartEarliestRetrievalDate);

	}

}
