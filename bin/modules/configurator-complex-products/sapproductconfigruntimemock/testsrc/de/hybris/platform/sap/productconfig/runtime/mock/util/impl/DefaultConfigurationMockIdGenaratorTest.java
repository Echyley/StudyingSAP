/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.mock.data.ConfigurationId;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultConfigurationMockIdGenaratorTest
{
	private final DefaultConfigurationMockIdGenarator classUnderTest = new DefaultConfigurationMockIdGenarator();

	private static final String VARIANT_ID = "variantId";
	private static final String PRODUCT_ID = "productId";
	private static final String UUID = "uuid";

	@Test(expected = IllegalArgumentException.class)
	public void testGetStructuredConfigIdInvalid() throws ConfigurationEngineException
	{
		classUnderTest.getStructuredConfigIdFromString("invalid");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetStructuredConfigIdInvalidTooManyParts() throws ConfigurationEngineException
	{
		classUnderTest.getStructuredConfigIdFromString("1@2@3@4");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetStructuredConfigIdInvalidTooManyPartsStartingSep() throws ConfigurationEngineException
	{
		classUnderTest.getStructuredConfigIdFromString("@1@2@3");
	}

	@Test
	public void testGetStructuredConfigIdVariantIsMissing() throws ConfigurationEngineException
	{
		final ConfigurationId configurationId = classUnderTest.getStructuredConfigIdFromString("guid@product@");
		assertEquals("guid", configurationId.getUid());
		assertEquals("product", configurationId.getProductId());
	}

	@Test
	public void testGetStructuredConfigId() throws ConfigurationEngineException, JsonProcessingException
	{
		final ConfigurationId structuredConfigId = new ConfigurationId();
		structuredConfigId.setUid(UUID);
		structuredConfigId.setProductId(PRODUCT_ID);
		structuredConfigId.setVariantId(VARIANT_ID);
		final ConfigurationId result = classUnderTest
				.getStructuredConfigIdFromString(classUnderTest.getConfigIdFromStructured(structuredConfigId));
		assertNotNull(result);
		assertEquals(UUID, result.getUid());
		assertEquals(PRODUCT_ID, result.getProductId());
		assertEquals(VARIANT_ID, result.getVariantId());
	}

}
