/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.handler.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationDependencyHandlerRulesCPSImplTest
{
	private static final String SOURCE_CONFIG_ID = "123";
	private static final String TARGET_CONFIG_ID = "456";

	ConfigurationDependencyHandlerRulesCPSImpl classUnderTest;

	@Mock
	private CharacteristicValueRulesResultHandler rulesResultHandler;


	@Before
	public void setUp() throws ConfigurationEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationDependencyHandlerRulesCPSImpl();
		classUnderTest.setRulesResultHandler(rulesResultHandler);
	}

	@Test
	public void testCopyProductConfigurationDependency()
	{
		classUnderTest.copyProductConfigurationDependency(SOURCE_CONFIG_ID, TARGET_CONFIG_ID);
		verify(rulesResultHandler).copyAndPersistRuleResults(SOURCE_CONFIG_ID, TARGET_CONFIG_ID);
	}
}
