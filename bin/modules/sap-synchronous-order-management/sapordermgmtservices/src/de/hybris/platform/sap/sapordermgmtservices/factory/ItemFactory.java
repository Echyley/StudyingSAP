/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.factory;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;


/**
 * Factory to create new instance of item
 */
public interface ItemFactory
{
	/**
	 * Return new instance of item
	 *
	 * @return item
	 */
	Item createItem();
}
