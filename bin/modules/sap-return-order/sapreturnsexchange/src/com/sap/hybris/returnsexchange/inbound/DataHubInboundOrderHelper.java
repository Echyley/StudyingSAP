/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.returnsexchange.inbound;
/**
 *
 */
public interface DataHubInboundOrderHelper {

    /**
     * Trigger subsequent actions after order confirmation has arrived
     *
     * @param orderNumber
     */
    void processOrderConfirmationFromDataHub(final String orderNumber); 
    
    /**
     * Trigger subsequent actions after delivery and post goods receipt notification has arrived
     * @param orderNumber
     * @param delivInfo
     */
    void processOrderDeliveryNotififcationFromDataHub(final String orderNumber, final String delivInfo);
    
    
}
