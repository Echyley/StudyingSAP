/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.daos.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class AbstractFacetSearchConfigDAOTest
{
	static boolean isFacetSearchConfigModelCreated = false;
	static class AbstractFacetSearchConfigDAOImpl extends AbstractFacetSearchConfigDAO
	{
		@Override
		protected FlexibleSearchQuery getQuery4FindSearchCfgForTypes(final List types)
		{
			return null;
		}

		@Override
		protected FlexibleSearchQuery getQuery4FindSearchCfgForName(final String facetSearchConfigName)
		{
			return null;
		}

		@Override
		protected FlexibleSearchQuery getQuery4FindAllSearchCfg()
		{
			return null;
		}

		@Override
		protected boolean isFacetSearchConfigModelCreated()
		{
			return isFacetSearchConfigModelCreated;
		}

		public void setIsFacetSearchConfigModelCreated(final boolean isCreated)
		{
			isFacetSearchConfigModelCreated = isCreated;
		}
	}


	@Mock
	private FlexibleSearchService flexibleSearchService;


	@InjectMocks
	private AbstractFacetSearchConfigDAOImpl abstractFacetSearchConfigDAOImpl;

	@Test
	public void shouldReturnEmptyListWhenFacetSearchConfigModelNotCreated()
	{
		assertEquals(Collections.emptyList(), abstractFacetSearchConfigDAOImpl.findAllSearchConfigs());
	}

	@Test
	public void shouldSearchResultWhenFacetSearchConfigModelCreated()
	{
		final SearchResult searchResult = mock(SearchResult.class);
		doReturn(searchResult).when(flexibleSearchService).search(nullable(FlexibleSearchQuery.class));
		abstractFacetSearchConfigDAOImpl.setIsFacetSearchConfigModelCreated(true);
		assertEquals(searchResult.getResult(), abstractFacetSearchConfigDAOImpl.findAllSearchConfigs());
	}

}
