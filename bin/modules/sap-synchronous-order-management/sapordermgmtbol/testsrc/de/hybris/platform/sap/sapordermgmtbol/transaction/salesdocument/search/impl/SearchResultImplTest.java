/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.util.Date;

import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class SearchResultImplTest
{
	private final SearchResultImpl classUnderTest = new SearchResultImpl();

	@Test
	public void testDocumentID()
	{
		final TechKey orderID = new TechKey("A");
		classUnderTest.setKey(orderID);
		assertEquals(orderID, classUnderTest.getKey());
	}

	@Test
	public void testPurchaseOrderNo()
	{
		final String poNr = "Hallo";
		classUnderTest.setPurchaseOrderNumber(poNr);
		assertEquals(poNr, classUnderTest.getPurchaseOrderNumber());
	}

	@Test
	public void testOverallStatus()
	{
		final String overallStatus = SearchResult.OVERALL_STATUS_OPEN;
		classUnderTest.setOverallStatus(overallStatus);
		assertEquals(overallStatus, classUnderTest.getOverallStatus());
	}

	@Test
	public void testShippingStatus()
	{
		final String shippingStatus = SearchResult.SHIPPING_NOT_SHIPPED;
		classUnderTest.setShippingStatus(shippingStatus);
		assertEquals(shippingStatus, classUnderTest.getShippingStatus());
	}

	@Test
	public void testCreationDate()
	{
		final Date date = new Date(System.currentTimeMillis());
		classUnderTest.setCreationDate(date);
	}
}
