/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.checkout;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartCheckoutBaseService;

import java.util.List;


/**
 * Service representation of checkout with SAP synchronous order management
 */
public interface CheckoutService extends CartCheckoutBaseService
{
	/**
	 * Submits an order from the current session cart which is held in the SAP back end.
	 * 
	 * @return The order that has been persisted in the SAP back end
	 */
	public abstract OrderData placeOrder();

	/**
	 * Retrieving delivery modes from SAP back end. In SAP terminology, this is mostly referred to as 'Shipping
	 * Condition'
	 * 
	 * @return The list of available delivery modes. These are read from the SAP back end.
	 */
	List<DeliveryModeData> getSupportedDeliveryModes();

	/**
	 * Sets a delivery mode code into the current session cart which is held in SD, and updates the cart afterwards as
	 * prices can change.
	 * 
	 * @param deliveryModeCode
	 *           The new delivery mode code. Named 'Shipping condition' in SAP back end terms.
	 * @return Has this action been performed successfully?
	 */
	boolean setDeliveryMode(String deliveryModeCode);

	/**
	 * Sets the purchase order number into the current session cart which is held in SD, and updates the cart afterwards.
	 * 
	 * @param purchaseOrderNumber
	 *           Purchase order number
	 * @return Has this action been performed successfully?
	 */
	boolean setPurchaseOrderNumber(String purchaseOrderNumber);

	/**
	 * Sets current delivery address. It's not possible to set a document specific address, instead the ID of a valid
	 * ship-to party needs to be passed.
	 * 
	 * @param sapCustomerId
	 *           Technical key of an back end ship-to party, typically with length 10
	 * 
	 * @return Did it succeed?
	 * 
	 */
	public abstract boolean setDeliveryAddress(String sapCustomerId);

	/**
	 * Update the checkout cart quantity,
	 * 
	 * @param cartData Cartdata values
	 * @return cartData after update.
	 */
	public CartData updateCheckoutCart(CartData cartData);

}
