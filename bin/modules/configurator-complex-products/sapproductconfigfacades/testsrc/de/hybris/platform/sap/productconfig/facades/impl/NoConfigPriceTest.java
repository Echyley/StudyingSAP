/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceDataType;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link NoConfigPrice}
 */
@UnitTest
public class NoConfigPriceTest
{

	private NoConfigPrice classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new NoConfigPrice();
	}

	@Test
	public void testGetCurrencyIso()
	{
		assertTrue(classUnderTest.getCurrencyIso().isEmpty());
	}

	@Test
	public void testGetPriceType()
	{
		assertEquals(PriceDataType.BUY, classUnderTest.getPriceType());
	}

	@Test
	public void testGetValue()
	{
		assertEquals(BigDecimal.ZERO, classUnderTest.getValue());
	}

	@Test
	public void testGetFormattedValue()
	{
		assertEquals("-", classUnderTest.getFormattedValue());
	}

	@Test
	public void testGetMinQuantity()
	{
		assertEquals(Long.valueOf(0), classUnderTest.getMinQuantity());
	}

	@Test
	public void testGetMaxQuantity()
	{
		assertEquals(Long.valueOf(0), classUnderTest.getMaxQuantity());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCurrencyIsoImmutable()
	{
		classUnderTest.setCurrencyIso("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueImmutable()
	{
		classUnderTest.setValue(BigDecimal.ONE);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSetFormattedValueyImmutable()
	{
		classUnderTest.setFormattedValue("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinQuantityImmutable()
	{
		classUnderTest.setMinQuantity(Long.valueOf(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaxQuantityImmutable()
	{
		classUnderTest.setMaxQuantity(Long.valueOf(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPriceTypeImmutable()
	{
		classUnderTest.setPriceType(PriceDataType.FROM);
	}
}
