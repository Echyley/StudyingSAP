/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.returnsexchange.inbound;

public interface DataHubInboundCancelOrderHelper extends DataHubInboundOrderHelper {
	 /**
     * Trigger subsequent actions after cancel return order confirmation has arrived
     * 
     * @param orderNumber
     */
    void processCancelOrderConfirmationFromDataHub (final String orderNumber);
    
}
