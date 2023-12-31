/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper for validating and formatting intervals of characteristic domain.
 */
public interface IntervalInDomainHelper
{
	/**
	 * Retrieves concatenated string of intervals in characteristic domain.
	 *
	 * @param cstic
	 *           characteristic model
	 * @return concatenated string of intervals in characteristic domain
	 */
	String retrieveIntervalMask(final CsticModel cstic);

	/**
	 * Converts an interval of characteristic domain into an external format.
	 *
	 * @param interval
	 *           interval in internal format
	 * @return interval in external format
	 */
	String formatNumericInterval(final String interval);

	/**
	 * Retrieves error message.
	 *
	 * @param value
	 *           characteristic value in external format
	 * @param interval
	 *           interval in external format
	 * @return error message
	 */
	String retrieveErrorMessage(String value, String interval);

}
