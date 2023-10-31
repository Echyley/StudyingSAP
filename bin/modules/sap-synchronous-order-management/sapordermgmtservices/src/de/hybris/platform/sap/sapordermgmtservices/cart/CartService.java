/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.cart;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.List;



/**
 * Service for updating cart data like Adding/updating cart entries
 */
public interface CartService extends CartCheckoutBaseService
{



	/**
	 * Adds an entry to the cart. The result contains error messages if those have occurred.
	 *
	 * @param code
	 *           Product ID
	 * @param quantity
	 *           Quantity to be added
	 * @return Cart modification data
	 */
	public abstract CartModificationData addToCart(String code, long quantity);



	/**
	 * Updates a cart entry
	 *
	 * @param entryNumber
	 *           Item number
	 * @param quantity
	 *           New quantity of item
	 * @return Cart modifications
	 */
	public abstract CartModificationData updateCartEntry(long entryNumber, long quantity);

	/**
	 * Validates cart
	 *
	 * @return Modification status as result of the validation
	 */
	List<CartModificationData> validateCartData();

	/**
	 * Does an item exist with a given key?
	 *
	 * @param itemKey
	 *           key for the item.
	 * @return Does item exist for the item key?
	 */
	boolean isItemAvailable(String itemKey);

	/**
	 * Adds an item to the cart.
	 *
	 * @param items
	 *           list of items
	 */
	void addItemsToCart(List<Item> items);

	/**
	 * Adds quick order entries to the cart.
	 *
	 * @param orderEntries
	 *           order entries list
	 * @return list of CartModificationData objects
	 */
	List<CartModificationData> addEntriesToCart(List<OrderEntryData> orderEntries);


}