/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.List;


/**
 * Hook interface for SapCartFacade
 */
public interface SapCartFacadeHook
{

	/**
	 * @param quantity
	 * @param entryNumber
	 * @param entries
	 */
	void beforeCartEntryUpdate(long quantity, long entryNumber, List<OrderEntryData> entries);

}
