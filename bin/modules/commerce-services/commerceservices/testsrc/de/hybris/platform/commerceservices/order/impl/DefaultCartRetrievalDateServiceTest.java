/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.hook.CartEarliestRetrievalDateHook;
import de.hybris.platform.commerceservices.order.strategies.CartRetrievalDateStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartRetrievalDateServiceTest
{

	@Spy
	@InjectMocks
	DefaultCartRetrievalDateService cartRetrievalDateService;

	@Mock
	CartService cartService;
	@Mock
	ModelService modelService;
	@Mock
	CartRetrievalDateStrategy cartRetrievalDateStrategy;
	@Mock
	CartEarliestRetrievalDateHook cartEarliestRetrievalDateHook;

	final String date = "9999-12-31";

	@Before
	public void init()
	{
		cartRetrievalDateService.setCartEarliestRetrievalDateHooks(Collections.singletonList(cartEarliestRetrievalDateHook));
	}

	@Test
	public void testGetCartEarliestRetrievalDate()
	{
		final CartModel cart = spy(CartModel.class);
		final BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cart.getStore()).thenReturn(baseStore);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(true);
		when(cartEarliestRetrievalDateHook.getEarliestRetrievalDates(cart)).thenReturn(Collections.singletonList(date));
		when(cartRetrievalDateStrategy.getCartEarliestRetrievalDate(Mockito.anyList())).thenReturn(date);
		doNothing().when(modelService).save(cart);

		assertNotNull(cartRetrievalDateService.getCartEarliestRetrievalDate());
	}

	@Test
	public void testGetCartEarliestRetrievalDateWhenFeatureDisabled()
	{
		final CartModel cart = spy(CartModel.class);
		final BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cart.getStore()).thenReturn(baseStore);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(false);
		assertNull(cartRetrievalDateService.getCartEarliestRetrievalDate());

	}

	@Test
	public void testUpdateCartRequestedRetrievalDate()
	{
		final CartModel cart = spy(CartModel.class);
		final BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cart.getStore()).thenReturn(baseStore);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(true);
		doNothing().when(cartRetrievalDateStrategy).updateRequestedRetrievalDate(Mockito.any(CartModel.class), Mockito.anyString());
		cartRetrievalDateService.updateCartRequestedRetrievalDate(date);

		verify(cartRetrievalDateStrategy, times(1)).updateRequestedRetrievalDate(Mockito.any(CartModel.class), Mockito.anyString());
	}

	@Test
	public void testUpdateCartRequestedRetrievalDateWhenFeatureDisabled()
	{
		final CartModel cart = spy(CartModel.class);
		final BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cart.getStore()).thenReturn(baseStore);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(false);
		cartRetrievalDateService.updateCartRequestedRetrievalDate(date);

		verify(cartRetrievalDateStrategy, times(0)).updateRequestedRetrievalDate(Mockito.any(CartModel.class), Mockito.anyString());

	}

	@Test
	public void testGetRetrievalDates()
	{
		final CartModel cart = spy(CartModel.class);
		when(cartEarliestRetrievalDateHook.getEarliestRetrievalDates(Mockito.any(CartModel.class)))
				.thenReturn(Collections.singletonList(date));
		final List<String> retrievalDates = cartRetrievalDateService.getRetrievalDates(cart);

		assertNotNull(retrievalDates);
		assertEquals(1, retrievalDates.size());
	}

}
