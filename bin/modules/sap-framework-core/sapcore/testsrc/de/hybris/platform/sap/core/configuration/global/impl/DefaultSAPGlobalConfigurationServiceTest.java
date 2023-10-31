/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.global.impl;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * DefaultSAPGlobalConfigurationServicetest.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultSAPGlobalConfigurationServiceTest-spring.xml" })
public class DefaultSAPGlobalConfigurationServiceTest extends SapcoreSpringJUnitTest
{

	/**
	 * The class under test.
	 */
	@Resource(name = "testDefaultSAPGlobalConfigurationService")
	private DefaultSAPGlobalConfigurationService classUnderTest;

	@SuppressWarnings("javadoc")
	@Test
	public void testGetPropertyValue()
	{
		assertEquals("test Entry", classUnderTest.getProperty("sapsflight.global.test"));
	}

}
