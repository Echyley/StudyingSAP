/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.proxy.LabelServiceProxy;


@RunWith(MockitoJUnitRunner.class)
public class ItemModelLabelSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@Mock
	private LabelServiceProxy labelServiceProxy;

	@InjectMocks
	private ItemModelLabelSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Test
	public void shouldGetEmptyWhenQualifiersNotExist() throws SnIndexerException, SnException
	{
		//give
		final Object object = new Object();

		when(snExpressionEvaluator.evaluate(source, "")).thenReturn(object);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(Collections.emptyMap());
		when(fieldWrapper.getQualifiers()).thenReturn(Collections.emptyList());
		when(fieldWrapper.isLocalized()).thenReturn(true);

		//then
		final Map<Locale, Object> localizedValue = (Map<Locale, Object>) provider.getFieldValue(indexerContext, fieldWrapper,
				source, null);
		assertThat(localizedValue.isEmpty()).isEqualTo(true);
	}

	@Test
	public void shouldGetProductLabelWhenProviderParametersNotExist() throws SnIndexerException, SnException
	{
		//give
		final Object object = new Object();
		final String label = "productLabel";
		final SnQualifier qualifier = mock(SnQualifier.class);
		final Locale locale = new Locale("en");
		final List<SnQualifier> qualifiers = new ArrayList<>(Arrays.asList(qualifier));

		when(fieldWrapper.getValueProviderParameters()).thenReturn(Collections.emptyMap());
		when(snExpressionEvaluator.evaluate(source, "")).thenReturn(object);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(qualifier.getAs(Locale.class)).thenReturn(locale);
		when(labelServiceProxy.getObjectLabel(object, locale)).thenReturn(label);

		//then
		final Map<Locale, Object> localizedValue = (Map<Locale, Object>) provider.getFieldValue(indexerContext, fieldWrapper,
				source, null);

		assertThat(localizedValue.isEmpty()).isEqualTo(false);
		assertThat(localizedValue.size()).isEqualTo(1);
		assertThat(localizedValue.get(locale)).isEqualTo(label);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValueAndNotLocalized() throws SnException
	{
		//give
		final Object object = new Object();
		final String label = "productLabel";
		final SnQualifier qualifier = mock(SnQualifier.class);
		final Locale locale = new Locale("en");
		final List<SnQualifier> qualifiers = new ArrayList<>(Arrays.asList(qualifier));

		when(fieldWrapper.getValueProviderParameters()).thenReturn(Collections.emptyMap());
		when(snExpressionEvaluator.evaluate(source, "")).thenReturn(object);
		Mockito.lenient().when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(fieldWrapper.isLocalized()).thenReturn(false);
		Mockito.lenient().when(qualifier.getAs(Locale.class)).thenReturn(locale);
		Mockito.lenient().when(labelServiceProxy.getObjectLabel(object, locale)).thenReturn(label);

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, null);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValueCatchSnException() throws SnException
	{
		//give
		final Object object = new Object();
		final String label = "productLabel";
		final SnQualifier qualifier = mock(SnQualifier.class);
		final Locale locale = new Locale("en");
		final List<SnQualifier> qualifiers = new ArrayList<>(Arrays.asList(qualifier));

		when(fieldWrapper.getValueProviderParameters()).thenReturn(Collections.emptyMap());
		given(snExpressionEvaluator.evaluate(source, "")).willThrow(new SnException());
		Mockito.lenient().when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		Mockito.lenient().when(fieldWrapper.isLocalized()).thenReturn(false);
		Mockito.lenient().when(qualifier.getAs(Locale.class)).thenReturn(locale);
		Mockito.lenient().when(labelServiceProxy.getObjectLabel(object, locale)).thenReturn(label);

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, null);
	}

	@Test
	public void shouldGetItemLabelWhenProviderParametersExist() throws SnIndexerException, SnException
	{
		//give
		final Object object = new Object();
		final Map<String, String> valueProviderParameters = new HashMap<>();
		final String parameter = "catalogVersion";
		final String label = "catalogVersionLabel";
		final SnQualifier qualifier = mock(SnQualifier.class);
		final Locale locale = new Locale("en");
		final List<SnQualifier> qualifiers = new ArrayList<>(Arrays.asList(qualifier));

		valueProviderParameters.put(ItemModelLabelSnIndexerValueProvider.EXPRESSION_PARAM, parameter);

		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(snExpressionEvaluator.evaluate(source, parameter)).thenReturn(object);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(qualifier.getAs(Locale.class)).thenReturn(locale);
		when(labelServiceProxy.getObjectLabel(object, locale)).thenReturn(label);

		//then
		final Map<Locale, Object> localizedValue = (Map<Locale, Object>) provider.getFieldValue(indexerContext, fieldWrapper,
				source, null);

		assertThat(localizedValue.isEmpty()).isEqualTo(false);
		assertThat(localizedValue.size()).isEqualTo(1);
		assertThat(localizedValue.get(locale)).isEqualTo(label);
	}

	@Test
	public void shouldNotGetItemLabelWhenLabelEmpty() throws SnIndexerException, SnException
	{
		//give
		final Object object = new Object();
		final Map<String, String> valueProviderParameters = new HashMap<>();
		final String parameter = "catalogVersion";
		final String label = "";
		final SnQualifier qualifier = mock(SnQualifier.class);
		final Locale locale = new Locale("en");
		final List<SnQualifier> qualifiers = new ArrayList<>(Arrays.asList(qualifier));

		valueProviderParameters.put(ItemModelLabelSnIndexerValueProvider.EXPRESSION_PARAM, parameter);

		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(snExpressionEvaluator.evaluate(source, parameter)).thenReturn(object);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(qualifier.getAs(Locale.class)).thenReturn(locale);
		when(labelServiceProxy.getObjectLabel(object, locale)).thenReturn(label);

		//then
		final Map<Locale, Object> localizedValue = (Map<Locale, Object>) provider.getFieldValue(indexerContext, fieldWrapper,
				source, null);

		assertThat(localizedValue.isEmpty()).isEqualTo(true);
	}

}
