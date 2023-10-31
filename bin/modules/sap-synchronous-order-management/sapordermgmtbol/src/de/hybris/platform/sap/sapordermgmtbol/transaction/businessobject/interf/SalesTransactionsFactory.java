/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

/**
 * A sales document provides factory methods for its sub objects.<br>
 * However in some cases these sub objects should be created prior the sales
 * document itself, hence this factory provides factory methods to create these
 * objects independently from any sales document.<br>
 * 
 * @version 1.0
 */
public interface SalesTransactionsFactory {

    /**
     * Creates a new ship object
     * 
     * @return Newly created shipTo
     */
    ShipTo createShipTo();

    /**
     * Creates a new billTo object
     * 
     * @return Newly created billTo
     */
    BillTo createBillTo();

    /**
     * Gets a new Item object<br>
     * 
     * @return new item instance
     * @see de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocumentBase#createItem()
     */
    Item createSalesDocumentItem();

}
