/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the ConnectedDocument object<br>
 * 
 */
public interface ConnectedDocument extends ConnectedObject {

    /**
     * Application Type Order.
     */
    String ORDER = "";
    /**
     * Application Type Billing.
     */
    String BILL = "BILL";
    /**
     * Application Type Delivery.
     */
    String DLVY = "DLVY";

    /**
     * Returns the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @return Document application type
     */
    String getAppTyp();

    /**
     * Sets the application type of the document(e.g. one order document,
     * billing document, etc.).<br>
     * 
     * @param appTyp Document application type
     */
    void setAppTyp(String appTyp);

}
