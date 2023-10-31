/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.searchservices.strategies.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.constants.AdaptivesearchConstants;
import de.hybris.platform.adaptivesearch.services.AsCategoryService;
import de.hybris.platform.adaptivesearch.strategies.impl.AbstractAsCategoryPathResolver;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.searchservices.search.data.SnBucketsFacetFilter;
import de.hybris.platform.searchservices.search.data.SnFilter;
import de.hybris.platform.searchservices.search.data.SnMatchTermQuery;
import de.hybris.platform.searchservices.search.data.SnMatchTermsQuery;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.service.SnSearchContext;
import de.hybris.platform.searchservices.search.service.SnSearchRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSnAsCategoryPathResolverTest
{
	private static final String INDEX_PROPERTY_CODE = "property1";

	private static final String CATEGORY_1_CODE = "cat1";
	private static final String CATEGORY_2_CODE = "cat2";

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AsCategoryService asCategoryService;

	@Mock
	private SnSearchContext searchContext;

	@Mock
	private SnSearchRequest searchRequest;

	@Mock
	private CatalogVersionModel catalogVersion;

	private SnSearchQuery searchQuery;

	private DefaultSnAsCategoryPathResolver snAsCategoryPathResolver;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		searchQuery = new SnSearchQuery();

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(AbstractAsCategoryPathResolver.FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		when(configuration.getString(AbstractAsCategoryPathResolver.FACET_FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);

		when(searchContext.getSearchRequest()).thenReturn(searchRequest);
		when(searchRequest.getSearchQuery()).thenReturn(searchQuery);

		snAsCategoryPathResolver = new DefaultSnAsCategoryPathResolver();
		snAsCategoryPathResolver.setConfigurationService(configurationService);
		snAsCategoryPathResolver.setAsCategoryService(asCategoryService);
	}

	@Test
	public void getEmptyCategoryPath()
	{
		// given
		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotNull().isEmpty();
	}

	@Test
	public void getCategoryPathFromCurrentSessionCategoryPath()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.of(List.of(category)));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterWithMatchTermQuery()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		final SnMatchTermQuery filterQuery = new SnMatchTermQuery();
		filterQuery.setExpression(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		filterQuery.setValue(CATEGORY_1_CODE);

		final SnFilter filter = new SnFilter();
		filter.setQuery(filterQuery);

		searchQuery.getFilters().add(filter);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterWithMatchTermsQuery()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		final SnMatchTermsQuery filterQuery = new SnMatchTermsQuery();
		filterQuery.setExpression(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		filterQuery.setValues(List.of(CATEGORY_1_CODE));

		final SnFilter filter = new SnFilter();
		filter.setQuery(filterQuery);

		searchQuery.getFilters().add(filter);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterWithConfiguredIndexProperty()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		final SnMatchTermQuery filterQuery = new SnMatchTermQuery();
		filterQuery.setExpression(INDEX_PROPERTY_CODE);
		filterQuery.setValue(CATEGORY_1_CODE);

		final SnFilter filter = new SnFilter();
		filter.setQuery(filterQuery);

		searchQuery.getFilters().add(filter);

		when(configuration.getString(AbstractAsCategoryPathResolver.FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(INDEX_PROPERTY_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFacetFilter()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		final SnBucketsFacetFilter facetFilter = new SnBucketsFacetFilter();
		facetFilter.setFacetId(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		facetFilter.setBucketIds(List.of(CATEGORY_1_CODE));

		searchQuery.getFacetFilters().add(facetFilter);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFacetFilterWithConfiguredIndexProperty()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		final SnBucketsFacetFilter facetFilter = new SnBucketsFacetFilter();
		facetFilter.setFacetId(INDEX_PROPERTY_CODE);
		facetFilter.setBucketIds(List.of(CATEGORY_1_CODE));

		searchQuery.getFacetFilters().add(facetFilter);

		when(configuration.getString(AbstractAsCategoryPathResolver.FACET_FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(INDEX_PROPERTY_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterAndFacetFilter()
	{
		// given
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);

		final SnMatchTermQuery filterQuery = new SnMatchTermQuery();
		filterQuery.setExpression(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		filterQuery.setValue(CATEGORY_1_CODE);

		final SnFilter filter = new SnFilter();
		filter.setQuery(filterQuery);

		searchQuery.getFilters().add(filter);

		final SnBucketsFacetFilter facetFilter = new SnBucketsFacetFilter();
		facetFilter.setFacetId(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		facetFilter.setBucketIds(List.of(CATEGORY_2_CODE));

		searchQuery.getFacetFilters().add(facetFilter);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE, CATEGORY_2_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category1, category2));

		// when
		final List<CategoryModel> categoryPath = snAsCategoryPathResolver.resolveCategoryPath(searchContext,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category1, category2);
	}
}
