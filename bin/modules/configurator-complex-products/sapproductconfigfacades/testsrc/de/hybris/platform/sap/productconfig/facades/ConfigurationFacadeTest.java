/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class ConfigurationFacadeTest
{
	private final ConfigurationFacade classUnderTest = new ConfigurationFacade()
	{

		@Override
		public void updateConfiguration(final ConfigurationData configuration)
		{
			// nothing happens
		}

		@Override
		public int getNumberOfSolvableConflicts(final String configId)
		{
			return 0;
		}

		@Override
		public int getNumberOfIncompleteCstics(final String configId)
		{
			return 0;
		}

		@Override
		public int getNumberOfErrors(final String configId)
		{
			return 0;
		}

		@Override
		public ConfigurationData getConfiguration(final ConfigurationData configuration)
		{
			return null;
		}

		@Override
		public ConfigurationData getConfiguration(final KBKeyData kbKey)
		{
			return null;
		}
	};

	@Test(expected = IllegalStateException.class)
	public void testGetConfigurationFromTemplate()
	{
		classUnderTest.getConfigurationFromTemplate(null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testIsConfigurationAvailable()
	{
		classUnderTest.isConfigurationAvailable("CONFIG_ID");
	}

	@Test(expected = NotImplementedException.class)
	public void testGetConfiguration()
	{
		classUnderTest.getConfiguration(new KBKeyData(), false);
	}
}
