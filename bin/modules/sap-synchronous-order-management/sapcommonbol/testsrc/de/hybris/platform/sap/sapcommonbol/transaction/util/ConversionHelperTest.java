/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.transaction.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;


@UnitTest
@SuppressWarnings("javadoc")
public class ConversionHelperTest extends TestCase
{

	@Override
	public void setUp()
	{
		LocaleUtil.setLocale(Locale.ENGLISH);
	}

	@Test
	public void testAdjustCurrencyScale_5_2()
	{
		final BigDecimal value = BigDecimal.valueOf(500, 2);// 5.00

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("5.00", newValue.toPlainString());
	}

	@Test
	public void testAdjustCurrencyScale_5_3()
	{
		final BigDecimal value = BigDecimal.valueOf(500, 2);

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 3);

		assertEquals("0.500", newValue.toPlainString());
	}

	@Test
	public void testAdjustCurrencyScale_50_3()
	{
		final BigDecimal value = BigDecimal.valueOf(500, 1);// 50.0

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 3);

		assertEquals("0.500", newValue.toPlainString());

	}

	@Test
	public void testAdjustCurrencyScale_500_0()
	{
		final BigDecimal value = BigDecimal.valueOf(500, 0);// 500

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("5.00", newValue.toPlainString());

	}

	@Test
	public void testAdjustCurrencyScale_max()
	{
		final BigDecimal value = BigDecimal.valueOf(99999999999998888l, 2);// 999999999999988,88

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 4);

		assertEquals("9999999999999.8888", newValue.toPlainString());

	}

	@Test
	public void testOverflowProblem()
	{
		final BigDecimal value = BigDecimal.valueOf(3599964000l, 2);// 3599964000,00

		final BigDecimal newValue = ConversionHelper.adjustCurrencyDecimalPoint(value, 2);

		assertEquals("35999640.00", newValue.toPlainString());

	}

}
