/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BooleanSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private BooleanSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	static final String FIELD_ID = "field_id";

	@Test
	public void shouldGetTrueFieldValue() throws SnIndexerException, SnException
	{
		//give
		List<ItemModel> value = Collections.emptyList();
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_ID);

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(true);
	}

	@Test
	public void shouldGetFalseFieldValue() throws SnIndexerException, SnException
	{
		//give
		List<ItemModel> value = new ArrayList<>();
		ItemModel item = mock(ItemModel.class);
		value.add(item);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_ID);

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(false);
	}


	@Test
	public void shouldGetNotBooleanFieldValue() throws SnIndexerException, SnException
	{
		//give
		ItemModel value = mock(ItemModel.class);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_ID);

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(value);
	}

}
