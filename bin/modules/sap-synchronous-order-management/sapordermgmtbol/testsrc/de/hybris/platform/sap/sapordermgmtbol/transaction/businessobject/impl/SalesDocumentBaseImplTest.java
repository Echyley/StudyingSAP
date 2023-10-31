/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class SalesDocumentBaseImplTest extends TestCase
{

	private SalesDocumentBaseImpl<ItemList, Item, Header> classUnderTest;

	@Override
	public void setUp()
	{
		classUnderTest = new BasketImpl();

	}

	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}


}
