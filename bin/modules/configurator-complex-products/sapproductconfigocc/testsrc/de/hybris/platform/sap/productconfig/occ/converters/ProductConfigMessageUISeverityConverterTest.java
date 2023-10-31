/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigMessageUISeverityConverterTest
{

	private ProductConfigMessageUISeverityConverter classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigMessageUISeverityConverter();
	}

	@Test
	public void testConvertTo()
	{
		assertEquals(ProductConfigMessageUISeverity.CONFIG,
				classUnderTest.convertFrom(ProductConfigMessageUISeverity.CONFIG.toString(), null, null));
	}

	@Test
	public void testConvertFrom()
	{
		assertEquals(ProductConfigMessageUISeverity.CONFIG.toString(),
				classUnderTest.convertTo(ProductConfigMessageUISeverity.CONFIG, null, null));
	}
}
