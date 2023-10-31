/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.businessobject.impl;

import de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailability;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Immutable Object
 * 
 * 
 */
public class SapProductAvailabilityImpl implements SapProductAvailability
{

	private final Long currentStockLevel;

	private final Map<String, Map<Date, Long>> futureAvailability;

	/**
	 * @param currentStockLevel
	 * @param futureAvailability
	 */
	public SapProductAvailabilityImpl(final Long currentStockLevel, final Map<String, Map<Date, Long>> futureAvailability)
	{
		this.currentStockLevel = currentStockLevel;
		this.futureAvailability = new HashMap<String, Map<Date, Long>>(futureAvailability);
	}

	@Override
	public Long getCurrentStockLevel()
	{
		return this.currentStockLevel;
	}

	@Override
	public Map<String, Map<Date, Long>> getFutureAvailability()
	{
		return new HashMap<String, Map<Date, Long>>(this.futureAvailability);
	}

}
