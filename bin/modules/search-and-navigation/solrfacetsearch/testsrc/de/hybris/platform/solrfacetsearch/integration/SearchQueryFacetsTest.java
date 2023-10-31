/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import org.junit.Test;


public class SearchQueryFacetsTest extends AbstractSearchQueryTest
{
	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchQueryFacetsTest.csv");
	}

	@Test
	public void addRefineFacet() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addRefineFacetWithSingleFacetValue() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(0);
	}

	@Test
	public void addRefineFacetWithMultipleFacetValues() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode(),
					ArticleApprovalStatus.CHECK.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().isEmpty();

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(0);
	}

	@Test
	public void addMultiselectOrFacet() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTOR);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultiselectOrFacetWithSingleFacetValue() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTOR);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultiselectOrFacetWithMultipleFacetValues() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTOR);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode(),
					ArticleApprovalStatus.CHECK.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(3);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultiselectAndFacet() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTAND);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultiselectAndFacetWithSingleFacetValue() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTAND);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultiselectAndFacetWithMultipleFacetValues() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTAND);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode(),
					ArticleApprovalStatus.CHECK.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().isEmpty();

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addMultipleFacets() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTOR);
			searchQuery.addFacet(PRODUCT_CODE_FIELD, FacetType.REFINE);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet1).isNotNull();
		assertThat(facet1.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet1.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertThat(facet2).isNotNull();
		assertThat(facet2.getFacetValues()).isNotNull().hasSize(2);
		assertThat(facet2.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount)
				.containsOnly(tuple(PRODUCT1_CODE, 2L), tuple(PRODUCT2_CODE, 2L));
	}

	@Test
	public void addMultipleFacetsWithFacetValues() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.MULTISELECTOR);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
			searchQuery.addFacet(PRODUCT_CODE_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_CODE_FIELD, PRODUCT2_CODE);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(1);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT2_CODE);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet1.getFacetValues()).isNotNull().hasSize(2);
		assertThat(facet1.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 1L), tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));


		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertThat(facet2.getFacetValues()).isNotNull().hasSize(0);
	}

	@Test
	public void addFacetWithEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, FacetType.REFINE);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(2);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount)
				.containsOnly(tuple(PRODUCT1_NAME, 2L), tuple(PRODUCT2_NAME, 2L));
	}

	@Test
	public void addFacetValueWithEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addSort(PRODUCT_APPROVAL_STATUS_FIELD);
			searchQuery.addFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, PRODUCT1_NAME);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(0);
	}

	@Test
	public void addFacetValueWithEscaping2() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addFacet(PRODUCT_NAME_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_NAME_FIELD, "AND");
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().isEmpty();

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().isEmpty();
	}

	@Test
	public void addFacetValueWithEscaping3() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addFacet(PRODUCT_NAME_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_NAME_FIELD, PRODUCT1_NAME + ":OR");
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().isEmpty();

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().isEmpty();
	}

	@Test
	public void addFacetForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));
	}

	@Test
	public void addFacetValueForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(0);
	}

	@Test
	public void addMultipleFacetsForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForCodeField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet1).isNotNull();
		assertThat(facet1.getFacetValues()).isNotNull().hasSize(3);
		assertThat(facet1.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount).containsOnly(
				tuple(ArticleApprovalStatus.APPROVED.getCode(), 2L), tuple(ArticleApprovalStatus.CHECK.getCode(), 1L),
				tuple(ArticleApprovalStatus.UNAPPROVED.getCode(), 1L));

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertThat(facet2).isNotNull();
		assertThat(facet2.getFacetValues()).isNotNull().hasSize(2);
		assertThat(facet2.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount)
				.containsOnly(tuple(PRODUCT1_CODE, 2L), tuple(PRODUCT2_CODE, 2L));
	}

	@Test
	public void addMultipleFacetValuesForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForCodeField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
			searchQuery.addFacetValue(PRODUCT_CODE_FIELD, PRODUCT2_CODE);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(1);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT2_CODE);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertThat(facet1).isNotNull();
		assertNotNull(facet1.getFacetValues());

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertThat(facet2).isNotNull();
		assertNotNull(facet2.getFacetValues());
	}

	@Test
	public void addFacetWithEscapingForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForNameField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(4);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE, PRODUCT2_CODE, PRODUCT2_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(2);
		assertThat(facet.getFacetValues()).extracting(FacetValue::getName, FacetValue::getCount)
				.containsOnly(tuple(PRODUCT1_NAME, 2L), tuple(PRODUCT2_NAME, 2L));
	}

	@Test
	public void addFacetValueWithEscapingForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForNameField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addSort(PRODUCT_APPROVAL_STATUS_FIELD);
			searchQuery.addFacetValue(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, PRODUCT1_NAME);
		});

		// then
		assertThat(searchResult.getDocuments()).isNotNull().hasSize(2);
		assertThat(searchResult.getDocuments()).extracting(document -> document.getFields().get(PRODUCT_CODE_FIELD))
				.containsExactly(PRODUCT1_CODE, PRODUCT1_CODE);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertThat(facet).isNotNull();
		assertThat(facet.getFacetValues()).isNotNull().hasSize(0);
	}
}
