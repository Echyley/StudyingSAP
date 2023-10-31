/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu;

import java.time.Duration;
import java.util.List;


/**
 * Service for retrieving information about database utilization
 */
public interface DatabaseUtilizationService
{

	/**
	 * Returns DatabaseUtilization objects for given duration (counted from now)
	 * <p>
	 * List must be sorted and returned in <b>descending</b> order of observationTime.
	 * @param duration
	 * @return the list of DatabaseUtilization objects
	 */
	List<DatabaseUtilization> getUtilization(Duration duration);

	/**
	 * Checks if the utilization can be determined at all.
	 * @return True for readiness
	 */
	default boolean isActive()
	{
		return true;
	}

	/**
	 * Returns the reason if the service is not active.
	 * @return The reason or null
	 */
	default String getStatusReason()
	{
		return null;
	}
}
