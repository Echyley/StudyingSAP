/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.searchservices.constants.BackofficesearchservicesConstants;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.searchservices.enums.SnFieldType;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetFilter;
import de.hybris.platform.searchservices.search.data.SnFilter;
import de.hybris.platform.searchservices.search.data.SnMatchQuery;
import de.hybris.platform.searchservices.search.data.SnMatchTermsQuery;
import de.hybris.platform.searchservices.search.data.SnMatchType;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnAndQuery;
import de.hybris.platform.servicelayer.type.TypeService;


import java.util.*;

import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryConditionList;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class SearchQueryFiltersPopulatorTest
{

	@Mock
	private BackofficeFacetSearchConfigService facetSearchConfigService;

	@Mock
	private TypeService typeService;

	@Mock
	private CatalogTypeService catalogTypeService;

	@Mock
	private UserService userService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@InjectMocks
	private SearchQueryFiltersPopulator searchQueryFiltersPopulator;

	private static final String SEARCH_TYPE = "searchType";
	private static final String ATTR_NAME = "attribute_name";
	private static final String QUERY_VALUE = "condition_value";
	private static final String QUERY_TEXT = "query_text";
	private static final String FACET_A = "facet_a";
	private static final String FACET_B = "facet_b";
	private static final int PAGE_SIZE = 10;
	private static final int OFFSET = 1;
	private final SearchQueryData searchQueryData = mock(SearchQueryData.class);
	private final SearchQueryConditionList searchQueryConditionList = mock(SearchQueryConditionList.class);
	private final SnIndexTypeModel snIndexTypeModel = mock(SnIndexTypeModel.class);
	private final SnFieldModel snFieldModel = mock(SnFieldModel.class);
	private Set<SearchQueryCondition> searchQueryConditions = new HashSet<>();
	private SnSearchQuery snSearchQuery = new SnSearchQuery();

	@Before
	public void setup()
	{
		final HashSet<String> item1Set = Sets.newHashSet("a1", "a2");
		final HashSet<String> item2Set = Sets.newHashSet("b1", "b2");
		final Map<String, Set<String>> selectedFacets = new HashMap<>();
		selectedFacets.put(FACET_A, item1Set);
		selectedFacets.put(FACET_B, item2Set);

		snSearchQuery.setQuery(QUERY_TEXT);
		snSearchQuery.setLimit(PAGE_SIZE);
		snSearchQuery.setOffset(OFFSET);

		when(snFieldModel.getId()).thenReturn(ATTR_NAME);
		when(snFieldModel.getFieldType()).thenReturn(SnFieldType.TEXT);
		when(snIndexTypeModel.getFields()).thenReturn(Lists.newArrayList(snFieldModel));
		when(facetSearchConfigService.getIndexedTypeModel(SEARCH_TYPE)).thenReturn(snIndexTypeModel);

		when(searchQueryData.getSelectedFacets()).thenReturn(selectedFacets);
		when(searchQueryData.getSearchType()).thenReturn(SEARCH_TYPE);
	}

	@Test
	public void shouldDoNothingWhenSearchQueryDataIsNull()
	{
		final SnSearchQueryConverterData snSearchQueryConverterData = mock(SnSearchQueryConverterData.class);
		final SnSearchQuery snSearchQuery = new SnSearchQuery();

		when(snSearchQueryConverterData.getSearchQueryData()).thenReturn(null);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getQuery()).isNull();
		assertThat(snSearchQuery.getFilters()).isEmpty();
		assertThat(snSearchQuery.getFacetFilters()).isEmpty();
	}

	@Test
	public void shouldNotAddFiltersWhenGotSearchQueryConditionsWithoutMatchedFieldModel()
	{
		final List conditionsList = Lists.newArrayList(searchQueryConditions);
		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);

		when(searchQueryData.getConditions()).thenReturn(conditionsList);
		when(snFieldModel.getId()).thenReturn(ATTR_NAME);

		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<SnFilter> snFilters = snSearchQuery.getFilters();
		assertTrue(snFilters.isEmpty());
	}

	@Test
	public void shouldAddFacetFiltersWhenGotSearchQueryConditionsWithMatchedFieldModel()
	{
		searchQueryConditions = Sets.newHashSet(prepareCondition(ATTR_NAME, QUERY_VALUE, ValueComparisonOperator.EQUALS));
		when(searchQueryData.getConditions()).thenReturn((List) Lists.newArrayList(searchQueryConditions));

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<AbstractSnFacetFilter> facetFilters = snSearchQuery.getFacetFilters();
		assertThat(facetFilters).hasSize(2);
		assertThat(facetFilters.get(0).getFacetId()).isEqualTo(FACET_A);
		assertThat(facetFilters.get(1).getFacetId()).isEqualTo(FACET_B);
	}

	@Test
	public void shouldAddFiltersWhenGotSearchQueryConditionsWithMatchedFieldModel()
	{
		searchQueryConditions = Sets.newHashSet(prepareCondition(ATTR_NAME, QUERY_VALUE, ValueComparisonOperator.EQUALS));
		when(searchQueryData.getConditions()).thenReturn((List) Lists.newArrayList(searchQueryConditions));

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<SnFilter> snFilters = snSearchQuery.getFilters();
		assertThat(snFilters).hasSize(1);
		final SnFilter snFilter = snFilters.get(0);
		assertTrue(snFilter.getQuery() instanceof SnMatchQuery);
		final SnMatchQuery snMatchQuery = (SnMatchQuery) snFilter.getQuery();
		assertThat(snMatchQuery.getValue()).isEqualTo(QUERY_VALUE);
		assertThat(snMatchQuery.getExpression()).isEqualTo(ATTR_NAME);
	}

	@Test
	public void shouldNotAddFiltersWhenGotSearchQueryConditionsWithPk()
	{
		searchQueryConditions = Sets.newHashSet(prepareCondition(SnIndexTypeModel.PK, QUERY_VALUE, ValueComparisonOperator.CONTAINS));
		when(searchQueryData.getConditions()).thenReturn((List) Lists.newArrayList(searchQueryConditions));

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<SnFilter> snFilters = snSearchQuery.getFilters();
		assertTrue(snFilters.isEmpty());
	}

	@Test
	public void shouldAddFiltersWhenGotSearchQueryConditionListWhichContainsMatchedSearchQueryCondition()
	{
		searchQueryConditions = Sets.newHashSet(prepareCondition(ATTR_NAME, QUERY_VALUE, ValueComparisonOperator.EQUALS));
		when(searchQueryConditionList.getConditions()).thenReturn(Lists.newArrayList(searchQueryConditions));
		when(searchQueryConditionList.getOperator()).thenReturn(ValueComparisonOperator.AND);
		when(searchQueryData.getConditions()).thenReturn((List) Lists.newArrayList(searchQueryConditionList));

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<SnFilter> snFilters = snSearchQuery.getFilters();
		assertThat(snFilters).hasSize(1);
		final SnFilter snFilter = snFilters.get(0);
		assertTrue(snFilter.getQuery() instanceof SnAndQuery);
		final SnAndQuery andQuery = (SnAndQuery) snFilter.getQuery();
		final List<SnMatchQuery> queries = (List) andQuery.getQueries();
		assertThat(queries).hasSize(1);
		final SnMatchQuery snMatchQuery = queries.get(0);
		assertThat(snMatchQuery.getValue()).isEqualTo(QUERY_VALUE);
		assertThat(snMatchQuery.getExpression()).isEqualTo(ATTR_NAME);
	}

	@Test
	public void shouldNotAddCatalogVersionFiltersWhenLoginUserIsAdmin()
	{
		final SnFieldModel snCVField = mock(SnFieldModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final UserModel currentUser = mock(UserModel.class);
		when(snIndexTypeModel.getFields()).thenReturn(Lists.newArrayList(snCVField));
		when(snCVField.getId()).thenReturn(BackofficesearchservicesConstants.INDEX_PROPERTY_CATALOG_VERSION_PK);
		when(typeService.getComposedTypeForCode(SEARCH_TYPE)).thenReturn(composedType);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.lenient().when(userService.isAdmin(currentUser)).thenReturn(true);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getFilters()).isEmpty();
	}

	@Test
	public void shouldNotAddCatalogVersionFiltersWhenNoCatalogVersionEixsted()
	{
		final SnFieldModel snCVField = mock(SnFieldModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final UserModel currentUser = mock(UserModel.class);
		when(snIndexTypeModel.getFields()).thenReturn(Lists.newArrayList(snCVField));
		when(snCVField.getId()).thenReturn(BackofficesearchservicesConstants.INDEX_PROPERTY_CATALOG_VERSION_PK);
		when(typeService.getComposedTypeForCode(SEARCH_TYPE)).thenReturn(composedType);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.lenient().when(userService.isAdmin(currentUser)).thenReturn(false);
		Mockito.lenient().when(catalogVersionService.getAllReadableCatalogVersions(currentUser)).thenReturn(CollectionUtils.EMPTY_COLLECTION);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getFilters()).isEmpty();
	}

	@Test
	public void shouldNotAddCatalogVersionFiltersWhenSearchTypeIsNotCatalogVersionAwareType()
	{
		final SnFieldModel snCVField = mock(SnFieldModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final UserModel currentUser = mock(UserModel.class);
		when(snIndexTypeModel.getFields()).thenReturn(Lists.newArrayList(snCVField));
		when(snCVField.getId()).thenReturn(BackofficesearchservicesConstants.INDEX_PROPERTY_CATALOG_VERSION_PK);
		when(typeService.getComposedTypeForCode(SEARCH_TYPE)).thenReturn(composedType);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.lenient().when(userService.isAdmin(currentUser)).thenReturn(true);
		when(catalogTypeService.isCatalogVersionAwareType(composedType)).thenReturn(false);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getFilters()).isEmpty();
	}


	@Test
	public void shouldAddCatalogVersionFilters()
	{
		final SnFieldModel snCVField = mock(SnFieldModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final UserModel currentUser = mock(UserModel.class);
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final PK pk = PK.BIG_PK;

		when(snIndexTypeModel.getFields()).thenReturn(Lists.newArrayList(snCVField));
		when(snCVField.getId()).thenReturn(BackofficesearchservicesConstants.INDEX_PROPERTY_CATALOG_VERSION_PK);
		when(typeService.getComposedTypeForCode(SEARCH_TYPE)).thenReturn(composedType);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(userService.isAdmin(currentUser)).thenReturn(false);
		when(catalogTypeService.isCatalogVersionAwareType(composedType)).thenReturn(true);
		when(catalogVersionService.getAllReadableCatalogVersions(currentUser)).thenReturn(Lists.newArrayList(catalogVersion));
		when(catalogVersion.getPk()).thenReturn(pk);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		searchQueryFiltersPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final List<SnFilter> snFilters = snSearchQuery.getFilters();
		assertThat(snFilters).hasSize(1);
		final SnFilter snFilter = snFilters.get(0);
		assertThat(snFilter.getQuery()).isInstanceOf(SnMatchTermsQuery.class);
		final SnMatchTermsQuery snMatchTermsQuery = (SnMatchTermsQuery) snFilter.getQuery();
		assertThat(snMatchTermsQuery.getMatchType()).isEqualTo(SnMatchType.ANY);
		assertThat(snMatchTermsQuery.getExpression()).isEqualTo(BackofficesearchservicesConstants.INDEX_PROPERTY_CATALOG_VERSION_PK);
		final List<Object> catalogVersionPKs = snMatchTermsQuery.getValues();
		assertThat(catalogVersionPKs.size()).isEqualTo(1);
		assertThat(catalogVersionPKs.get(0)).isEqualTo(pk.toString());
	}

	protected SearchQueryCondition prepareCondition(final String attrName, final Object value,
			final ValueComparisonOperator operator)
	{
		return new SearchQueryCondition(new SearchAttributeDescriptor(attrName), value, operator);
	}
}
