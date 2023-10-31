/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.datahub.inbound;

import de.hybris.platform.impex.jalo.ImpExException;


/**
 * Data Hub Inbound Helper for Order related notifications
 */
public interface DataHubInboundOrderHelper
{

	/**
	 * Trigger subsequent actions after order conformation has arrived
	 * @param orderNumber
	 */
	void processOrderConfirmationFromHub(final String orderNumber);
	
	/**
	 * Cancel order which was cancelled on ERP side also on hybris side
	 * @param orderInformation
	 * @param orderCode
	 * @throws ImpExException
	 */
	void cancelOrder(final String orderInformation, final String orderCode) throws ImpExException;
	
}
