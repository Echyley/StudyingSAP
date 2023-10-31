/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class DefaultProductConfigIntegrationCloneAbstractOrderHookTest
{
	private static final int ITEMS_START = 10;
	private static final int ITEMS_INCREMENT = 10;

	private static final int ANOTHER_ITEMS_START = 100;
	private static final int ANOTHER_ITEMS_INCREMENT = 3;

	private DefaultProductConfigIntegrationCloneAbstractOrderHook classUnderTest;
	private final Map<AbstractOrderEntryModel, Integer> entryNumberMappings = new LinkedHashMap<>();
	private final AbstractOrderEntryModel orderEntry_1 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_2 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_3 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_4 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_5 = new AbstractOrderEntryModel();


	@Test
	public void testCheckCorrectEntryNumberShiftTrue()
	{
		setUpWithCorrectEntryNumbersIncrementation(ITEMS_START, ITEMS_INCREMENT);
		assertTrue("Entry number shift is correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftFalse()
	{
		setUpWithWrongEntryNumbersIncrementation(ITEMS_START, ITEMS_INCREMENT);
		assertFalse("Entry number shift is not correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftOriginal()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		assertFalse("Entry number shift is not correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftAndAnotherIncrementTrue()
	{
		setUpWithCorrectEntryNumbersIncrementationAndAnotherIncrement(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		assertTrue("Entry number shift is correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftWithEmptyOrNullValues()
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(ITEMS_START, ITEMS_INCREMENT);
		assertTrue("Entry number shift is correct as there are no entry numbers",
				classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftWithOnlyOneValueInValuesListFalse()
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(ITEMS_START, ITEMS_INCREMENT);
		orderEntry_1.setEntryNumber(0);
		entryNumberMappings.put(orderEntry_1, 0);

		assertFalse("Entry number shift is not correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}

	@Test
	public void testCheckCorrectEntryNumberShiftWithOnlyOneValueInValuesListTrue()
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(ITEMS_START, ITEMS_INCREMENT);
		orderEntry_1.setEntryNumber(10);
		entryNumberMappings.put(orderEntry_1, 10);

		assertTrue("Entry number shift is correct", classUnderTest.checkCorrectEntryNumberShift(entryNumberMappings.values()));
	}


	@Test
	public void testUpdateEntryNumbers()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		classUnderTest.updateEntryNumbers(entryNumberMappings);

		assertEquals("10", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("20", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("30", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("40", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("50", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testUpdateEntryNumbersPartiallyShifted()
	{
		setUpWithPartiallyShiftedEntryNumbers(ITEMS_START, ITEMS_INCREMENT);
		classUnderTest.updateEntryNumbers(entryNumberMappings);

		assertEquals("10", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("20", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("30", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("40", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("50", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testUpdateEntryNumbersWithAnotherItemsIncrement()
	{
		setUp(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		classUnderTest.updateEntryNumbers(entryNumberMappings);

		assertEquals("100", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("103", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("106", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("109", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("112", entryNumberMappings.get(orderEntry_5).toString());
	}


	@Test
	public void testAdjustEntryNumbers()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		classUnderTest.adjustEntryNumbers(entryNumberMappings);

		assertEquals("10", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("20", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("30", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("40", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("50", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testAdjustEntryNumbersWithAnotherItemsIncrement()
	{
		setUp(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		classUnderTest.adjustEntryNumbers(entryNumberMappings);

		assertEquals("100", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("103", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("106", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("109", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("112", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testAdjustEntryNumbersPartiallyShiftedAndWithAnotherItemsIncrement()
	{
		setUpWithPartiallyShiftedEntryNumbersAndAnotherIncrement(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		classUnderTest.adjustEntryNumbers(entryNumberMappings);

		assertEquals("100", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("103", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("106", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("109", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("112", entryNumberMappings.get(orderEntry_5).toString());
	}

	protected void setUp(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(0);
		orderEntry_2.setEntryNumber(1);
		orderEntry_3.setEntryNumber(2);
		orderEntry_4.setEntryNumber(3);
		orderEntry_5.setEntryNumber(4);


		entryNumberMappings.put(orderEntry_4, 3);
		entryNumberMappings.put(orderEntry_5, 4);
		entryNumberMappings.put(orderEntry_1, 0);
		entryNumberMappings.put(orderEntry_2, 1);
		entryNumberMappings.put(orderEntry_3, 2);
	}

	protected void setUpWithWrongEntryNumbersIncrementation(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(0);
		orderEntry_2.setEntryNumber(2);
		orderEntry_3.setEntryNumber(4);
		orderEntry_4.setEntryNumber(6);
		orderEntry_5.setEntryNumber(8);

		entryNumberMappings.put(orderEntry_1, 0);
		entryNumberMappings.put(orderEntry_5, 8);
		entryNumberMappings.put(orderEntry_2, 2);
		entryNumberMappings.put(orderEntry_3, 4);
		entryNumberMappings.put(orderEntry_4, 6);
	}

	protected void setUpWithPartiallyShiftedEntryNumbers(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_2.setEntryNumber(20);
		orderEntry_3.setEntryNumber(21);
		orderEntry_4.setEntryNumber(22);
		orderEntry_1.setEntryNumber(10);
		orderEntry_5.setEntryNumber(23);

		entryNumberMappings.put(orderEntry_1, 10);
		entryNumberMappings.put(orderEntry_5, 23);
		entryNumberMappings.put(orderEntry_2, 20);
		entryNumberMappings.put(orderEntry_3, 21);
		entryNumberMappings.put(orderEntry_4, 22);
	}

	protected void setUpWithPartiallyShiftedEntryNumbersAndAnotherIncrement(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_2.setEntryNumber(103);
		orderEntry_3.setEntryNumber(104);
		orderEntry_4.setEntryNumber(105);
		orderEntry_1.setEntryNumber(100);
		orderEntry_5.setEntryNumber(106);

		entryNumberMappings.put(orderEntry_1, 100);
		entryNumberMappings.put(orderEntry_5, 106);
		entryNumberMappings.put(orderEntry_2, 103);
		entryNumberMappings.put(orderEntry_3, 104);
		entryNumberMappings.put(orderEntry_4, 105);
	}

	protected void setUpWithCorrectEntryNumbersIncrementation(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(10);
		orderEntry_2.setEntryNumber(20);
		orderEntry_3.setEntryNumber(30);
		orderEntry_4.setEntryNumber(40);
		orderEntry_5.setEntryNumber(50);

		entryNumberMappings.put(orderEntry_2, 20);
		entryNumberMappings.put(orderEntry_3, 30);
		entryNumberMappings.put(orderEntry_5, 50);
		entryNumberMappings.put(orderEntry_4, 40);
		entryNumberMappings.put(orderEntry_1, 10);
	}

	protected void setUpWithCorrectEntryNumbersIncrementationAndAnotherIncrement(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(100);
		orderEntry_2.setEntryNumber(103);
		orderEntry_3.setEntryNumber(106);
		orderEntry_4.setEntryNumber(109);
		orderEntry_5.setEntryNumber(112);

		entryNumberMappings.put(orderEntry_1, 100);
		entryNumberMappings.put(orderEntry_2, 103);
		entryNumberMappings.put(orderEntry_5, 112);
		entryNumberMappings.put(orderEntry_3, 106);
		entryNumberMappings.put(orderEntry_4, 109);
	}

	protected void setUpWithWrongEntryNumbersIncrementationAndAnotherIncrement(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigIntegrationCloneAbstractOrderHook(itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(100);
		orderEntry_2.setEntryNumber(103);
		orderEntry_3.setEntryNumber(106);
		orderEntry_4.setEntryNumber(109);
		orderEntry_5.setEntryNumber(112);

		entryNumberMappings.put(orderEntry_1, 100);
		entryNumberMappings.put(orderEntry_4, 109);
		entryNumberMappings.put(orderEntry_5, 112);
		entryNumberMappings.put(orderEntry_2, 103);
		entryNumberMappings.put(orderEntry_3, 106);
	}


}