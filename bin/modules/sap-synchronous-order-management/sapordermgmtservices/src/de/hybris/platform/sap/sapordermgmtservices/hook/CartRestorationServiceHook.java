/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.hook;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;


/**
 * Hook interface for CartRestorationService
 *
 */
public interface CartRestorationServiceHook
{

	/**
	 * @param orderEntry
	 * @param item
	 */
	void afterCreateItemHook(AbstractOrderEntryModel orderEntry, Item item);

}
