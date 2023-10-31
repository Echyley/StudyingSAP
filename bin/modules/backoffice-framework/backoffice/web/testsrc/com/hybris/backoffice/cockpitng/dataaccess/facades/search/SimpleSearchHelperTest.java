/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.cockpitng.dataaccess.facades.search;

import static com.hybris.backoffice.cockpitng.dataaccess.facades.search.SimpleSearchHelper.FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.util.Config;

import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchMode;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.SimpleSearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class SimpleSearchHelperTest
{

	@Test
	public void shouldBeTrueIfDataIsSimpleSearchQueryData()
	{
		final SearchQueryData target = new SimpleSearchQueryData("Catalog");
		assertThat(SimpleSearchHelper.isSimpleSearch(target)).isTrue();
	}

	@Test
	public void shouldBeTrueIfDataIsAdvancedSearchQueryDataWithSimpleMode()
	{
		final SearchQueryData target = new AdvancedSearchQueryData.Builder("Catalog").advancedSearchMode(AdvancedSearchMode.SIMPLE)
				.build();
		assertThat(SimpleSearchHelper.isSimpleSearch(target)).isTrue();
	}

	@Test
	public void shouldBeFalseIfDataIsAdvancedSearchQueryDataWithAdvancedMode()
	{
		final SearchQueryData target = new AdvancedSearchQueryData.Builder("Catalog")
				.advancedSearchMode(AdvancedSearchMode.ADVANCED).build();
		assertThat(SimpleSearchHelper.isSimpleSearch(target)).isFalse();
	}

	@Test
	public void shouldNotProcessOperatorIfNotSimpleSearchCase()
	{
		final AdvancedSearchQueryData queryData = mock(AdvancedSearchQueryData.class);
		when(queryData.getAdvancedSearchMode()).thenReturn(AdvancedSearchMode.ADVANCED);
		SimpleSearchHelper.processDefaultComparisonOperator(queryData, new SearchQueryCondition());
		verify(queryData, never()).getSearchType();
	}

	@Test
	public void shouldChangeOperatorToStartswithIfConfigured()
	{
		final ValueComparisonOperator configOperator = ValueComparisonOperator.STARTS_WITH;
		final String configValue = "Catalog";
		final Pair<SearchQueryData, SearchQueryCondition> context = initTestContext("Catalog");
		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue);
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(context.getSecond().getOperator()).isEqualTo(configOperator);
		}
	}

	@Test
	public void shouldChangeOperatorToEndswithIfConfigured()
	{
		final ValueComparisonOperator configOperator = ValueComparisonOperator.ENDS_WITH;
		final String configValue = "Order, Catalog";
		final Pair<SearchQueryData, SearchQueryCondition> context = initTestContext("Catalog");
		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES,
					ValueComparisonOperator.STARTS_WITH.getOperatorCode().toLowerCase()), "")).thenReturn("");
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue);
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(context.getSecond().getOperator()).isEqualTo(configOperator);
		}
	}

	@Test
	public void shouldChangeOperatorToEqualsIfConfigured()
	{
		final ValueComparisonOperator configOperator = ValueComparisonOperator.EQUALS;
		final String configValue = "Order,Catalog,User";
		final Pair<SearchQueryData, SearchQueryCondition> context = initTestContext("Catalog");
		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES,
					ValueComparisonOperator.STARTS_WITH.getOperatorCode().toLowerCase()), "")).thenReturn("");
			configMock.when(() -> Config.getString(String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES,
					ValueComparisonOperator.ENDS_WITH.getOperatorCode().toLowerCase()), "")).thenReturn("");
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue);
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(context.getSecond().getOperator()).isEqualTo(configOperator);
		}
	}

	@Test
	public void shouldOnlyChangeOperatorForConfiguredType_NoImpactForOthers()
	{
		final ValueComparisonOperator configOperator = ValueComparisonOperator.STARTS_WITH;
		final String configValue = "Catalog";
		final Pair<SearchQueryData, SearchQueryCondition> context = initTestContext("Catalog");
		final Pair<SearchQueryData, SearchQueryCondition> other = initTestContext("Order");
		assertThat(context.getSecond().getOperator()).isEqualTo(ValueComparisonOperator.CONTAINS);
		assertThat(other.getSecond().getOperator()).isEqualTo(ValueComparisonOperator.CONTAINS);

		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue);
			// change operator to startsWith for "Catalog"
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(context.getSecond().getOperator()).isEqualTo(configOperator);
			//no impact for "Order", still default "contains"
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(other.getSecond().getOperator()).isEqualTo(ValueComparisonOperator.CONTAINS);
		}
	}

	@Test
	public void shouldChangeOperatorToStartswithIfConfigDuplicated()
	{
		final ValueComparisonOperator configOperator1 = ValueComparisonOperator.STARTS_WITH;
		final String configValue1 = "Order,Catalog";
		final ValueComparisonOperator configOperator2 = ValueComparisonOperator.ENDS_WITH;
		final String configValue2 = "Catalog";

		final Pair<SearchQueryData, SearchQueryCondition> context = initTestContext("Catalog");

		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator2.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue2);
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator1.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue1);
			SimpleSearchHelper.processDefaultComparisonOperator(context.getFirst(), context.getSecond());
			assertThat(context.getSecond().getOperator()).isEqualTo(configOperator1);
		}
	}

	@Test
	public void shouldApplyOperatorStartswithForAllTypesIfWildcardConfigured()
	{
		final ValueComparisonOperator configOperator = ValueComparisonOperator.STARTS_WITH;
		final String configValue = "*";
		final Pair<SearchQueryData, SearchQueryCondition> context1 = initTestContext("Catalog");
		final Pair<SearchQueryData, SearchQueryCondition> context2 = initTestContext("Order");
		final Pair<SearchQueryData, SearchQueryCondition> context3 = initTestContext("User");

		try (final MockedStatic<Config> configMock = Mockito.mockStatic(Config.class))
		{
			configMock.when(() -> Config.getString(
					String.format(FLEXIBLE_SEARCH_COMPARISON_OPERATOR_TYPES, configOperator.getOperatorCode().toLowerCase()), ""))
					.thenReturn(configValue);

			SimpleSearchHelper.processDefaultComparisonOperator(context1.getFirst(), context1.getSecond());
			assertThat(context1.getSecond().getOperator()).isEqualTo(configOperator);

			SimpleSearchHelper.processDefaultComparisonOperator(context2.getFirst(), context2.getSecond());
			assertThat(context2.getSecond().getOperator()).isEqualTo(configOperator);

			SimpleSearchHelper.processDefaultComparisonOperator(context3.getFirst(), context3.getSecond());
			assertThat(context3.getSecond().getOperator()).isEqualTo(configOperator);
		}
	}

	private Pair<SearchQueryData, SearchQueryCondition> initTestContext(final String typeCode)
	{
		final SearchAttributeDescriptor descriptor = mock(SearchAttributeDescriptor.class);
		final SearchQueryCondition condition = new SearchQueryCondition(descriptor, "searchTerm", ValueComparisonOperator.CONTAINS);
		final AdvancedSearchQueryData queryData = mock(AdvancedSearchQueryData.class);
		when(queryData.getAdvancedSearchMode()).thenReturn(AdvancedSearchMode.SIMPLE);
		when(queryData.getSearchType()).thenReturn(typeCode);
		return Pair.create(queryData, condition);
	}

}
