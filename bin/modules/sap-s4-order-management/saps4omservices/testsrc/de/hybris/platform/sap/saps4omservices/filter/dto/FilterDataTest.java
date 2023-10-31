/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.filter.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FilterDataTest {
	
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String OPERATOR = "operator";
	private static final String SEPARATOR = "separator";
	private static final String SPACE_WITH_COMMA = ", ";

	
	@Test
	public void testFilterBuilder() {
		final FilterData filter = new FilterData.FilterDataBuilder(KEY).operatorWithSpacePrefix(OPERATOR).filterDataOperator(OPERATOR)
				.separatorWithSpacePrefix(SEPARATOR).filterDataSeparator(SEPARATOR).valueWithSpacePrefix(VALUE).filterDataValue(VALUE).build();
		
		Assert.assertEquals(KEY,filter.getKey());
		Assert.assertEquals(OPERATOR,filter.getOperator());
		Assert.assertEquals(SEPARATOR,filter.getSeparator());
		Assert.assertEquals(VALUE,filter.getValue());
		Assert.assertEquals("FilterData: ".concat(KEY).concat(SPACE_WITH_COMMA).concat(OPERATOR).concat(SPACE_WITH_COMMA).concat(VALUE).concat(SPACE_WITH_COMMA).concat(SEPARATOR),filter.toString());
	}

}
