/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigMessagePromoTypeConverterTest
{

	private ProductConfigMessagePromoTypeConverter classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigMessagePromoTypeConverter();
	}

	@Test
	public void testConvertTo()
	{
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED,
				classUnderTest.convertFrom(ProductConfigMessagePromoType.PROMO_APPLIED.toString(), null, null));
	}

	@Test
	public void testConvertFrom()
	{
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED.toString(),
				classUnderTest.convertTo(ProductConfigMessagePromoType.PROMO_APPLIED, null, null));
	}
}
