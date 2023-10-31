/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearchsolr.strategies.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.constants.AdaptivesearchConstants;
import de.hybris.platform.adaptivesearch.services.AsCategoryService;
import de.hybris.platform.adaptivesearch.strategies.impl.AbstractAsCategoryPathResolver;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSolrAsCategoryPathResolverTest
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
	private CatalogVersionModel catalogVersion;

	@Mock
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private IndexedType indexedType;

	private SearchQuery searchQuery;

	private DefaultSolrAsCategoryPathResolver solrAsCategoryPathResolver;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(AbstractAsCategoryPathResolver.FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		when(configuration.getString(AbstractAsCategoryPathResolver.FACET_FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);

		solrAsCategoryPathResolver = new DefaultSolrAsCategoryPathResolver();
		solrAsCategoryPathResolver.setConfigurationService(configurationService);
		solrAsCategoryPathResolver.setAsCategoryService(asCategoryService);
	}

	@Test
	public void getEmptyCategoryPath()
	{
		// given
		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
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
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterQuery()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		searchQuery.addFilterQuery(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY, CATEGORY_1_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterQueryWithConfiguredIndexProperty()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		searchQuery.addFilterQuery(INDEX_PROPERTY_CODE, CATEGORY_1_CODE);

		when(configuration.getString(AbstractAsCategoryPathResolver.FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(INDEX_PROPERTY_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFacetValue()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		searchQuery.addFacet(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		searchQuery.addFacetValue(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY, CATEGORY_1_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFacetValueWithConfiguredIndexProperty()
	{
		// given
		final CategoryModel category = mock(CategoryModel.class);

		searchQuery.addFacet(INDEX_PROPERTY_CODE);
		searchQuery.addFacetValue(INDEX_PROPERTY_CODE, CATEGORY_1_CODE);

		when(configuration.getString(AbstractAsCategoryPathResolver.FACET_FILTER_INDEX_PROPERTY_KEY,
				AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY)).thenReturn(INDEX_PROPERTY_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category));

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void getCategoryPathFromFilterQueryAndFacetValue()
	{
		// given
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);

		searchQuery.addFilterQuery(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY, CATEGORY_1_CODE);

		searchQuery.addFacet(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY);
		searchQuery.addFacetValue(AdaptivesearchConstants.ALL_CATEGORIES_PROPERTY, CATEGORY_2_CODE);

		when(asCategoryService.getCurrentCategoryPath()).thenReturn(Optional.empty());
		when(asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE, CATEGORY_2_CODE), List.of(catalogVersion), true))
				.thenReturn(List.of(category1, category2));

		// when
		final List<CategoryModel> categoryPath = solrAsCategoryPathResolver.resolveCategoryPath(searchQuery,
				List.of(catalogVersion));

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category1, category2);
	}
}
