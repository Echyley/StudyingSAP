/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.adapters.conditions.product;

import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FullTextSearchUncategorizedConditionAdapterTest {

	private static final String UNCATEGORIZED_SYSTEM_PROPERTY_NAME = "uncategorized";
	private static final String PARENT_NODE_ID = "parentNode";

	private FullTextSearchUncategorizedConditionAdapter fullTextSearchUncategorizedConditionAdapter;

	@Before
	public void setup() {
		fullTextSearchUncategorizedConditionAdapter = new FullTextSearchUncategorizedConditionAdapter();
		fullTextSearchUncategorizedConditionAdapter.setOperator(ValueComparisonOperator.EQUALS);
		fullTextSearchUncategorizedConditionAdapter.setUncategorizedPropertyName(UNCATEGORIZED_SYSTEM_PROPERTY_NAME);
	}

	@Test
	public void shouldBuildUncategorizedSearchCondition() {
		// when
		final SearchConditionData uncategorizedCondition = fullTextSearchUncategorizedConditionAdapter.buildUncategorizedSearchCondition();

		// then
		assertThat(uncategorizedCondition.getFieldType().getName()).isEqualTo(UNCATEGORIZED_SYSTEM_PROPERTY_NAME);
		assertThat(uncategorizedCondition.getValue()).isEqualTo(Boolean.TRUE);
		assertThat(uncategorizedCondition.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);
		assertThat(FullTextSearchUncategorizedConditionAdapter.PARENT_NODE_ID).isEqualTo(PARENT_NODE_ID);
	}
}
