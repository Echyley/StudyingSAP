/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Provides access to product configuration intelligence information for analytics
 */
public interface AnalyticsProvider
{
	/**
	 * Provides AnalyticsDocument object, which contains popularity information for the given ConfigModel Retrieves if
	 * present current total price, base price and selected options price
	 *
	 * @param config
	 *           configuration model for which popularity should be retrieved
	 * @return analytics document
	 */
	AnalyticsDocument getPopularity(final ConfigModel config);

	/**
	 * Indicates whether the product configuration intelligence provider is active
	 *
	 * @return true if product configuration intelligence provider is active in the implementation
	 */
	boolean isActive();
}
