/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultB2BIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;

import org.junit.Test;


public abstract class ConfigurationServiceIntegrationTestBase extends BaseIntegrationTest
{

	@Override
	protected IntegrationTestConfiguration createTestConfig()
	{
		return new DefaultB2BIntegrationTestConfiguration();
	}

	@Test
	public void testCreateConfiguration()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		checkIsValidConfigId(configId);
	}

	@Test
	public void testGetSummary()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		assertNotNull(configurationService.getConfigurationSummary(configId));
	}

	@Test
	public void testDelete()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		assertTrue(configurationService.deleteConfiguration(configId));
	}

	@Test
	public void testDeleteMultipleCalls()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		configurationService.deleteConfiguration(configId);
		assertFalse(configurationService.deleteConfiguration(configId));
	}

	@Test
	public void testClone()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		configurationService.makeConfigurationPermanent(configId);
		assertNotEquals(configId, configurationService.cloneConfiguration(configId, true));
	}


	@Test
	public void makePermanent()
	{
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		configurationService.makeConfigurationPermanent(configId);
		configurationService.makeConfigurationPermanent(configId); // should not harm if executed twice
		assertTrue(configurationService.deleteConfiguration(configId));
	}
}
