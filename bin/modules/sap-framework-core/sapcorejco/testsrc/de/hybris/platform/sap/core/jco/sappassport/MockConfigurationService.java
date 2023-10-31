/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import org.apache.commons.configuration.Configuration;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class MockConfigurationService implements ConfigurationService
{
	@Override
	public Configuration getConfiguration()
	{
		return null;
	}

}
