/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;

import org.easymock.EasyMock;


/**
 * 
 */
public class CartServiceMockBuilder
{

	CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);

	public static CartServiceMockBuilder create()
	{
		return new CartServiceMockBuilder();
	}

	public CartServiceMockBuilder withStandardAddToCart(final long quantity, final String productCode)
	{
		final ProductData productData = new ProductData();
		productData.setCode(productCode);

		final CartModificationData cartModificationData = new CartModificationData();
		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setProduct(productData);
		cartModificationData.setEntry(orderEntryData);
		EasyMock.expect(cartServiceMock.addToCart(productCode, quantity)).andReturn(cartModificationData).anyTimes();

		return this;
	}

	public CartService build()
	{
		EasyMock.replay(cartServiceMock);
		return cartServiceMock;
	}

	/**
	 * @return
	 */
	public OrderEntryDataMockBuilder withProductImageHelper()
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
