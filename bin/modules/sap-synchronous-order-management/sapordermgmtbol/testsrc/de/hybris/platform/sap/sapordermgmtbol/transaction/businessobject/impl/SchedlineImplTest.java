/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SchedlineImplTest extends SapordermanagmentBolSpringJunitTest
{

	private SchedlineImpl classUnderTest;
	private Date arbitraryCommittedDate;
	private BigDecimal arbitraryCommittedQuantity;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = (SchedlineImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SCHEDLINE);
	}

	@Test
	public void testGetterSetter()
	{
		prepareScheduleLineWithArbitraryData();

		assertEquals(arbitraryCommittedDate, classUnderTest.getCommittedDate());
		assertEquals(arbitraryCommittedQuantity, classUnderTest.getCommittedQuantity());
	}

	@Test
	public void testClone()
	{
		prepareScheduleLineWithArbitraryData();

		final SchedlineImpl clone = (SchedlineImpl) classUnderTest.clone();
		assertNotSame(clone, classUnderTest);
		assertEquals(arbitraryCommittedDate, clone.getCommittedDate());
		assertEquals(arbitraryCommittedQuantity, clone.getCommittedQuantity());


	}

	@Test
	public void testUnit()
	{
		final String unit = "ST";
		classUnderTest.setUnit(unit);
		assertEquals(unit, classUnderTest.getUnit());

	}

	/**
	 * 
	 */
	private void prepareScheduleLineWithArbitraryData()
	{
		arbitraryCommittedDate = new Date();
		classUnderTest.setCommittedDate(arbitraryCommittedDate);

		arbitraryCommittedQuantity = new BigDecimal(123);
		classUnderTest.setCommittedQuantity(arbitraryCommittedQuantity);
	}

}
