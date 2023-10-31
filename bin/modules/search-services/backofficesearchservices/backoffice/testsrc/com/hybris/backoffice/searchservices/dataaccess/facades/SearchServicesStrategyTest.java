/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.dataaccess.facades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.searchservices.enums.SnFieldType;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SearchServicesStrategyTest
{
	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;

	@InjectMocks
	private SearchServicesStrategy searchServicesStrategy;

	private static final String TEST_TYPE_CODE = "typeCode";
	private static final String TEST_FIELD_NAME = "testFieldName";
	private static final String TEST_TYPE = "java.lang.String";

	@Before
	public void setUp()
	{
		final Map<String, String> typeMappings = new HashMap<>();
		typeMappings.put("TEXT", TEST_TYPE);
		typeMappings.put("BOOLEAN", "java.lang.Boolean");

		searchServicesStrategy.setTypeMappings(typeMappings);
	}

	@Test
	public void checkGetFieldTypeAndIsLocalized() throws Exception
	{
		final SnIndexTypeModel indexTypeModel = createIndexTypeModel();

		Mockito.when(backofficeFacetSearchConfigService.getIndexedTypeModel(TEST_TYPE_CODE)).thenReturn(indexTypeModel);

		assertThat(searchServicesStrategy.getFieldType(TEST_TYPE_CODE, TEST_FIELD_NAME)).isEqualTo(TEST_TYPE);
		assertThat(searchServicesStrategy.isLocalized(TEST_TYPE_CODE, TEST_FIELD_NAME)).isEqualTo(true);
	}

	private SnIndexTypeModel createIndexTypeModel()
	{
		final SnIndexTypeModel indexTypeModel = mock(SnIndexTypeModel.class);
		final SnFieldModel snFieldModel = mock(SnFieldModel.class);

		final List<SnFieldModel> fieldModelList = new ArrayList<>();
		fieldModelList.add(snFieldModel);

		when(snFieldModel.getId()).thenReturn(TEST_FIELD_NAME);
		when(snFieldModel.getFieldType()).thenReturn(SnFieldType.TEXT);
		when(snFieldModel.getLocalized()).thenReturn(true);

		when(indexTypeModel.getFields()).thenReturn(fieldModelList);

		return indexTypeModel;
	}
}
