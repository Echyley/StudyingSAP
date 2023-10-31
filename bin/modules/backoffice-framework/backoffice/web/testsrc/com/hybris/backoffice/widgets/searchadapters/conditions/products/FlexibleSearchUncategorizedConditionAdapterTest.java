/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.searchadapters.conditions.products;

import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlexibleSearchUncategorizedConditionAdapterTest {

	private static final String PARENT_NODE_ID = "parentNode";
	private FlexibleSearchUncategorizedConditionAdapter flexibleSearchUncategorizedConditionAdapter = new FlexibleSearchUncategorizedConditionAdapter();

	@Test
	public void shouldBuildUncategorizedSearchCondition() {

		// when
		final SearchConditionData uncategorizedCondition = flexibleSearchUncategorizedConditionAdapter.buildUncategorizedSearchCondition();

		// then
		assertThat(uncategorizedCondition.getFieldType().getName()).isEqualTo(ProductModel.SUPERCATEGORIES);
		assertThat(uncategorizedCondition.getValue()).isNull();
		assertThat(uncategorizedCondition.getOperator()).isEqualTo(ValueComparisonOperator.IS_EMPTY);
		assertThat(FlexibleSearchUncategorizedConditionAdapter.PARENT_NODE_ID).isEqualTo(PARENT_NODE_ID);
	}
}
