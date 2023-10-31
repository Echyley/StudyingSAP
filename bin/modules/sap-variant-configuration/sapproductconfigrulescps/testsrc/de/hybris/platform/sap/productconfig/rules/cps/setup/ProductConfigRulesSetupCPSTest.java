/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.setup;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class ProductConfigRulesSetupCPSTest
{
	private final ProductConfigRulesCPSSetup classUnderTest = new ProductConfigRulesCPSSetup();

	@Test
	public void testGetExtensionName()
	{
		assertEquals("sapproductconfigrulescps", classUnderTest.getExtensionName());
	}
}
