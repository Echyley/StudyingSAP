/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Default implementation of the analytics provider
 */
public class DefaultAnalyticsProviderImpl implements AnalyticsProvider
{
	@Override
	public AnalyticsDocument getPopularity(final ConfigModel config)
	{
		throw new UnsupportedOperationException(
				"Analytics is not supported by default but requires specific runtime implementation");
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

}
