/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook;

import de.hybris.platform.core.model.order.CartModel;


/**
 * Hook interface for CartMerging
 */
public interface CommerceCartMergingMethodHook
{
	/**
	 * Executed before the cart merging
	 *
	 * @param fromCart
	 *           object containing from cart model
	 * @param toCart
	 *           object containing to cart model
	 */
	void beforeCartMerge(CartModel fromCart, CartModel toCart);

	/**
	 * Executed after the cart merging
	 * 
	 * @param fromCart
	 *           object containing from cart model
	 * @param toCart
	 *           object containing to cart model
	 */
	void afterCartMerge(CartModel fromCart, CartModel toCart);

}
