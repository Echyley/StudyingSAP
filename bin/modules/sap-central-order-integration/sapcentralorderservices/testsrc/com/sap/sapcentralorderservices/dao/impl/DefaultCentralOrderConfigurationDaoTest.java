/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.sapcentralorderservices.model.SAPCentralOrderConfigurationModel;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderConfigurationDaoTest
{


	private final FlexibleSearchService flexibleSearchService = mock(FlexibleSearchService.class);


	private final SearchResult searchResult = mock(SearchResult.class);


	private final SAPCentralOrderConfigurationModel sapCentralOrderConfigurationModel = mock(
			SAPCentralOrderConfigurationModel.class);


	private final DefaultCentralOrderConfigurationDao centralOrderConfigurationDao = new DefaultCentralOrderConfigurationDao();

	private List resultList;

	@Before
	public void setUp()
	{
		centralOrderConfigurationDao.setFlexibleSearchService(flexibleSearchService);
		resultList = new ArrayList();
		resultList.add(sapCentralOrderConfigurationModel);

		//Mocks
		when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

	}



	@Test
	public void findSapCentralOrderConfigurationTest_ReturnsNonZeroResult()
	{
		when(searchResult.getTotalCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(resultList);
		assertNotNull(centralOrderConfigurationDao.findSapCentralOrderConfiguration());

	}

	@Test
	public void findSapCentralOrderConfigurationTest_ReturnsZeroResult()
	{
		boolean exceptionThrown = false;
		when(searchResult.getTotalCount()).thenReturn(0);
		try
		{
			centralOrderConfigurationDao.findSapCentralOrderConfiguration();
		}
		catch (final UnknownIdentifierException e)
		{
			exceptionThrown = true;
		}

		assertTrue(exceptionThrown);
	}

	@Test
	public void findSapCentralOrderConfigurationTest_ReturnsMultipleResults()
	{
		boolean exceptionThrown = false;
		when(searchResult.getTotalCount()).thenReturn(3);
		try
		{
			centralOrderConfigurationDao.findSapCentralOrderConfiguration();
		}
		catch (final AmbiguousIdentifierException e)
		{
			exceptionThrown = true;
		}

		assertTrue(exceptionThrown);
	}
}
