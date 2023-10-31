/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DateSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private DateSnIndexerValueProvider provider;

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
		final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date value = new Date();
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn("field_id");

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(dateFormat.format(value));
	}

	@Test
	public void shouldGetNotDateFieldValue() throws SnIndexerException, SnException
	{
		//give
		ItemModel value = mock(ItemModel.class);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(new HashMap<>());
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn("field_id");

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(value);
	}

}
