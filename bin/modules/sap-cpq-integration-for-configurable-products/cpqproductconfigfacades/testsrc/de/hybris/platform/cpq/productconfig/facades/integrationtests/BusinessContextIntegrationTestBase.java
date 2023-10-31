/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.cpq.productconfig.services.data.BusinessContext;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.DefaultB2BIntegrationTestConfiguration;
import de.hybris.platform.cpq.productconfig.services.integrationtests.config.IntegrationTestConfiguration;

import org.junit.Test;


public abstract class BusinessContextIntegrationTestBase extends BaseIntegrationTest
{
	private static final String LANGUAGE = "en";
	private static final String CURRENCY = "EUR";
	private static final String DIVISION = "division";
	private static final String DISTCHANNEL = "dc";
	private static final String SALESORG = "salesorg";
	private static final String TESTUNIT = "TESTUNIT";

	@Override
	protected IntegrationTestConfiguration createTestConfig()
	{
		return new DefaultB2BIntegrationTestConfiguration();
	}

	@Test
	public void testSendBusinessContextToCPQ()
	{
		final String ownerId = businessContextService.getOwnerId();
		assertEquals(TESTUNIT, ownerId);
		final BusinessContext businessContext = businessContextService.getBusinessContext();
		assertEquals(SALESORG, businessContext.getSalesArea().getSalesOrganization());
		assertEquals(DISTCHANNEL, businessContext.getSalesArea().getDistributionChannel());
		assertEquals(DIVISION, businessContext.getSalesArea().getDivision());
		assertEquals(TESTUNIT, businessContext.getInvolvedParties().get(0).getExternalId());
		assertEquals(CURRENCY, businessContext.getCurrencyCode());
		assertEquals(LANGUAGE, businessContext.getLanguage());
		businessContextService.sendBusinessContextToCPQ(ownerId, businessContext);
	}

}
