/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.jalo.CloudCPQOrderEntryProductInfo;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;


/**
 * Unit test for {@link DefaultCloudCPQOrderEntryProductInfoDaos}
 */
@UnitTest
public class DefaultCloudCPQOrderEntryProductInfoDaoTest
{
	private static final String CONFIG_ID_WITH_2_RESULTS = "c123";
	private static final String CONFIG_ID_WITH_NO_RESULTS = "c456";
	private static final String CONFIG_ID_WITH_1_RESULT = "c789";
	private static final int PAGE_SIZE = 5;
	private static final String MATCH_ANY = "--any--";

	private DefaultCloudCPQOrderEntryProductInfoDao classUnderTest;
	private ConfigurationServiceLayerHelper serviceHelper;

	@Mock
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private AbstractOrderEntryModel entryMock;


	private ArgumentMatcher<FlexibleSearchQuery> queryMatcher;
	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
	private ArgumentMatcher<PaginatedFlexibleSearchParameter> pageableQueryMatcher;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		serviceHelper = new DefaultConfigurationServiceLayerHelper(null, null);
		classUnderTest = new DefaultCloudCPQOrderEntryProductInfoDao(flexibleSearchService, paginatedFlexibleSearchService);

		given(entryMock.getPk()).willReturn(PK.parse("12345678"));

		final CloudCPQOrderEntryProductInfoModel cpqInfo = new CloudCPQOrderEntryProductInfoModel();
		cpqInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		final AbstractOrderEntryModel entryMockForSearch = Mockito.mock(AbstractOrderEntryModel.class);
		given(entryMockForSearch.getPk()).willReturn(PK.parse("12345678"));
		cpqInfo.setOrderEntry(entryMockForSearch);
		entry.setProductInfos(Collections.singletonList(cpqInfo));



		queryMatcher = matchQueryWithParams(DefaultCloudCPQOrderEntryProductInfoDao.GET_ALL_PRODUCT_INFOS_BY_CONFIG_ID,
				Collections.singletonMap(DefaultCloudCPQOrderEntryProductInfoDao.PARAM_NAME_CONFIG_ID, CONFIG_ID_WITH_2_RESULTS));
		mockNonUniqueResult(queryMatcher, 2);

		pageableQueryMatcher = matchPageableQueryWithParams(DefaultCloudCPQOrderEntryProductInfoDao.GET_ALL_PRODUCT_INFOS,
				Collections.emptyMap(), PAGE_SIZE, 0);
		mockPageableResult(pageableQueryMatcher, 3);

		queryMatcher = matchQueryWithParams(DefaultCloudCPQOrderEntryProductInfoDao.GET_ALL_PRODUCT_INFOS_BY_CONFIG_ID,
				Collections.singletonMap(DefaultCloudCPQOrderEntryProductInfoDao.PARAM_NAME_CONFIG_ID, CONFIG_ID_WITH_1_RESULT));
		mockGivenQuery(queryMatcher, cpqInfo);

		queryMatcher = matchQueryWithParams(DefaultCloudCPQOrderEntryProductInfoDao.GET_ALL_PRODUCT_INFOS_BY_CONFIG_ID,
				Collections.singletonMap(DefaultCloudCPQOrderEntryProductInfoDao.PARAM_NAME_CONFIG_ID, CONFIG_ID_WITH_NO_RESULTS));
		mockGivenQueryEmpty(queryMatcher);


	}

	@Test
	public void testGetAllConfigurationProductInfosById()
	{
		final List<CloudCPQOrderEntryProductInfoModel> allModels = classUnderTest
				.getAllConfigurationProductInfosById(CONFIG_ID_WITH_2_RESULTS);
		assertEquals(2, allModels.size());
	}

	@Test
	public void testGetAllConfigurationProductInfosByIdEmpty()
	{
		final List<CloudCPQOrderEntryProductInfoModel> allModels = classUnderTest
				.getAllConfigurationProductInfosById(CONFIG_ID_WITH_NO_RESULTS);
		assertEquals(0, allModels.size());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetAllConfigurationProductInfosByIdNullParam()
	{
		classUnderTest.getAllConfigurationProductInfosById(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsOnlyRelatedToGivenEntryNullParam()
	{
		classUnderTest.isOnlyRelatedToGivenEntry(null, CONFIG_ID_WITH_1_RESULT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsOnlyRelatedToGivenEntryNullParam2()
	{
		classUnderTest.isOnlyRelatedToGivenEntry(entry, null);
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryTrue()
	{
		assertTrue(classUnderTest.isOnlyRelatedToGivenEntry(entryMock, CONFIG_ID_WITH_1_RESULT));
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryFalseToMany()
	{
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(entry, CONFIG_ID_WITH_2_RESULTS));
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryFalsePKsDoNotMatch()
	{
		given(entryMock.getPk()).willReturn(PK.parse("567890"));
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(entryMock, CONFIG_ID_WITH_1_RESULT));
	}


	@Test
	public void testGetAllConfigurationProductInfos()
	{
		final SearchPageData<CloudCPQOrderEntryProductInfoModel> searchResult = classUnderTest
				.getAllConfigurationProductInfos(PAGE_SIZE, 0);
		assertNotNull(searchResult);
		assertEquals(3, searchResult.getResults().size());

	}

	@Test
	public void testCreatePageableQuery()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("dummy");
		final PaginatedFlexibleSearchParameter searchInput = classUnderTest.createPageableQuery(flexibleSearchQuery, PAGE_SIZE, 0);
		assertNotNull(searchInput);
		assertSame(flexibleSearchQuery, searchInput.getFlexibleSearchQuery());
		assertEquals(PAGE_SIZE, searchInput.getSearchPageData().getPagination().getPageSize());
	}



	protected ArgumentMatcher<FlexibleSearchQuery> matchQueryWithParams(final String queryString, final Map<String, String> params)
	{
		return new ArgumentMatcherFSQ<FlexibleSearchQuery>(queryString, params);
	}

	protected void mockGivenQuery(final ArgumentMatcher<FlexibleSearchQuery> queryMatcher, final Object model)
	{
		willReturn(model).given(flexibleSearchService).searchUnique(argThat(queryMatcher));
		willReturn(new SearchResultImpl<>(Collections.singletonList(model), 1, 1, 1)).given(flexibleSearchService)
				.search(argThat(queryMatcher));
	}

	protected void mockGivenQueryEmpty(final ArgumentMatcher<FlexibleSearchQuery> queryMatcher)
	{
		willReturn(null).given(flexibleSearchService).searchUnique(argThat(queryMatcher));
		willReturn(new SearchResultImpl<>(Collections.emptyList(), 0, 1, 1)).given(flexibleSearchService)
				.search(argThat(queryMatcher));
	}

	protected void mockNonUniqueResult(final ArgumentMatcher<FlexibleSearchQuery> queryMatcher, final int numResults)
	{
		final List<CloudCPQOrderEntryProductInfo> list = new ArrayList<>();
		for (int ii = 0; ii < numResults; ii++)
		{
			final CloudCPQOrderEntryProductInfo infoModel = new CloudCPQOrderEntryProductInfo();
			list.add(infoModel);
		}
		willReturn(new SearchResultImpl<>(list, numResults, numResults, numResults)).given(flexibleSearchService)
				.search(argThat(queryMatcher));
	}

	protected void mockPageableResult(final ArgumentMatcher<PaginatedFlexibleSearchParameter> queryMatcher, final int size)
	{
		final List<CloudCPQOrderEntryProductInfo> list = new ArrayList<>();
		for (int ii = 0; ii < size; ii++)
		{
			final CloudCPQOrderEntryProductInfo infoModel = new CloudCPQOrderEntryProductInfo();
			list.add(infoModel);
		}
		final SearchPageData<CloudCPQOrderEntryProductInfo> pageData = new SearchPageData<>();
		pageData.setResults(list);

		willReturn(pageData).given(paginatedFlexibleSearchService).search(argThat(queryMatcher));
	}

	protected ArgumentMatcher<PaginatedFlexibleSearchParameter> matchPageableQueryWithParams(final String queryString,
			final Map<String, String> params, final int pageSize, final int currentPage)
	{
		return new ArgumentMatcherPFSP<FlexibleSearchQuery>(queryString, params, pageSize, currentPage);
	}
	public class ArgumentMatcherFSQ<FlexibleSearchQuery> implements ArgumentMatcher{

		final String queryString;
		final Map<String, String> params;
		ArgumentMatcherFSQ(final String queryString, final Map<String, String> params)
		{
			this.queryString = queryString;
			this.params = params;
		}

		@Override
		public boolean matches(final Object argument)
		{
			final FlexibleSearchQuery query = (FlexibleSearchQuery) argument;
			boolean matches = queryString.equals(((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQuery()) && ((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQueryParameters().size() == params.size();
			for (final Entry<String, String> expectedParam : params.entrySet())
			{
				matches = matches && expectedParam.getValue().equals(((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQueryParameters().get(expectedParam.getKey()));
			}
			return matches;
		}
	}

	public class ArgumentMatcherPFSP<FlexibleSearchQuery> implements ArgumentMatcher{
		final String queryString;
		final Map<String,String> params;
		final int pageSize;
		final int currentPage;

		ArgumentMatcherPFSP( final String queryString, final Map<String, String> params,  final int pageSize,  final int currentPage){
			this.queryString = queryString;
			this.params = params;
			this.pageSize = pageSize;
			this.currentPage = currentPage;
		}
		@Override
		public boolean matches(final Object argument)
		{
			final PaginatedFlexibleSearchParameter pageableQuery = (PaginatedFlexibleSearchParameter) argument;
			final FlexibleSearchQuery query = (FlexibleSearchQuery) pageableQuery.getFlexibleSearchQuery();
			boolean matches = queryString.equals(((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQuery()) && ((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQueryParameters().size() == params.size();
			matches = matches && pageableQuery.getSearchPageData().getPagination().getCurrentPage() == currentPage;
			matches = matches && pageableQuery.getSearchPageData().getPagination().getPageSize() == pageSize;
			for (final Entry<String, String> expectedParam : params.entrySet())
			{
				if (MATCH_ANY.equals(expectedParam.getValue()))
				{
					matches = matches && ((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQueryParameters().get(expectedParam.getKey()) != null;
				}
				else
				{
					matches = matches && expectedParam.getValue().equals(((de.hybris.platform.servicelayer.search.FlexibleSearchQuery) query).getQueryParameters().get(expectedParam.getKey()));
				}
			}
			return matches;
		}
	}

}
