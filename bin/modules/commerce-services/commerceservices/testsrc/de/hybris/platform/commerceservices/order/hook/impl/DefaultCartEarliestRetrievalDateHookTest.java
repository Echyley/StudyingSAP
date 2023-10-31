/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartEarliestRetrievalDateHookTest
{
	@Spy
	@InjectMocks
	DefaultCartEarliestRetrievalDateHook cartEarliestRetrievalDateHook;

	@Test
	public void testGetRequestedRetrievalDates()
	{
		final CartModel cart = spy(CartModel.class);

		assertNotNull(cartEarliestRetrievalDateHook.getEarliestRetrievalDates(cart));
	}

}
