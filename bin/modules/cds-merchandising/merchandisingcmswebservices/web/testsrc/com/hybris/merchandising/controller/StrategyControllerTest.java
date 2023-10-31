/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.merchandising.dto.StrategyListWsDTO;
import com.hybris.merchandising.service.StrategyService;
import com.hybris.merchandising.service.impl.StrategyTest;


/**
 * StrategyControllerTest is a test class for the {@link StrategyController}.
 */
@UnitTest
public class StrategyControllerTest extends StrategyTest
{

	StrategyController controller;
	StrategyService mockStrategyService;

	@Before
	public void setUp()
	{
		controller = new StrategyController();
		mockStrategyService = Mockito.mock(StrategyService.class);
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(1), Integer.valueOf(10))).thenReturn(getMockStrategies(10));
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(2), Integer.valueOf(16))).thenReturn(getMockStrategies(16));
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(3), Integer.valueOf(6))).thenReturn(getMockStrategies(5));
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(1), Integer.valueOf(6))).thenReturn(List.of());
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(4), Integer.valueOf(6))).thenReturn(List.of());
		controller.strategyService = mockStrategyService;
	}

	@Test
	public void testGetStrategies()
	{
		final StrategyListWsDTO allStrategies = controller.getStrategies(null, null);
		for (int i = 0; i < 9; i++)
		{
			verifyDropDown(i, allStrategies.getOptions().get(i));
		}
		verifyPagination(allStrategies.getPagination(), 10, 0, true, false);
		Mockito.verify(mockStrategyService, Mockito.times(1)).getStrategies(1, 10);

		final StrategyListWsDTO pagedStrategies = controller.getStrategies(Integer.valueOf(1), Integer.valueOf(16));
		for (int i = 0; i < 15; i++)
		{
			verifyDropDown(i, pagedStrategies.getOptions().get(i));
		}
		verifyPagination(pagedStrategies.getPagination(), 16, 1, true, true);
		Mockito.verify(mockStrategyService, Mockito.times(1)).getStrategies(Integer.valueOf(2), Integer.valueOf(16));
	}


	@Test
	public void testPaginationForLastPage()
	{
		//given
		final Integer currentPage = 2;
		final Integer pageSize = 6;
		final Integer numberOfResult = 5;

		//when
		final StrategyListWsDTO strategies = controller.getStrategies(currentPage, pageSize);

		//then
		verifyPagination(strategies.getPagination(), numberOfResult, currentPage, false, true, Long.valueOf(currentPage * pageSize + numberOfResult));
	}

	@Test
	public void testPaginationWhenNoResults()
	{
		//given
		final Integer currentPage = 0;
		final Integer pageSize = 6;
		final Integer numberOfResult = 0;

		//when
		final StrategyListWsDTO strategies = controller.getStrategies(currentPage, pageSize);

		//then
		verifyPagination(strategies.getPagination(), numberOfResult, currentPage, false, false, numberOfResult.longValue());
	}

	@Test
	public void testPaginationWhenNoResultsForLastPage()
	{
		//given
		final Integer currentPage = 3;
		final Integer pageSize = 6;
		final Integer numberOfResult = 0;

		//when
		final StrategyListWsDTO strategies = controller.getStrategies(currentPage, pageSize);

		//then
		verifyPagination(strategies.getPagination(), numberOfResult, currentPage, false, true, Long.valueOf(currentPage * pageSize + numberOfResult));
	}

	protected void verifyPagination(final PaginationWsDTO result, final Integer count, final Integer page, final boolean hasNext, final boolean hasPrevious)
	{
		verifyPagination(result, count, page, hasNext, hasPrevious, null);
	}

	protected void verifyPagination(final PaginationWsDTO result, final Integer count, final Integer page, final boolean hasNext, final boolean hasPrevious,
	                                final Long totalCount)
	{
		assertNotNull("Pagination should not be null", result);
		assertEquals("Incorrect result count", count, result.getCount());
		assertEquals("Incorrect page", page, result.getPage());
		assertEquals("Incorrect hasNext", hasNext, result.getHasNext());
		assertEquals("Incorrect hasPrevious", hasPrevious, result.getHasPrevious());
		assertEquals("Incorrect total count", totalCount, result.getTotalCount());
	}
}
