/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.adapters.conditions.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.core.PK;

import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


public class FullTextSearchClassificationSystemConditionAdapterTest
{

	public static final String CLASSIFICATION_SYSTEM_PROPERTY_NAME = "classificationSystem";

	private FullTextSearchClassificationSystemConditionAdapter fullTextSearchClassificationSystemConditionAdapter;

	@Before
	public void setup()
	{
		fullTextSearchClassificationSystemConditionAdapter = new FullTextSearchClassificationSystemConditionAdapter();
		fullTextSearchClassificationSystemConditionAdapter.setOperator(ValueComparisonOperator.EQUALS);
		fullTextSearchClassificationSystemConditionAdapter.setClassificationSystemPropertyName(CLASSIFICATION_SYSTEM_PROPERTY_NAME);
	}

	@Test
	public void shouldAddCatalogCondition()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final ClassificationSystemModel classificationSystem = mock(ClassificationSystemModel.class);
		final PK classificationSystemPK = PK.BIG_PK;

		given(navigationNode.getData()).willReturn(classificationSystem);
		given(classificationSystem.getPk()).willReturn(classificationSystemPK);

		// when
		fullTextSearchClassificationSystemConditionAdapter.addSearchCondition(searchData, navigationNode);

		// then
		final SearchConditionData searchConditionData = searchData.getCondition(0);
		assertThat(searchConditionData.getFieldType().getName()).isEqualTo(CLASSIFICATION_SYSTEM_PROPERTY_NAME);
		assertThat(searchConditionData.getValue()).isEqualTo(classificationSystemPK);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);
	}
}
