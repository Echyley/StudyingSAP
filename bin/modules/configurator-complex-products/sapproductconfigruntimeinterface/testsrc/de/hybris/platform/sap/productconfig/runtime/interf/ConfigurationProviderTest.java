/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


/**
 * Unit test for {@link ConfigurationProvider}
 */
@UnitTest
public class ConfigurationProviderTest
{

	private final ConfigurationProvider classUnderTest = new DummyConfigurationProvider();


	@Test(expected = NotImplementedException.class)
	public void testEnrichModelWithGroup() throws ConfigurationEngineException
	{
		classUnderTest.enrichModelWithGroup(new ConfigModelImpl(), "groupId");
	}

	@Test(expected = NotImplementedException.class)
	public void testRetrieveConfigurationOverview() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel("configId", "groupId", false, null);
	}

	@Test
	public void testIsGroupBasedConfigurationReadSupported()
	{
		assertFalse(classUnderTest.isReadDomainValuesOnDemandSupported());
	}

	@Test
	public void testIsKbVersionValid()
	{
		assertTrue(classUnderTest.isKbVersionValid(null));
	}

	@Test
	public void testIsKbVersionExists()
	{
		assertTrue(classUnderTest.isKbVersionExists(null));
	}

	@Test(expected = NotImplementedException.class)
	public void testExtractKbKey()
	{
		classUnderTest.extractKbKey("productCode", "externalConfig");
	}

	@Test(expected = NotImplementedException.class)
	public void testRetrieveConfigurationFromVariant()
	{
		classUnderTest.retrieveConfigurationFromVariant("baseProductCode", "variantProductCode");
	}

}
