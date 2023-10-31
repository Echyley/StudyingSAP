/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * JUnit test suite for {@link DefaultB2BUnitOrderDaoTest}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BUnitOrderDaoTest
{
	@InjectMocks
	private DefaultB2BUnitOrderDao defaultB2BUnitOrderDao;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private PageableData pageableData;
	@Mock
	private Set<B2BUnitModel> branchUnits;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private PagedFlexibleSearchService pagedFlexibleSearchService;
	@Captor
	private ArgumentCaptor<List<SortQueryData>> sortQueriesCaptor;
	@Captor
	private ArgumentCaptor<Map<String, String>> queryParamsCaptor;

	@Mock
	private SolrSearchQueryData solrSearchQueryData;

	private String TYPECODE = "TYPECODE";

	private static final String FIND_ORDERS_ADDITIONAL_FILTER_ENABLED = "commerceservices.find.orders.additional.filter.enabled";
	private static final String FIND_ORDERS_ADDITIONAL_FILTER = " AND {parent} IS NULL";

	private static final String BY_DATE = "byDate";
	private static final String BY_ORDER_NUMBER = "byOrderNumber";
	private static final String BY_ORG_UNIT = "byOrgUnit";
	private static final String BY_BUYER = "byBuyer";
	private static final String BY_ORG_UNIT_DESC = "byOrgUnitDesc";
	private static final String BY_BUYER_DESC = "byBuyerDesc";
	private static final OrderStatus STATUS1 = OrderStatus.valueOf("Status1");
	private static final OrderStatus STATUS2 = OrderStatus.valueOf("Status2");
	private static final String FILTER_STATUS_LIST = "filterStatusList";
	private static final String STORE = "store";
	private static final String BRANCH_UNITS = "branchUnits";
	private static final String UNIT_PARAM = "unit";
	private static final String BUYER_PARAM = "buyer";


	@Before
	public void setUp() throws Exception
	{
		defaultB2BUnitOrderDao.setConfigurationService(configurationService);
		defaultB2BUnitOrderDao.setPagedFlexibleSearchService(pagedFlexibleSearchService);
		when(configurationService.getConfiguration()).thenReturn(configuration);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindBranchOrdersByStoreNullStore()
	{
		defaultB2BUnitOrderDao.findBranchOrdersByStore(branchUnits, null, null, null, pageableData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindBranchOrdersByStoreNullPageable()
	{
		defaultB2BUnitOrderDao.findBranchOrdersByStore(branchUnits, baseStore, null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindBranchOrdersByStoreNullFilters()
	{
		defaultB2BUnitOrderDao.findBranchOrdersByStore(branchUnits, baseStore, null, null, null);
	}

	@Test
	public void testGetFindOrdersAdditionalFilterWhenAdditionalFilterIsDisabled()
	{
		when(configurationService.getConfiguration().getBoolean(FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false)).thenReturn(false);
		assertTrue("Additional filter is not disabled", defaultB2BUnitOrderDao.getFindOrdersAdditionalFilter().isEmpty());
	}

	@Test
	public void testGetFindOrdersAdditionalFilterWhenAdditionalFilterIsEnabled()
	{
		when(configurationService.getConfiguration().getBoolean(FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false)).thenReturn(true);
		assertEquals("Additional filter is not enabled", FIND_ORDERS_ADDITIONAL_FILTER,
				defaultB2BUnitOrderDao.getFindOrdersAdditionalFilter());
	}

	@Test(expected = ConversionException.class)
	public void testGetFindOrdersAdditionalFilterWhenAdditionalFilterConversionException()
	{
		when(configurationService.getConfiguration().getBoolean(FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false)).thenThrow(
				new ConversionException());
		defaultB2BUnitOrderDao.getFindOrdersAdditionalFilter();
	}

	@Test
	public void testFindBranchOrdersByStoreWithFilterOrderStatusListConfigured()
	{
		defaultB2BUnitOrderDao.setFilterOrderStatusList(List.of(STATUS1, STATUS2));
		when(configurationService.getConfiguration().getBoolean(FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false)).thenReturn(false);
		when(solrSearchQueryData.getFilterTerms()).thenReturn(List.of());
		defaultB2BUnitOrderDao.findBranchOrdersByStore(branchUnits, baseStore, solrSearchQueryData, null, pageableData);

		verify(pagedFlexibleSearchService).search(sortQueriesCaptor.capture(), any(), queryParamsCaptor.capture(),
				eq(pageableData));
		final List<SortQueryData> sortQueries = sortQueriesCaptor.getValue();
		final Set<String> sortCodes = sortQueries.stream().map(SortQueryData::getSortCode).collect(Collectors.toSet());
		assertEquals("Wrong size of SortQuery list", 6, sortQueries.size());
		assertEquals("SortQuery list should contain all sort codes",
				Set.of(BY_DATE, BY_ORG_UNIT, BY_BUYER, BY_ORDER_NUMBER, BY_ORG_UNIT_DESC, BY_BUYER_DESC), sortCodes);

		final Map<String, String> queryParams = queryParamsCaptor.getValue();
		assertTrue("Query parameters should contain excluded statuses", queryParams.containsKey(FILTER_STATUS_LIST));
		assertTrue("Query parameters should contain store", queryParams.containsKey(STORE));
		assertTrue("Query parameters should contain branchUnits", queryParams.containsKey(BRANCH_UNITS));

	}

	@Test
	public void testFindBranchOrdersByStoreWithFilterByUnitAndBuyer()
	{
		defaultB2BUnitOrderDao.setFilterOrderStatusList(List.of());
		when(configurationService.getConfiguration().getBoolean(FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false)).thenReturn(false);
		var unitTermData = new SolrSearchQueryTermData();
		unitTermData.setKey(UNIT_PARAM);
		unitTermData.setValue("testUnit");
		var buyerTermData = new SolrSearchQueryTermData();
		buyerTermData.setKey(BUYER_PARAM);
		buyerTermData.setValue("testBuyer");
		when(solrSearchQueryData.getFilterTerms()).thenReturn(List.of(unitTermData, buyerTermData));
		defaultB2BUnitOrderDao.findBranchOrdersByStore(branchUnits, baseStore, solrSearchQueryData, null, pageableData);

		verify(pagedFlexibleSearchService).search(sortQueriesCaptor.capture(), any(), queryParamsCaptor.capture(),
				eq(pageableData));
		final List<SortQueryData> sortQueries = sortQueriesCaptor.getValue();
		final Set<String> sortCodes = sortQueries.stream().map(SortQueryData::getSortCode).collect(Collectors.toSet());
		assertEquals("Wrong size of SortQuery list", 6, sortQueries.size());
		assertEquals("SortQuery list should contain all sort codes",
				Set.of(BY_DATE, BY_ORG_UNIT, BY_BUYER, BY_ORDER_NUMBER, BY_ORG_UNIT_DESC, BY_BUYER_DESC), sortCodes);

		final Map<String, String> queryParams = queryParamsCaptor.getValue();
		assertTrue("Query parameters should contain store", queryParams.containsKey(STORE));
		assertTrue("Query parameters should contain branchUnits", queryParams.containsKey(BRANCH_UNITS));
		assertTrue("Query parameters should contain unit", queryParams.containsKey(UNIT_PARAM));
		assertEquals("Query parameter value for unit should match", "%testUnit%".toLowerCase(), queryParams.get(UNIT_PARAM));
		assertTrue("Query parameters should contain buyer", queryParams.containsKey(BUYER_PARAM));
		assertEquals("Query parameter value for buyer should match", "%testBuyer%".toLowerCase(), queryParams.get(BUYER_PARAM));

	}
}
