/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.factory.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;


/* (non-Javadoc)
 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory
 */
public class DefaultItemFactory implements ItemFactory
{

	/* (non-Javadoc)
	 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
	 */
	@Override
	public Item createItem()
	{
		return new ItemSalesDoc();
	}

}
