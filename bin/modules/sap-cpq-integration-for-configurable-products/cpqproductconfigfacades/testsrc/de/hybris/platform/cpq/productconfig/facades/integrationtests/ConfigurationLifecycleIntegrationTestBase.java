/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultB2BIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;

import javax.annotation.Resource;

import org.junit.Test;


public abstract class ConfigurationLifecycleIntegrationTestBase extends BaseIntegrationTest
{

	@Resource(name = "cpqProductConfigConfigurationService")
	protected ConfigurationService configurationService;

	@Override
	protected IntegrationTestConfiguration createTestConfig()
	{
		return new DefaultB2BIntegrationTestConfiguration();
	}

	@Test
	public void testBasicServerLifecycle()
	{

		// get server token (implicit) and send business context
		businessContextService.sendBusinessContextToCPQ();

		// init and save (preserve) configuration. This should use the client token out of the box, so that
		// the correct owner is set
		final String configId = configurationService.createConfiguration(P_CODE_CONF_LAPTOP);
		checkIsValidConfigId(configId);

		configurationService.makeConfigurationPermanent(configId);

		// read configuration summary
		final ConfigurationSummaryData summaryData = configurationService.getConfigurationSummary(configId);
		assertNotNull(summaryData);

		// delete configuration
		assertTrue(configurationService.deleteConfiguration(configId));

	}

}
