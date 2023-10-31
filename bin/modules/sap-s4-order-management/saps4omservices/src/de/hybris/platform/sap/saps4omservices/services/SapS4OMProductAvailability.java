/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import java.util.List;

import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;


public interface SapS4OMProductAvailability
{

	Long getCurrentStockLevel();
	List<FutureStockModel> getFutureAvailability();
	StockLevelModel getStockLevelModel();
}
