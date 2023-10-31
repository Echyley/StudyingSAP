/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CartRetrievalDateFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CartRetrievalDateService;


/**
 * Default implementation of {@link CartRetrievalDateFacade}
 */
public class DefaultCartRetrievalDateFacade implements CartRetrievalDateFacade
{

	private CartRetrievalDateService cartRetrievalDateService;
	private CartFacade cartFacade;

	@Override
	public String getCartEarliestRetrievalDate()
	{
		final CartData cart = cartFacade.getSessionCart();
		return cart.getEarliestRetrievalAt() != null ? cart.getEarliestRetrievalAt()
				: getCartRetrievalDateService().getCartEarliestRetrievalDate();
	}

	@Override
	public void updateCartRequestedRetrievalDate(final String requestedDeliveryDate)
	{
		getCartRetrievalDateService().updateCartRequestedRetrievalDate(requestedDeliveryDate);
	}

	public CartRetrievalDateService getCartRetrievalDateService()
	{
		return cartRetrievalDateService;
	}

	public void setCartRetrievalDateService(final CartRetrievalDateService cartRetrievalDateService)
	{
		this.cartRetrievalDateService = cartRetrievalDateService;
	}

	public CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}



}
