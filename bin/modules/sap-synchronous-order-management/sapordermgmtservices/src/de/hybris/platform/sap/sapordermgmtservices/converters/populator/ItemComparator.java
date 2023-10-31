/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Comparator;


/**
 * 
 */
public class ItemComparator implements Comparator<Item>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Item o1, final Item o2)
	{
		//When the numberInt is greater the Item was created first so return 1
		if (o1.getNumberInt() > o2.getNumberInt())
		{
			return 1;
		}
		else if (o1.getNumberInt() < o2.getNumberInt())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

}
