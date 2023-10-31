/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Helper for synchronizing the B2C customer replication with the order replication process
 */

public interface B2CCustomerHelper
{
	/**
	 * Determine the SAP customer ID of the customer who created the order 
	 * @param order
	 * @return SAP Customer ID
	 */
	String determineB2CCustomer(OrderModel order);

	void processWaitingCustomerOrders(final String customerID);

}
