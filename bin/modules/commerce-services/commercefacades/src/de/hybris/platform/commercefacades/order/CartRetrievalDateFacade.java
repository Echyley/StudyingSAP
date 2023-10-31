/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order;

/**
 * Cart retrieval date facade updates cart with requested retrieval date and gets earliest retrieval date for cart.
 */
public interface CartRetrievalDateFacade
{

	/**
	 * Updates cart with requested retrieval date
	 *
	 * @param requestedRetrievalDate
	 *           The requested retrieval date
	 */
	public void updateCartRequestedRetrievalDate(String requestedRetrievalDate);

	/**
	 * Returns the earliest retrieval date for cart.
	 *
	 * @return earliest retrieval date
	 */
	public String getCartEarliestRetrievalDate();

}
