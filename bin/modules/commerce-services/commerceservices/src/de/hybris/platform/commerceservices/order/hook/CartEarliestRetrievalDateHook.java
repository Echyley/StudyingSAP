/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook;

import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Cart earliest retrieval date hook gets earliest retrieval dates for cart.
 */
public interface CartEarliestRetrievalDateHook
{

	/**
	 * Returns list of earliest retrieval dates for given cart.
	 *
	 * @param cart
	 *           cart
	 * @return list of earliest retrieval dates for given cart
	 */
	public List<String> getEarliestRetrievalDates(CartModel cart);

}
