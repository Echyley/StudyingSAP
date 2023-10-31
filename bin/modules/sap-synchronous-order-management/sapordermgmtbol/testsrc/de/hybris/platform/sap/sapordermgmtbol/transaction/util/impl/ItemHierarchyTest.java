/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import junit.framework.TestCase;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class ItemHierarchyTest extends TestCase
{

	private ItemHierarchy classUnderTest;
	private final ItemList itemList = new ItemListImpl();

	private void init()
	{
		classUnderTest = new ItemHierarchy(itemList);
	}

	@Test
	public void testGetNode()
	{
		addItemToList("A");
		addItemToList("B");
		init();

		assertNotNull(classUnderTest.getNode(new TechKey("A")));
		assertNull(classUnderTest.getNode(new TechKey("C")));
	}

	private void addItemToList(final String techKey)
	{
		final Item item = new ItemSalesDoc();
		item.setTechKey(new TechKey(techKey));
		itemList.add(item);
	}
}
