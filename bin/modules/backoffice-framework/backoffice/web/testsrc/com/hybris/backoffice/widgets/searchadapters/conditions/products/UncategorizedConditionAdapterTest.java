/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.searchadapters.conditions.products;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.tree.model.CatalogTreeModelPopulator;
import com.hybris.backoffice.tree.model.UncategorizedNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.backoffice.widgets.searchadapters.conditions.SearchConditionAdapter;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UncategorizedConditionAdapterTest {

	static class UncategorizedConditionAdapterImpl extends UncategorizedConditionAdapter {
		protected SearchConditionData buildUncategorizedSearchCondition(){
			return createSearchConditions(ProductModel.SUPERCATEGORIES, null,
					ValueComparisonOperator.IS_EMPTY);
		};
	}

	private UncategorizedConditionAdapterImpl uncategorizedConditionAdapter = new UncategorizedConditionAdapterImpl();

	@Test
	public void shouldNotHandleIfNodeNotEndWithUncategorizedProducts() {
		// given
		final NavigationNode node = mock(NavigationNode.class);
		when(node.getId()).thenReturn("test");

		// when
		final boolean canHandle = uncategorizedConditionAdapter.canHandle(node);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleIfNodeIsNotUncategorizedNode() {
		// given
		final NavigationNode node = mock(NavigationNode.class);
		when(node.getId()).thenReturn(CatalogTreeModelPopulator.UNCATEGORIZED_PRODUCTS_NODE_ID);
		when(node.getData()).thenReturn("test");

		// when
		final boolean canHandle = uncategorizedConditionAdapter.canHandle(node);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleIfNodeIsUncategorizedNode() {
		// given
		final NavigationNode node = mock(NavigationNode.class);
		when(node.getId()).thenReturn(CatalogTreeModelPopulator.UNCATEGORIZED_PRODUCTS_NODE_ID);
		when(node.getData()).thenReturn(mock(UncategorizedNode.class));

		// when
		final boolean canHandle = uncategorizedConditionAdapter.canHandle(node);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldAddUncategorizedCondition() {
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode node = mock(NavigationNode.class);
		final UncategorizedNode uncategorizedNode = mock(UncategorizedNode.class);

		when(node.getData()).thenReturn(uncategorizedNode);
		when(uncategorizedNode.getParentItem()).thenReturn(null);

		// when
		uncategorizedConditionAdapter.addSearchCondition(searchData, node);

		// then
		assertThat(searchData.getCondition(0)).isNotNull();
		assertThat(searchData.getCondition(0)).isInstanceOf(SearchConditionDataList.class);
		final SearchConditionDataList searchConditionData = (SearchConditionDataList)searchData.getCondition(0);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.AND);
		final List<SearchConditionData> conditionList = searchConditionData.getConditions();
		assertThat(conditionList.size()).isEqualTo(1);
		final SearchConditionData condition = conditionList.get(0);
		assertThat(condition.getFieldType().getName()).isEqualTo(ProductModel.SUPERCATEGORIES);
		assertThat(condition.getValue()).isNull();
		assertThat(condition.getOperator()).isEqualTo(ValueComparisonOperator.IS_EMPTY);
	}

	@Test
	public void shouldAddUncategorizedConditionAndCatalogVersionCondition() {
		// given
		final String catalogVersionPropertyName = "CatalogVersion";
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode node = mock(NavigationNode.class);
		final UncategorizedNode uncategorizedNode = mock(UncategorizedNode.class);
		final CatalogVersionModel mockCatalogVersion = mock(CatalogVersionModel.class);
		final PK catalogVersionPK = PK.BIG_PK;
		final CatalogVersionConditionAdapter catalogVersionAdapter = new CatalogVersionConditionAdapter();
		catalogVersionAdapter.setOperator(ValueComparisonOperator.EQUALS);
		catalogVersionAdapter.setCatalogVersionPropertyName(catalogVersionPropertyName);
		final SearchConditionAdapter mockAdapter = mock(SearchConditionAdapter.class);

		uncategorizedConditionAdapter.setConditionsAdapters(Arrays.asList(catalogVersionAdapter, mockAdapter));
		when(node.getData()).thenReturn(uncategorizedNode);
		when(uncategorizedNode.getParentItem()).thenReturn(mockCatalogVersion);
		when(mockCatalogVersion.getPk()).thenReturn(catalogVersionPK);
		when(mockAdapter.canHandle(any())).thenReturn(true);

		// when
		uncategorizedConditionAdapter.addSearchCondition(searchData, node);

		// then
		verify(mockAdapter, times(0)).addSearchCondition(eq(searchData), any());
		assertThat(searchData.getCondition(0)).isNotNull();
		assertThat(searchData.getCondition(0)).isInstanceOf(SearchConditionDataList.class);
		final SearchConditionDataList searchConditionData = (SearchConditionDataList)searchData.getCondition(0);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.AND);
		final List<SearchConditionData> conditionList = searchConditionData.getConditions();
		assertThat(conditionList.size()).isEqualTo(2);
		final SearchConditionData catalogVersionCondition = conditionList.get(0);
		final SearchConditionData uncategorizedCondition = conditionList.get(1);

		assertThat(catalogVersionCondition.getFieldType().getName()).isEqualTo(catalogVersionPropertyName);
		assertThat(catalogVersionCondition.getValue()).isEqualTo(catalogVersionPK);
		assertThat(catalogVersionCondition.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);

		assertThat(uncategorizedCondition.getFieldType().getName()).isEqualTo(ProductModel.SUPERCATEGORIES);
		assertThat(uncategorizedCondition.getValue()).isNull();
		assertThat(uncategorizedCondition.getOperator()).isEqualTo(ValueComparisonOperator.IS_EMPTY);
	}
}
