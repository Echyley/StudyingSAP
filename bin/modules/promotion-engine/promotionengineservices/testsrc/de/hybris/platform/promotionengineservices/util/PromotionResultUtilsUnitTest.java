/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.promotionengineservices.util;



import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.internal.model.order.InMemoryCartModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionResultUtilsUnitTest
{
	@InjectMocks
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private CartService cartService;

	@Mock
	private InMemoryCartModel sessionCart;

	@Mock
	private CartModel cart;

	@Test
	public void testGetReferredOrder()
	{
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrder(cart);
		Assert.assertEquals(cart, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderByCode()
	{
		Mockito.lenient().when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.TRUE);
		final String orderCode = "00001";
		Mockito.lenient().when(sessionCart.getCode()).thenReturn(orderCode);
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrderCode(orderCode);
		Assert.assertEquals(sessionCart, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderHasSessionCartWithWrongCode()
	{
		Mockito.lenient().when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(sessionCart.getCode()).thenReturn("00001");
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		promotionResult.setOrderCode("00002");
		Assert.assertEquals(null, promotionResultUtils.getOrder(promotionResult));
	}

	@Test
	public void testGetOrderHasNoSessionCart()
	{
		Mockito.lenient().when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(sessionCart);
		final PromotionResultModel promotionResult = new PromotionResultModel();
		Assert.assertEquals(null, promotionResultUtils.getOrder(promotionResult));
	}
}
