/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.cart;


import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Service needed to access the standard session cart (basing on the hybris persistence) and to create a BOL cart from a
 * standard session cart.
 * 
 */
public interface CartRestorationService
{

	/**
	 * Returns the standard session cart, basing on the hybris persistence
	 * 
	 * @return hybris cart model
	 */
	CartModel getInternalSessionCart();

	/**
	 * Sets session cart (stores it into the session)
	 * 
	 * @param cart
	 */
	void setInternalSessionCart(CartModel cart);

	/**
	 * Does a standard session cart exist?
	 * 
	 * @return true if a hybris session cart exists
	 */
	boolean hasInternalSessionCart();

	/**
	 * Removes the internal session cart, basing on the hybris persistence
	 */
	void removeInternalSessionCart();


	/**
	 * Restores a BOL cart from the persisted standard session cart
	 * 
	 * @param fromCart
	 *           Session cart in hybris persistence
	 * @return Restorated cart, basing on the BOL cart
	 */
	CartRestorationData restoreCart(CartModel fromCart);




}
