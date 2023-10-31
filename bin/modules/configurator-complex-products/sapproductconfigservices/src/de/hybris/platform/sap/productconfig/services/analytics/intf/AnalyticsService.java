/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.analytics.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;


/**
 * The analytics service retrieves an analytics document.
 *
 */
public interface AnalyticsService
{

	/**
	 * Retrieves the analytic document
	 *
	 * @param configId
	 *           id of the configuration
	 * @return analytical data
	 */
	AnalyticsDocument getAnalyticData(String configId);

	/**
	 * Indicates whether the underlying analytical provider is active
	 *
	 * @return true if the underlying pricing provider is active
	 */
	boolean isActive();
}
