/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplatefacades.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link DefaultTextFieldFacade}
 */
@UnitTest
public class DefaultTextFieldFacadeTest
{
	private static final int ENTRY_NUMBER = 4;
	DefaultTextFieldFacade classUnderTest = new DefaultTextFieldFacade();
	private final AbstractOrderData abstractOrder = new AbstractOrderData();
	private final List<OrderEntryData> entries = new ArrayList<OrderEntryData>();
	private final OrderEntryData entry = new OrderEntryData();


	@Before
	public void initialize()
	{
		abstractOrder.setEntries(entries);
		entries.add(entry);
		entry.setEntryNumber(ENTRY_NUMBER);
	}

	@Test
	public void testGetAbstractOrderEntry() throws CommerceCartModificationException
	{
		final OrderEntryData abstractOrderEntry = classUnderTest.getAbstractOrderEntry(ENTRY_NUMBER, abstractOrder);
		assertNotNull(abstractOrderEntry);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testGetAbstractOrderEntryNoEntryFound() throws CommerceCartModificationException
	{
		entry.setEntryNumber(0);
		classUnderTest.getAbstractOrderEntry(ENTRY_NUMBER, abstractOrder);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testGetAbstractOrderEntryNullEntries() throws CommerceCartModificationException
	{
		abstractOrder.setEntries(null);
		classUnderTest.getAbstractOrderEntry(ENTRY_NUMBER, abstractOrder);
	}
}
