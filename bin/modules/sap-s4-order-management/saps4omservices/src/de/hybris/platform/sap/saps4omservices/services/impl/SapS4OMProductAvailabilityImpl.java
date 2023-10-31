/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;


import java.util.List;

import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;


/**
 * Immutable Object
 * 
 * 
 */
public class SapS4OMProductAvailabilityImpl implements SapS4OMProductAvailability
{

	private final Long currentStockLevel;

	private final List<FutureStockModel>  futureAvailability;
	private final StockLevelModel stockLevelModel;

	/**
	 * @param currentStockLevel
	 * @param futureAvailability
	 */
	public SapS4OMProductAvailabilityImpl(final Long currentStockLevel, final List<FutureStockModel>  futureAvailability,StockLevelModel stockLevelModel)
	{
		this.currentStockLevel = currentStockLevel;
		this.futureAvailability = futureAvailability;
		this.stockLevelModel  = stockLevelModel;
	}

	@Override
	public Long getCurrentStockLevel()
	{
		return this.currentStockLevel;
	}


	public List<FutureStockModel> getFutureAvailability() {
		return futureAvailability;
	}

	public StockLevelModel getStockLevelModel() {
		return stockLevelModel;
	}


}
