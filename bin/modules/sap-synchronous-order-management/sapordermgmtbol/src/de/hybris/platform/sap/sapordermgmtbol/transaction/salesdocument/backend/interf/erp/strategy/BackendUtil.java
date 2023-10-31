/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;


/**
 * Utilities for ERP backend layer
 */
public interface BackendUtil
{


	/**
	 * Find an item via its POSNR
	 * 
	 * @param items
	 * @param posnr
	 * @return null if no item could be found
	 */
	Item findItem(ItemList items, String posnr);

}
