/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.toggle.impl;

import de.hybris.platform.util.Config;

import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Product configuration feature logger class logs the current state of toggled features.
 */
public class ProductConfigFeatureLoggerImpl
{

	private static final Logger LOG = Logger.getLogger(ProductConfigFeatureLoggerImpl.class);
	protected static final String TOGGLE_PREFIX = "toggle.sapproductconfigservices";
	protected static final String NO_TOGGLES_CONFIGURED = "No toggles configured for configurator-complex-products";
	protected static final String TOGGLE_STATE = "configurator-complex-products toggle state:";

	public ProductConfigFeatureLoggerImpl()
	{
		// We want to log the current state of the toggled configurator-complex-products features only once during commerce start.
		// This class/bean will be instantiated at this point of time and the desired logging will be done.
		logFeatures();
	}

	private void logFeatures()
	{
		if (LOG.isInfoEnabled())
		{
			final Map<String, String> parameters = Config.getParametersByPattern(TOGGLE_PREFIX);
			if (parameters != null && parameters.size() > 0)
			{
				final StringBuilder toggleState = new StringBuilder("\n" + TOGGLE_STATE);
				Config.getParametersByPattern(TOGGLE_PREFIX).entrySet().stream()
						.forEach(e -> toggleState.append("\n").append(e.getKey()).append("=").append(e.getValue()));
				LOG.info(toggleState.toString());
			}
			else
			{
				LOG.info(NO_TOGGLES_CONFIGURED);
			}
		}
	}

}
