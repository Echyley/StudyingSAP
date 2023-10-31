/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the OverallStatusOrder object. <br>
 * 
 */
public interface OverallStatusOrder extends OverallStatus {

    /**
     * Initializes the OverallStatusOrder object <br>
     * 
     * @param procStatus - Processing Status
     * @param shippingStatus - Shipping Status
     * @param billingStatus - Billing Status
     * @param rjStatus - Rejection Status
     */
    void init(EStatus procStatus,
                     ShippingStatus shippingStatus,
                     BillingStatus billingStatus,
                     EStatus rjStatus);

}
