/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.BackendUtil;

import java.math.BigDecimal;


/**
 * Backend utility implementation
 */
public class BackendUtilImpl implements BackendUtil
{




	@Override
	public Item findItem(final ItemList items, final String posnr)
	{
		final int internalNumber = new BigDecimal(posnr).intValue();
		for (final Item item : items)
		{
			if (item.getNumberInt() == internalNumber)
			{
				return item;
			}
		}
		return null;
	}
}
