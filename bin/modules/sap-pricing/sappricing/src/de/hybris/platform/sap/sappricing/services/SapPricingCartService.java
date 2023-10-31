/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Sap Pricing Cart Service
 */
public interface SapPricingCartService
{

	/**
	 * Method to get price information for cart
	 * 
	 * @param order orderModel
	 */
	void getPriceInformationForCart(AbstractOrderModel order);

}
