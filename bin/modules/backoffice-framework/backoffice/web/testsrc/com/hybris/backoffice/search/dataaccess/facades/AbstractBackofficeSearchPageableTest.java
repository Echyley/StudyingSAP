/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.dataaccess.facades;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doCallRealMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.SortData;


@RunWith(MockitoJUnitRunner.class)
public class AbstractBackofficeSearchPageableTest
{
	static class AbstractBackofficeSearchPageableImpl extends AbstractBackofficeSearchPageable {
		public AbstractBackofficeSearchPageableImpl(final SearchQueryData searchQueryData) {
			super(searchQueryData);
		}

		protected List<String> getResults(final int pageSize, final int offset) {
			return new ArrayList<>();
		}
	}

	@Mock
	private AbstractBackofficeSearchPageable abstractBackofficeSearchPageable;

	@Test
	public void shouldReturnCurrentPage()
	{
		final List<String> currentPageCache = new ArrayList<>(){{
			add("test1");
			add("test2");
		}};
		abstractBackofficeSearchPageable.currentPageCache = currentPageCache;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getCurrentPage();
		final List<String> currentPage= abstractBackofficeSearchPageable.getCurrentPage();
		assertEquals(currentPageCache, currentPage);
	}

	@Test
	public void shouldReturnCurrentEmptyPage()
	{
		abstractBackofficeSearchPageable.currentPageCache = null;
		doReturn(Optional.empty()).when(abstractBackofficeSearchPageable).getCurrentNotEmptyPage();
		doCallRealMethod().when(abstractBackofficeSearchPageable).getCurrentPage();
		final List<String> currentPage= abstractBackofficeSearchPageable.getCurrentPage();
		assertEquals(new ArrayList<String>(), currentPage);
	}

	@Test
	public void shouldReturnNotEmptyResult()
	{
		final List<String> notEmptyResult = new ArrayList<>(){{
			add("test1");
			add("test2");
		}};
		when(abstractBackofficeSearchPageable.getResults(10, 1)).thenReturn(notEmptyResult);
		abstractBackofficeSearchPageable.currentStart = 1;
		abstractBackofficeSearchPageable.pageSize = 10;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getCurrentNotEmptyPage();
		final Optional<List<String>> result= abstractBackofficeSearchPageable.getCurrentNotEmptyPage();
		assertEquals(Optional.of(notEmptyResult), result);
	}

	@Test
	public void shouldReturnEmptyResult()
	{
		final List<String> emptyResult = new ArrayList<>();
		doReturn(emptyResult).when(abstractBackofficeSearchPageable).getResults(10, 1);
		abstractBackofficeSearchPageable.currentStart = 1;
		abstractBackofficeSearchPageable.pageSize = 10;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getCurrentNotEmptyPage();
		final Optional<List<String>> result= abstractBackofficeSearchPageable.getCurrentNotEmptyPage();
		assertEquals(Optional.empty(), result);
	}

	@Test
	public void shouldReturnEmptyResultWhenCurrentStartLessThan1()
	{
		abstractBackofficeSearchPageable.currentStart = 0;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getCurrentNotEmptyPage();
		final Optional<List<String>> result= abstractBackofficeSearchPageable.getCurrentNotEmptyPage();
		assertEquals(Optional.empty(), result);
	}

	@Test
	public void shouldRefresh()
	{
		abstractBackofficeSearchPageable.currentStart = 0;
		doCallRealMethod().when(abstractBackofficeSearchPageable).refresh();
		abstractBackofficeSearchPageable.refresh();
		assertFalse(abstractBackofficeSearchPageable.initialized);
	}

	@Test
	public void shouldReturnFalseWhenDoesNotHasNextPage()
	{
		abstractBackofficeSearchPageable.pageSize = 0;
		doCallRealMethod().when(abstractBackofficeSearchPageable).hasNextPage();
		assertFalse(abstractBackofficeSearchPageable.hasNextPage());
	}

	@Test
	public void shouldReturnTrueWhenHasNextPage()
	{
		abstractBackofficeSearchPageable.currentStart = 1;
		abstractBackofficeSearchPageable.pageSize = 2;
		abstractBackofficeSearchPageable.totalCount = 4;
		doCallRealMethod().when(abstractBackofficeSearchPageable).hasNextPage();
		assertTrue(abstractBackofficeSearchPageable.hasNextPage());
	}

	@Test
	public void shouldReturnCurrentPageWhenHasNextPage()
	{
		abstractBackofficeSearchPageable.currentStart = 1;
		abstractBackofficeSearchPageable.pageSize = 2;
		doReturn(true).when(abstractBackofficeSearchPageable).hasNextPage();
		doCallRealMethod().when(abstractBackofficeSearchPageable).nextPage();
		abstractBackofficeSearchPageable.nextPage();
		verify(abstractBackofficeSearchPageable).getCurrentPage();
		assertEquals(3, abstractBackofficeSearchPageable.currentStart);
	}

	@Test
	public void shouldReturnEmptyListWhenDoesNotHasNextPage()
	{
		doReturn(false).when(abstractBackofficeSearchPageable).hasNextPage();
		doCallRealMethod().when(abstractBackofficeSearchPageable).nextPage();
		final List<String> result= abstractBackofficeSearchPageable.nextPage();
		assertEquals(Collections.emptyList(), result);
	}

	@Test
	public void shouldReturnFalseWhenDoesNotHasPreviousPage()
	{
		abstractBackofficeSearchPageable.currentStart = 0;
		doCallRealMethod().when(abstractBackofficeSearchPageable).hasPreviousPage();
		assertFalse(abstractBackofficeSearchPageable.hasPreviousPage());
	}

	@Test
	public void shouldReturnTrueWhenHasPreviousPage()
	{
		abstractBackofficeSearchPageable.currentStart = 1;
		doCallRealMethod().when(abstractBackofficeSearchPageable).hasPreviousPage();
		assertTrue(abstractBackofficeSearchPageable.hasPreviousPage());
	}

	@Test
	public void shouldReturnEmptyListWhenDoesNotHasPreviousPage()
	{
		doReturn(false).when(abstractBackofficeSearchPageable).hasPreviousPage();
		doCallRealMethod().when(abstractBackofficeSearchPageable).previousPage();
		final List<String> result= abstractBackofficeSearchPageable.previousPage();
		assertEquals(Collections.emptyList(), result);
	}

	@Test
	public void shouldReturnCurrentPageWhenHasPreviousPage()
	{
		abstractBackofficeSearchPageable.currentStart = 1;
		abstractBackofficeSearchPageable.pageSize = 2;
		doReturn(true).when(abstractBackofficeSearchPageable).hasPreviousPage();
		doCallRealMethod().when(abstractBackofficeSearchPageable).previousPage();
		abstractBackofficeSearchPageable.previousPage();
		verify(abstractBackofficeSearchPageable).getCurrentPage();
		assertEquals(0, abstractBackofficeSearchPageable.currentStart);
	}

	@Test
	public void shouldSetPageSizeWhenPageSizeChanged()
	{
		final int pageSize = 10;
		abstractBackofficeSearchPageable.pageSize = pageSize;
		doCallRealMethod().when(abstractBackofficeSearchPageable).setPageSize(pageSize);
		abstractBackofficeSearchPageable.setPageSize(pageSize);
		verify(abstractBackofficeSearchPageable).getCurrentPage();
		assertEquals(abstractBackofficeSearchPageable.pageSize, pageSize);
	}

	@Test
	public void shouldNotSetPageSizeWhenPageSizeNotChanged()
	{
		final int pageSize = 2;
		abstractBackofficeSearchPageable.pageSize = pageSize;
		doCallRealMethod().when(abstractBackofficeSearchPageable).setPageSize(pageSize);
		abstractBackofficeSearchPageable.setPageSize(pageSize);
		verify(abstractBackofficeSearchPageable).getCurrentPage();
		assertEquals(abstractBackofficeSearchPageable.pageSize, pageSize);
	}

	@Test
	public void shouldReturnTotalCount()
	{
		final int totalCount = 10;
		abstractBackofficeSearchPageable.totalCount = totalCount;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getTotalCount();
		assertEquals(totalCount, abstractBackofficeSearchPageable.getTotalCount());
	}

	@Test
	public void shouldReturnPageNumber()
	{
		final int currentStart = 1;
		abstractBackofficeSearchPageable.currentStart = currentStart;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getPageNumber();
		assertEquals(currentStart, abstractBackofficeSearchPageable.getPageNumber());
	}

	@Test
	public void shouldSetPageNumberWhenPageNumberChanged()
	{
		abstractBackofficeSearchPageable.currentStart = 1;
		doCallRealMethod().when(abstractBackofficeSearchPageable).setPageNumber(2);
		abstractBackofficeSearchPageable.setPageNumber(2);
		assertEquals(2, abstractBackofficeSearchPageable.currentStart);
	}

	@Test
	public void shouldReturnSortData()
	{	
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final AbstractBackofficeSearchPageableImpl abstractBackofficeSearchPageableImpl = new AbstractBackofficeSearchPageableImpl(searchQueryData);
		abstractBackofficeSearchPageableImpl.getSortData();
		verify(abstractBackofficeSearchPageableImpl.searchQueryData).getSortData();
	}

	@Test
	public void shouldSetSortData()
	{	
		final SortData sortData = mock(SortData.class);
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final AbstractBackofficeSearchPageableImpl abstractBackofficeSearchPageableImpl = new AbstractBackofficeSearchPageableImpl(searchQueryData);
		abstractBackofficeSearchPageableImpl.setSortData(sortData);
		verify(abstractBackofficeSearchPageableImpl.searchQueryData).setSortData(sortData);
	}

	@Test
	public void shouldReturnAllResults()
	{
		final int totalCount = 10;
		abstractBackofficeSearchPageable.totalCount = totalCount;
		doCallRealMethod().when(abstractBackofficeSearchPageable).getTotalCount();
		doCallRealMethod().when(abstractBackofficeSearchPageable).getAllResults();
		abstractBackofficeSearchPageable.getAllResults();
		verify(abstractBackofficeSearchPageable).getResults(totalCount, 0);
		verify(abstractBackofficeSearchPageable).getTotalCount();
	}

}
