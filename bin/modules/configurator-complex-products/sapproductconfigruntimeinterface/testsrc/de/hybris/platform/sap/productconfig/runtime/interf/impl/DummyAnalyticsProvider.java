/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


public class DummyAnalyticsProvider implements AnalyticsProvider
{

	@Override
	public AnalyticsDocument getPopularity(final ConfigModel config)
	{
		return new AnalyticsDocument();
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

}
