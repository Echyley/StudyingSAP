/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigRulesResultUtilImplTest
{
	private static final String CONFIG_ID = "configId";
	private ProductConfigRulesResultUtilImpl classUnderTest;


	@Before
	public void setup()
	{
		classUnderTest = new ProductConfigRulesResultUtilImpl();
	}

	@Test
	public void testRetrieveRulesBasedVariantConditionModifications()
	{
		assertTrue(classUnderTest.retrieveRulesBasedVariantConditionModifications(CONFIG_ID).isEmpty());
	}

	@Test
	public void testRetrieveDiscountMessages()
	{
		assertTrue(classUnderTest.retrieveDiscountMessages(CONFIG_ID).isEmpty());
	}


}
