/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.interaction.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;


/**
 * This interaction object is used to create an order out of an existing document.
 * 
 */
public interface CreateOrder
{

	/**
	 * Create an order with the data of a basket.
	 * 
	 * @param source
	 *           Basket which an order should be created of
	 * @param destination
	 *           Order to create
	 * @param isBasketLinkedToOrder
	 *           is basket linked to order?
	 * @param config
	 *           Configuration to use this creation
	 * @throws CommunicationException
	 *            the communication with the back end failed
	 * @throws BusinessObjectException
	 */
	void createOrderFromBasket(Basket source, Order destination, boolean isBasketLinkedToOrder,
			TransactionConfiguration config) throws CommunicationException, BusinessObjectException;


}
