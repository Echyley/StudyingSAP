/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.converters.impl;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Breadcrumb;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.cockpitng.search.data.facet.FacetData;
import com.hybris.cockpitng.search.data.facet.FacetValueData;


public class DefaultFullTextSearchDataConverterTest
{
	public static final String FACET_CATEGORY = "category";
	public static final String FACET_PRICE = "price";
	public static final String FACET_BREADCRUMB = "breadcrumbFacet";
	public static final String VAL_BREADCRUMB = "breadcrumbValue";
	public static final String VAL_SHOES = "shoes";
	public static final String VAL_SHIRTS = "shirts";
	public static final String VAL_PRICE_100 = "100";
	public static final String VAL_PRICE_200 = "200";
	@Mock
	private IndexedType indexedType;
	private DefaultFullTextSearchDataConverter converter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		converter = new DefaultFullTextSearchDataConverter();
		final Map<String, IndexedProperty> indexedPropertyMap = mock(Map.class);
		when(indexedType.getIndexedProperties()).thenReturn(indexedPropertyMap);
		when(indexedPropertyMap.get(anyString())).thenAnswer(invocationOnMock -> {
			final String name = (String) invocationOnMock.getArguments()[0];
			final IndexedProperty indexedProperty = new IndexedProperty();
			indexedProperty.setName(name);
			indexedProperty.setBackofficeDisplayName(name);
			indexedProperty.setFacetType(FacetType.MULTISELECTAND);
			return indexedProperty;
		});
	}

	@Test
	public void convertFacetsWithNotDuplicatedBreadcrumbs()
	{
		final List<Facet> facets = new ArrayList<>();
		facets.add(new Facet(FACET_CATEGORY, //
				newArrayList(new FacetValue(VAL_SHOES, 20, true), new FacetValue(VAL_SHIRTS, 10, false))));
		facets.add(new Facet(FACET_PRICE, //
				newArrayList(new FacetValue(VAL_PRICE_100, 20, true), new FacetValue(VAL_PRICE_200, 10, false))));

		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(createBreadcrumb(FACET_BREADCRUMB, VAL_BREADCRUMB));

		final Collection<FacetData> convertFacets = converter.convertFacets(facets, breadcrumbs, indexedType);
		assertThat(convertFacets).hasSize(3);

		final Map<String, FacetData> facetDataMap = convertFacets.stream().collect(Collectors.toMap(FacetData::getName, fd -> fd));
		assertThat(facetDataMap.get(FACET_CATEGORY)).isNotNull();
		assertThat(facetDataMap.get(FACET_PRICE)).isNotNull();
		assertThat(facetDataMap.get(FACET_BREADCRUMB)).isNotNull();

		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValues()).hasSize(2);
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_SHOES).isSelected()).isTrue();
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_SHOES).getCount()).isEqualTo(20);
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_SHIRTS).isSelected()).isFalse();
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_SHIRTS).getCount()).isEqualTo(10);

		assertThat(facetDataMap.get(FACET_PRICE).getFacetValues()).hasSize(2);
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_PRICE_100).isSelected()).isTrue();
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_PRICE_100).getCount()).isEqualTo(20);
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_PRICE_200).isSelected()).isFalse();
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_PRICE_200).getCount()).isEqualTo(10);

		assertThat(facetDataMap.get(FACET_BREADCRUMB).getFacetValues()).hasSize(1);
		assertThat(facetDataMap.get(FACET_BREADCRUMB).getFacetValue(VAL_BREADCRUMB).isSelected()).isTrue();
		assertThat(facetDataMap.get(FACET_BREADCRUMB).getFacetValue(VAL_BREADCRUMB).getCount()).isEqualTo(0);
	}

	@Test
	public void convertFacetsWithBreadcrumbForExistingFacet()
	{
		final List<Facet> facets = new ArrayList<>();
		facets.add(new Facet(FACET_CATEGORY, //
				newArrayList(new FacetValue(VAL_SHOES, 20, true), new FacetValue(VAL_SHIRTS, 10, false))));
		facets.add(new Facet(FACET_PRICE, //
				newArrayList(new FacetValue(VAL_PRICE_100, 20, true), new FacetValue(VAL_PRICE_200, 10, false),
						new FacetValue(VAL_BREADCRUMB, 5, false))));

		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(createBreadcrumb(FACET_CATEGORY, VAL_BREADCRUMB));
		breadcrumbs.add(createBreadcrumb(FACET_PRICE, VAL_BREADCRUMB));

		final Collection<FacetData> convertFacets = converter.convertFacets(facets, breadcrumbs, indexedType);
		assertThat(convertFacets).hasSize(2);

		final Map<String, FacetData> facetDataMap = convertFacets.stream().collect(Collectors.toMap(FacetData::getName, fd -> fd));
		assertThat(facetDataMap.get(FACET_CATEGORY)).isNotNull();
		assertThat(facetDataMap.get(FACET_PRICE)).isNotNull();

		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValues()).hasSize(3);
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_BREADCRUMB).isSelected()).isTrue();
		assertThat(facetDataMap.get(FACET_CATEGORY).getFacetValue(VAL_BREADCRUMB).getCount())
				.isEqualTo(FacetValueData.BREADCRUMB_COUNT);

		assertThat(facetDataMap.get(FACET_PRICE).getFacetValues()).hasSize(3);
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_BREADCRUMB).isSelected()).isTrue();
		assertThat(facetDataMap.get(FACET_PRICE).getFacetValue(VAL_BREADCRUMB).getCount()).isEqualTo(5);
	}

	@Test
	public void shouldMergeBreadcrumbWithSelectedFacet()
	{
		// given
		final FacetValue facetValue = new FacetValue(VAL_SHIRTS, "shirtsDisplayName", 10, false);
		final FacetValue facetValueSelected = new FacetValue(VAL_SHOES, "shoesDisplayName", 20, true);

		final Facet facet = new Facet(FACET_CATEGORY, singletonList(facetValue));
		facet.setSelectedFacetValues(singletonList(facetValueSelected));
		final List<Facet> facets = singletonList(facet);

		final List<Breadcrumb> breadcrumbs = singletonList((createBreadcrumb(FACET_CATEGORY, VAL_SHOES)));

		// when
		final Collection<FacetData> facetData = converter.convertFacets(facets, breadcrumbs, indexedType);

		// then
		assertThat(facetData).hasSize(1);
		assertThat(facetData.stream().findFirst().get().getFacetValues()).extracting(FacetValueData::getDisplayName)
				.contains("shoesDisplayName");
	}

	@Test
	public void shouldKeepFacetsOrder()
	{
		// given
		final List<Facet> facets = new ArrayList<>();
		facets.add(new Facet(FACET_CATEGORY, singletonList(new FacetValue(VAL_SHOES, 20, false))));
		facets.add(new Facet(FACET_PRICE, singletonList(new FacetValue(VAL_PRICE_100, 20, false))));
		facets.add(new Facet(FACET_BREADCRUMB, singletonList(new FacetValue(VAL_PRICE_100, 20, false))));

		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(createBreadcrumb(FACET_CATEGORY, VAL_BREADCRUMB));
		breadcrumbs.add(createBreadcrumb(FACET_PRICE, VAL_BREADCRUMB));
		breadcrumbs.add(createBreadcrumb(FACET_BREADCRUMB, VAL_BREADCRUMB));

		// when
		final Collection<FacetData> facetData = converter.convertFacets(facets, breadcrumbs, indexedType);

		// then
		assertThat(facetData).extracting(FacetData::getName).containsExactly(FACET_CATEGORY, FACET_PRICE, FACET_BREADCRUMB);
	}

	@Test
	public void shouldFacetValuesBeTakenWhenAllFacetValuesCollectionHasTheSameSize()
	{
		// given
		final FacetValue approved = new FacetValue(ArticleApprovalStatus.APPROVED.getCode(), 1, false);
		final FacetValue checked = new FacetValue(ArticleApprovalStatus.CHECK.getCode(), 1, false);

		final List<FacetValue> facetValues = List.of(approved, checked);
		final List<FacetValue> allFacetValues = List.of(checked, approved);

		final Facet facet = new Facet(ProductModel.APPROVALSTATUS, facetValues);
		facet.setAllFacetValues(allFacetValues);

		final IndexedProperty indexedProperty = mock(IndexedProperty.class);
		given(indexedProperty.getBackofficeDisplayName()).willReturn("name");

		// when
		final FacetData facetData = converter.convertFacet(facet, indexedProperty);

		// then
		assertThat(facetData.getFacetValues().stream().map(FacetValueData::getName).collect(Collectors.toList()))
				.isEqualTo(facetValues.stream().map(FacetValue::getName).collect(Collectors.toList()));
		assertThat(facetData.getFacetValues().stream().map(FacetValueData::getName).collect(Collectors.toList()))
				.isNotEqualTo(allFacetValues.stream().map(FacetValue::getName).collect(Collectors.toList()));
	}

	public Breadcrumb createBreadcrumb(final String fieldName, final String value)
	{
		// breadcrumb has protected constructor, that's why it has to be mocked
		final Breadcrumb breadcrumb = mock(Breadcrumb.class);
		when(breadcrumb.getFieldName()).thenReturn(fieldName);
		when(breadcrumb.getValue()).thenReturn(value);
		when(breadcrumb.getDisplayValue()).thenReturn(value);
		return breadcrumb;
	}
}
