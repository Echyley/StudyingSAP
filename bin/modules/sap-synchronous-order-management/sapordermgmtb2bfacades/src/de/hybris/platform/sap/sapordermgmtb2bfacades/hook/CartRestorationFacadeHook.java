/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Hook interface for CartRestorationFacade
 *
 */
public interface CartRestorationFacadeHook
{

	/**
	 * @param entry
	 * @param entryModel
	 */
	void afterAddCartEntriesToStandardCart(OrderEntryData entry, AbstractOrderEntryModel entryModel);

}
