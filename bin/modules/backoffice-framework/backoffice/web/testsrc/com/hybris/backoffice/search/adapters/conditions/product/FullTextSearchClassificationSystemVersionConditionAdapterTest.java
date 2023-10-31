/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.adapters.conditions.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.core.PK;

import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


public class FullTextSearchClassificationSystemVersionConditionAdapterTest
{

	public static final String CLASSIFICATION_SYSTEM_VERSION_PROPERTY_NAME = "classificationSystemVersion";

	private FullTextSearchClassificationSystemVersionConditionAdapter fullTextSearchClassificationSystemVersionConditionAdapter;

	@Before
	public void setup()
	{
		fullTextSearchClassificationSystemVersionConditionAdapter = new FullTextSearchClassificationSystemVersionConditionAdapter();
		fullTextSearchClassificationSystemVersionConditionAdapter.setOperator(ValueComparisonOperator.EQUALS);
		fullTextSearchClassificationSystemVersionConditionAdapter.setClassificationSystemVersionPropertyName(CLASSIFICATION_SYSTEM_VERSION_PROPERTY_NAME);
	}

	@Test
	public void shouldAddCatalogCondition()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final ClassificationSystemVersionModel classificationSystemVersion = mock(ClassificationSystemVersionModel.class);
		final PK classificationSystemPK = PK.BIG_PK;

		given(navigationNode.getData()).willReturn(classificationSystemVersion);
		given(classificationSystemVersion.getPk()).willReturn(classificationSystemPK);

		// when
		fullTextSearchClassificationSystemVersionConditionAdapter.addSearchCondition(searchData, navigationNode);

		// then
		final SearchConditionData searchConditionData = searchData.getCondition(0);
		assertThat(searchConditionData.getFieldType().getName()).isEqualTo(CLASSIFICATION_SYSTEM_VERSION_PROPERTY_NAME);
		assertThat(searchConditionData.getValue()).isEqualTo(classificationSystemPK);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);
	}
}
