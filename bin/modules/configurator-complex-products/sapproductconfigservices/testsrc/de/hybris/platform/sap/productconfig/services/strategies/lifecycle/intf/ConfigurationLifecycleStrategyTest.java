/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Unit test for {@link ConfigurationLifecycleStrategy}
 */
@UnitTest
public class ConfigurationLifecycleStrategyTest
{

	private final ConfigurationLifecycleStrategy classUnderTest = new ConfigurationLifecycleStrategyStable();


	@Test(expected = IllegalStateException.class)
	public void testRetrieveConfigurationModel() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel("configId", "groupId", false, new ConfigurationRetrievalOptions());
	}


	private static class ConfigurationLifecycleStrategyStable implements ConfigurationLifecycleStrategy
	{

		@Override
		public ConfigModel createDefaultConfiguration(final KBKey kbKey)
		{
			return null;
		}

		@Override
		public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
		{
			return false;
		}

		@Override
		public void updateUserLinkToConfiguration(final String userSessionId)
		{
			// empty
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
				throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId)
		{
			// empty
		}

		@Override
		public void releaseExpiredSessions(final String userSessionId)
		{
			// empty
		}

		@Override
		public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
		{
			return null;
		}

		@Override
		public boolean isConfigForCurrentUser(final String configId)
		{
			return false;
		}

		@Override
		public boolean isConfigKnown(final String configId)
		{
			return false;
		}

	}

}
