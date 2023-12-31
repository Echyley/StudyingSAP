/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.order.SapOaaCartAdjustmentStrategy;


/**
 *
 */
@UnitTest
public class DefaultSapOaaCommerceAddToCartStrategyTest
{
	private DefaultSapOaaCommerceAddToCartStrategy classUnderTest = null;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSapOaaCommerceAddToCartStrategy();
	}

	@Test
	public void getAllowedCartAdjustmentForProductTest()
	{
		final long qtyToAdd = 10;
		final Long stockAvailQty = Long.valueOf("5");

		final CartModel cartModel = new CartModel();
		final ProductModel productModel = new ProductModel();
		
		final CommonUtils commonUtilsMock = EasyMock.createNiceMock(CommonUtils.class);
		EasyMock.expect(commonUtilsMock.isCAREnabled()).andReturn(false);
		EasyMock.expect(commonUtilsMock.isCOSEnabled()).andReturn(true);
		EasyMock.replay(commonUtilsMock);
		classUnderTest.setCommonUtils(commonUtilsMock);

		// Mock the CartService because of usage in checkCartLevel()-method
		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		EasyMock.expect(cartServiceMock.getEntriesForProduct(cartModel, productModel)).andReturn(new ArrayList<CartEntryModel>());
		EasyMock.replay(cartServiceMock);
		classUnderTest.setCartService(cartServiceMock);

		// Create OaaCartAdjustmentStrategyMock
		final SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategyMock = EasyMock
				.createNiceMock(SapOaaCartAdjustmentStrategy.class);
		EasyMock.expect(
				oaaCartAdjustmentStrategyMock.determineAllowedCartAdjustmentForProduct(cartModel, productModel, qtyToAdd, 0, null))
				.andReturn(stockAvailQty);
		EasyMock.replay(oaaCartAdjustmentStrategyMock);
		classUnderTest.setOaaCartAdjustmentStrategy(oaaCartAdjustmentStrategyMock);


		final long actualAllowedQty = classUnderTest.getAllowedCartAdjustmentForProduct(cartModel, productModel, qtyToAdd, null);

		Assert.assertEquals(stockAvailQty.longValue(), actualAllowedQty);
	}
}
