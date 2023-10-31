/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the BillingStatus object. <br>
 * 
 */
public interface BillingStatus extends BusinessStatus {

    /**
     * Initializes the BillingStatus object.<br>
     * 
     * @param dlvStatus Delivery Status
     * @param ordInvoiceStatus Order Invoice Status
     * @param dlvInvoiceStatus Delivery Invoice Status
     * @param rjStatus Rejection Status
     */
    void init(EStatus dlvStatus,
                     EStatus ordInvoiceStatus,
                     EStatus dlvInvoiceStatus,
                     EStatus rjStatus);

}
