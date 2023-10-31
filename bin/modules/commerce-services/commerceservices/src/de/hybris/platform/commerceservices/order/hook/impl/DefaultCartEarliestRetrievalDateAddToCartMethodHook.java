/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.order.CartRetrievalDateService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


/**
 * Default implementation of {@link CommerceAddToCartMethodHook} for cart earliest retrieval date while add to cart.
 */
public class DefaultCartEarliestRetrievalDateAddToCartMethodHook implements CommerceAddToCartMethodHook
{

	private CartRetrievalDateService cartRetrievalDateService;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		//Implementation not required
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		cartRetrievalDateService.getCartEarliestRetrievalDate();
	}

	public CartRetrievalDateService getCartRetrievalDateService()
	{
		return cartRetrievalDateService;
	}

	public void setCartRetrievalDateService(final CartRetrievalDateService cartRetrievalDateService)
	{
		this.cartRetrievalDateService = cartRetrievalDateService;
	}

}
