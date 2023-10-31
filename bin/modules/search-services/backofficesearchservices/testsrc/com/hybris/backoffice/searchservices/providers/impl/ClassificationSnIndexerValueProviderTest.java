/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private ClassificationSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private ItemModel item;

	@Test
	public void shouldGetFieldValue() throws SnIndexerException, SnException
	{
		//give
		List<ItemModel> value = new ArrayList<>();
		value.add(item);
		value.add(item);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn("field_id");

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, null);
		assertThat(fieldValue instanceof List).isEqualTo(true);
		final List fieldValueList = (List) fieldValue;
		assertThat(fieldValueList.size()).isEqualTo(1);
		assertThat(fieldValueList.get(0)).isEqualTo(item);
	}

	@Test
	public void shouldGetNotCollectionFieldValue() throws SnIndexerException, SnException
	{
		//give
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(item);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn("field_id");

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, null);
		assertThat(fieldValue).isEqualTo(item);
	}


}
