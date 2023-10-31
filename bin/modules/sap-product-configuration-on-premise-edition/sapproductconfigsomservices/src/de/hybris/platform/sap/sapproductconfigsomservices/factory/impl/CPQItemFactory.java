/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.factory.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;


/* (non-Javadoc)
 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
 */
public class CPQItemFactory implements ItemFactory
{
	/*
	 * (non-Javadoc)
	 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
	 */
	@Override
	public Item createItem()
	{
		return new CPQItemSalesDoc();
	}

}
