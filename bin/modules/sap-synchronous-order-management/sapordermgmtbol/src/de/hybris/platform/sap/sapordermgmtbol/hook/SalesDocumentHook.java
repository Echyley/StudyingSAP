/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.hook;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import java.util.List;


/**
 * Hook interface for SalesDocument
 *
 */
public interface SalesDocumentHook
{

	/**
	 * @param techKey
	 */
	void afterDeleteItemInBackend(TechKey techKey);

	/**
	 * @param itemList
	 */
	void afterDeleteItemInBackend(ItemList itemList);

	/**
	 * @param itemList
	 */
	void afterDeleteItemInBackend(List<TechKey> itemsToDelete);

	/**
	 * @param itemsToDelete
	 */
	void afterUpdateItemInBackend(List<TechKey> itemsToDelete);

}
