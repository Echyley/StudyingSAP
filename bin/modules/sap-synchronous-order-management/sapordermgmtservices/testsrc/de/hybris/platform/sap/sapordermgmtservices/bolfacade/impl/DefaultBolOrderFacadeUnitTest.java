/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.Search;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultBolOrderFacadeUnitTest extends SapordermanagmentBolSpringJunitTest
{
	DefaultBolOrderFacade classUnderTest = new DefaultBolOrderFacade();


	@Before
	public void init()
	{
		classUnderTest.setGenericFactory(genericFactory);
	}


	@Test
	public void testGenericFactory()
	{
		assertNotNull(classUnderTest.getGenericFactory());
	}


	@Test
	public void testGetSearch()
	{
		assertNotNull(classUnderTest.getSearch());
	}

	@Test
	public void testGetSavedOrderBO()
	{
		assertNotNull(classUnderTest.getSavedOrder());
	}


	@Test
	public void testGetSearchResultsTotalNumber()
	{
		//no backend connected->we expect zero as number of total search results
		final Integer searchResultsTotalNumber = classUnderTest.getSearchResultsTotalNumber();
		assertEquals(new Integer(0), searchResultsTotalNumber);
	}


	@Test
	public void testGetSearchSort()
	{
		final List<SortData> sortData = classUnderTest.getSearchSort();
		assertNotNull(sortData);
		assertEquals(2, sortData.size());
	}

	@Test
	public void testSetSearchDirty()
	{
		final Search search = classUnderTest.getSearch();
		search.setDirty(false);
		classUnderTest.setSearchDirty();
		assertTrue(search.isDirty());
	}




}
