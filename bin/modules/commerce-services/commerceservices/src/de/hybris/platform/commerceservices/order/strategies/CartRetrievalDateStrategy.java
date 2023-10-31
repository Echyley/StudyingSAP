/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies;

import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Cart retrieval date strategy updates cart with requested retrieval date and gets earliest retrieval date for cart.
 */
public interface CartRetrievalDateStrategy
{

	/**
	 * Updates cart with requested retrieval date
	 *
	 * @param cart
	 *           Cart
	 * @param retrievalDate
	 *           The requested retrieval date
	 */
	public void updateRequestedRetrievalDate(CartModel cart, String retrievalDate);

	/**
	 * Returns the earliest retrieval date for list of retrieval dates.
	 *
	 * @param retrievalDates
	 *           List of retrieval dates
	 * @return The earliest retrieval date
	 */
	public String getCartEarliestRetrievalDate(List<String> retrievalDates);

}
