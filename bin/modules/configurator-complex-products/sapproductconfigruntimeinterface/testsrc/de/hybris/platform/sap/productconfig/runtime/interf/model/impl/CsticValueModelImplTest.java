/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link CsticValueModelImpl}
 */
@UnitTest
public class CsticValueModelImplTest
{
	private static final String csticName = "Name";
	private static final String csticNameAnother = "AnotherName";
	private static final String csticNameNumeric = "2.0";
	private static final String csticNameNumericDifferentFormat = "2.00";
	private final CsticValueModelImpl classUnderTest = new CsticValueModelImpl();
	private final CsticValueModelImpl other = new CsticValueModelImpl();

	protected CsticValueModelImpl createNumericValue()
	{
		final CsticValueModelImpl csticNumericValueModelImpl = new CsticValueModelImpl();
		csticNumericValueModelImpl.setNumeric(true);
		return csticNumericValueModelImpl;
	}

	@Before
	public void initialze()
	{
		other.setNumeric(false);
		classUnderTest.setName(csticName);
	}

	private void checkValuesAreSame(final String valueFirst, final String valueSecond)
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName(valueFirst);
		final CsticValueModel value2 = createNumericValue();
		value2.setName(valueSecond);
		assertEquals(value1, value2);
	}

	@Test
	public void testEquals_smallWith0Fraction()
	{
		checkValuesAreSame("1", "1.0");
	}


	@Test
	public void testEquals_bigWith0Fraction()
	{
		checkValuesAreSame("12345678.0", "12345678");
	}

	@Test
	public void testEquals_bigWith00Fraction()
	{
		checkValuesAreSame("12345678.00", "12345678");
	}

	@Test
	public void testEquals_bigWithFractionAndExponential()
	{
		checkValuesAreSame("9999999999.99999", "9.99999999999999E9");
	}

	@Test
	public void testEquals_bigNegativeWithFractionAndExponential()
	{
		checkValuesAreSame("-9999999999.99999", "-9.99999999999999E9");
	}

	@Test
	public void testEquals_bigWithFractionAndBigExponential()
	{
		checkValuesAreSame("9000000000000000", "9E15");
	}

	@Test
	public void testEquals_bigWithExponential()
	{
		checkValuesAreSame("12345678", "1.2345678E7");
	}

	@Test
	public void testEquals_bigNegativeWithExponential()
	{
		checkValuesAreSame("-12343678", "-1.2343678E7");
	}

	@Test
	public void testNotEquals()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("123a43678");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("12343678");
		assertNotEquals(value1, value2);
	}

	@Test
	public void testNotEqualsNoNumericCstic()
	{
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName("01");
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName("1");
		assertNotEquals(value1, value2);
	}

	@Test
	public void testIsNumeric()
	{
		assertFalse(classUnderTest.isNumeric());
		classUnderTest.setNumeric(true);
		assertTrue(classUnderTest.isNumeric());
	}

	@Test
	public void testCompareName()
	{
		other.setName(csticName);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameDifferentName()
	{
		other.setName(csticNameAnother);
		assertFalse(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericWithNonNumeric()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameAnother);
		assertFalse(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericSameValue()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameNumeric);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericSameValueDifferentFormats()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameNumericDifferentFormat);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testGetMessageListNotNull()
	{
		assertNotNull(classUnderTest.getMessages());
	}

	@Test
	public void testSetGetMessageList()
	{
		final Set<ProductConfigMessage> messages = new HashSet();
		classUnderTest.setMessages(messages);
		assertEquals(messages, classUnderTest.getMessages());
	}

	@Test
	public void testGetSetLongText()
	{
		classUnderTest.setLongText("test123");
		assertEquals("test123", classUnderTest.getLongText());
	}
}
