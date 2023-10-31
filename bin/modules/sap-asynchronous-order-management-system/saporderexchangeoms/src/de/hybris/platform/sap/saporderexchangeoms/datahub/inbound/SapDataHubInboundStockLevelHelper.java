/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound;

import de.hybris.platform.jalo.Item;

/**
 * OMS data hub inbound helper for stock level change notifications
 */
public interface SapDataHubInboundStockLevelHelper
{
	/**
	 * After the stock replication from ERP, we have one of the following scenarios:
	 * 1. Not shipped allocations + hybris ATP >  ERP Stock Level  -> Increase inventory event (-the difference)
	 * 2. Not shipped allocations + hybris ATP <  ERP Stock Level  -> Increase inventory event (the difference)
	 * 3. Not shipped allocations + hybris ATP =  ERP Stock Level  -> Do not do anything
	 * @param stockLevelQuantity
	 * @param stockLevelItem
	 */
	void processStockLevelNotification(String stockLevelQuantity, Item stockLevelItem);

}