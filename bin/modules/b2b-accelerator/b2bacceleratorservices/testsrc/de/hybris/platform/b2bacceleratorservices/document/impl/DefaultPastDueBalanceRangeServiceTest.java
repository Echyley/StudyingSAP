/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorservices.company.impl.DefaultPastDueBalanceDateRangeService;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.b2bacceleratorservices.document.NumberOfDayRange;
import de.hybris.platform.b2bacceleratorservices.company.PastDueBalanceDateRangeService;

@IntegrationTest
public class DefaultPastDueBalanceRangeServiceTest extends HybrisJUnit4Test
{


	@Before
	public void setUp()
	{
		Config.setParameter("accountsummary.daterange.1.start", "");
		Config.setParameter("accountsummary.daterange.1.end", "");

		Config.setParameter("accountsummary.daterange.2.start", "");
		Config.setParameter("accountsummary.daterange.2.end", "");

		Config.setParameter("accountsummary.daterange.3.start", "");
		Config.setParameter("accountsummary.daterange.3.end", "");
	}

	@Test
	public void shouldGetEmptyDaterangeList()
	{
		final PastDueBalanceDateRangeService srv = new DefaultPastDueBalanceDateRangeService();
		final List result = srv.getNumberOfDayRange();

		TestCase.assertEquals(0, result.size());
		TestCase.assertTrue(result.isEmpty());
	}

	@Test
	public void shouldGetEmptyDatarangeInvalidParameters()
	{
		Config.setParameter("accountsummary.daterange.1.start", "invalid");
		final PastDueBalanceDateRangeService srv = new DefaultPastDueBalanceDateRangeService();

		try
		{
			srv.getNumberOfDayRange();
			TestCase.fail();
		}
		catch (final NumberFormatException e)
		{
			// success
			TestCase.assertEquals("For input string: \"invalid\"", e.getMessage());
		}
	}

	@Test
	public void shouldGetOneDatarangeWithInfinite()
	{
		Config.setParameter("accountsummary.daterange.1.start", "1");
		final PastDueBalanceDateRangeService srv = new DefaultPastDueBalanceDateRangeService();
		final List<NumberOfDayRange> result = srv.getNumberOfDayRange();

		TestCase.assertEquals(1, result.size());
		TestCase.assertEquals(1, result.get(0).getMinBoundary().intValue());
		TestCase.assertNull(result.get(0).getMaxBoundary());
	}

	@Test
	public void shouldGetOneDatarange()
	{
		Config.setParameter("accountsummary.daterange.1.start", "1");
		Config.setParameter("accountsummary.daterange.1.end", "30");
		final PastDueBalanceDateRangeService srv = new DefaultPastDueBalanceDateRangeService();
		final List<NumberOfDayRange> result = srv.getNumberOfDayRange();

		TestCase.assertEquals(1, result.size());

		TestCase.assertEquals(1, result.get(0).getMinBoundary().intValue());
		TestCase.assertEquals(30, result.get(0).getMaxBoundary().intValue());
	}

	@Test
	public void shouldGetTwoDatarange()
	{
		Config.setParameter("accountsummary.daterange.1.start", "1");
		Config.setParameter("accountsummary.daterange.1.end", "30");

		Config.setParameter("accountsummary.daterange.2.start", "31");
		Config.setParameter("accountsummary.daterange.2.end", "60");

		final PastDueBalanceDateRangeService srv = new DefaultPastDueBalanceDateRangeService();
		final List<NumberOfDayRange> result = srv.getNumberOfDayRange();

		TestCase.assertEquals(2, result.size());

		TestCase.assertEquals(1, result.get(0).getMinBoundary().intValue());
		TestCase.assertEquals(30, result.get(0).getMaxBoundary().intValue());

		TestCase.assertEquals(31, result.get(1).getMinBoundary().intValue());
		TestCase.assertEquals(60, result.get(1).getMaxBoundary().intValue());
	}
}
