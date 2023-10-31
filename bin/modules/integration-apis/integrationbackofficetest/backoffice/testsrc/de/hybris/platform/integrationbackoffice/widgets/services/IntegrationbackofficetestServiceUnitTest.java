/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationbackoffice.services.IntegrationbackofficetestService;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

@UnitTest
public class IntegrationbackofficetestServiceUnitTest
{
	@Test
	public void testIntegrationbackofficetestServiceConstructor()
	{
		IntegrationbackofficetestService service = new IntegrationbackofficetestService();
		assertNotNull(service);
	}
}
