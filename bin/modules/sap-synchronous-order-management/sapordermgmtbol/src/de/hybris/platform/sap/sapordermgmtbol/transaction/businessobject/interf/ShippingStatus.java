/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the ShippingStatus object. <br>
 * 
 */
public interface ShippingStatus extends BusinessStatus {

    /**
     * Initializes the ShippingStatus object <br>
     * 
     * @param dlvStatus - Delivery Status
     * @param giStatus - Guts issue status
     * @param rjStatus - Rejection Status
     */
    void init(EStatus dlvStatus, EStatus giStatus, EStatus rjStatus);

}
